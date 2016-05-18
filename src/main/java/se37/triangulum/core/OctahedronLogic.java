package se37.triangulum.core;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;

import se37.triangulum.Triangulum;
import se37.triangulum.packets.SPacketNetUpdate;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class OctahedronLogic extends TileEntity implements ITickable {

	/** The voltage across the leads of this capacitor. */
	private float voltage;
	/**
	 * The relative positions of the octahedra that this one is connected to, in
	 * D-U-N-S-W-E order. 0 means no connection.
	 */
	private int[] connections;
	/**
	 * The current going OUT of this octahedron INTO the octahedron in the
	 * corresponding direction, again in D-U-N-S-W-E order. Zero means no
	 * connection.
	 */
	private float[] currents;
	/**
	 * The directions in which this octahedron connects to octahedra in loaded
	 * chunks, in S-W-N-E order. Current calculations are to be skipped in
	 * directions there this is false. Undefined in directions where a
	 * connection is nonexistent.
	 */
	private boolean[] loadedDirs;
	private float nextVoltage;
	private int counter;

	public OctahedronLogic() {
		super();
		voltage = 0;
		connections = new int[EnumFacing.VALUES.length];
		currents = new float[EnumFacing.VALUES.length];
		loadedDirs = new boolean[EnumFacing.HORIZONTALS.length];
		nextVoltage = voltage;
		counter = pos.getX() + pos.getY() << 2 + pos.getZ() << 4;
	}

	public float getVoltage() {
		return voltage;
	}

	public void setVoltage(float v) {
		voltage = v;
	}

	public float getCurrent(EnumFacing e) {
		return currents[e.getIndex()];
	}

	public void setCurrent(EnumFacing e, float i) {
		currents[e.getIndex()] = i;
	}

	public int getDistance(EnumFacing e) {
		return connections[e.getIndex()];
	}

	@Override
	public void writeToNBT(NBTTagCompound root) {
		super.writeToNBT(root);
		root.setFloat("Voltage", voltage);
		root.setIntArray("Connections", connections);

		NBTTagList list = new NBTTagList();
		for (EnumFacing e : EnumFacing.VALUES) {
			list.appendTag(new NBTTagFloat(currents[e.getIndex()]));
		}
		root.setTag("Currents", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound root) {
		super.readFromNBT(root);
		this.voltage = root.getFloat("Voltage");
		this.connections = root.getIntArray("Connections");

		this.currents = new float[EnumFacing.VALUES.length];
		NBTTagList list = root.getTagList("Currents", 5); // Why the heck is
															// that 5 not a
															// static constant
															// in the TagFloat
															// class?
		for (EnumFacing e : EnumFacing.VALUES) {
			currents[e.getIndex()] = list.getFloatAt(e.getIndex());
		}
	}

	/**
	 * Scans in all six directions up to this block's range for other Octahedra,
	 * and updates this Octahedron's state as necessary.
	 * 
	 * @return true if anything to be saved to NBT was updated
	 */
	private boolean scan() {
		boolean dirtied = false;
		Octahedron t = null;
		if (getBlockType() instanceof Octahedron) {
			t = (Octahedron) blockType;
		} else {
			LogManager.getLogger().error(
					blockType + " at " + pos
							+ "is not an Octahedron! Scan aborted!");
			return dirtied;
		}

		for (EnumFacing e : EnumFacing.VALUES) {
			boolean connected = false;
			for (int i = 1; i <= t.getRange(); i++) {
				// determine if we need to check that the block is loaded
				boolean needsCheck = e.getHorizontalIndex() >= 0;
				if (!needsCheck) {
					int coord = 0;
					if (e.getAxis() == EnumFacing.Axis.X) {
						coord = pos.getX();
					} else if (e.getAxis() == EnumFacing.Axis.Y) {
						coord = pos.getY();
					}
					if (e.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
						coord += i;
					} else {
						coord = coord - i + 1;
					}
					needsCheck = coord % 16 == 0;
				}

				if (!needsCheck || worldObj.isBlockLoaded(pos.offset(e, i))) {
					Block b = worldObj.getBlockState(pos.offset(e, i))
							.getBlock();
					if (b instanceof Octahedron
							&& i <= ((Octahedron) b).getRange()) {
						if (connections[e.getIndex()] != i) {
							connections[e.getIndex()] = i;
							dirtied = true;
						}
						if (e.getAxis() != EnumFacing.Axis.Y) {
							loadedDirs[e.getHorizontalIndex()] = true;
						}
						connected = true;
						break;
					}
				} else {
					if (e.getAxis() != EnumFacing.Axis.Y) {
						loadedDirs[e.getHorizontalIndex()] = false;
					}
					// if scan hit an unloaded chunk, skip updating the
					// connection in this direction
					connected = true;
					break;
				}
			}
			// If the scan ran out of range or hit an obstacle (NYI), but didn't
			// do so last time
			if (!connected && connections[e.getIndex()] != 0) {
				connections[e.getIndex()] = 0;
				setCurrent(e, 0);
				dirtied = true;
			}
		}
		return dirtied;
	}

	@Override
	public void update() {
		boolean dirtied = false;

		counter++;
		if (counter >= 20 * 4) {
			counter = 0;
			dirtied |= scan();
			if (!worldObj.isRemote) {
				Triangulum.networkWrapper.sendToAllAround(new SPacketNetUpdate(
						voltage, currents, pos), new TargetPoint(worldObj.provider.getDimension(),
						pos.getX(), pos.getY(), pos.getZ(), 20));
			}
		}

		
		Octahedron t = null;
		if (getBlockType() instanceof Octahedron) {
			t = (Octahedron) blockType;
		} else {
			LogManager.getLogger().error(
					blockType + " at " + pos
							+ " is not an Octahedron! Update aborted!");
			return;
		}

		nextVoltage = voltage;
		for (EnumFacing e : EnumFacing.VALUES) {
			if (connections[e.getIndex()] > 0
					&& (e.getHorizontalIndex() < 0 || loadedDirs[e
							.getHorizontalIndex()])) {

				Octahedron o = null;
				Block b = worldObj.getBlockState(
						pos.offset(e, connections[e.getIndex()])).getBlock();
				OctahedronLogic ol = null;
				TileEntity te = worldObj.getTileEntity(pos.offset(e,
						connections[e.getIndex()]));

				if (b instanceof Octahedron && te instanceof OctahedronLogic) {
					o = (Octahedron) b;
					ol = (OctahedronLogic) te;
				} else { // if there was no octahedron at the cached
							// location,
							// rescan and skip this iteration
					dirtied |= scan();
					continue;
				}

				// updates nextVoltage based on current state
				dirtied |= updatePower(e, o, ol);

				double x = pos.getX() + 0.5;
				double y = pos.getY() + 0.5;
				double z = pos.getZ() + 0.5;

				if (getCurrent(e) > 0) {
					// color of particles displays voltage:
					// . -64V ... -16V ... -4V ... 0V ... 4V ... 16V ... 64V
					// . yellow .. red .. mgnta . blue . cyan . green .
					// yellow
					// r . ******************-------_______________--------*
					// g . *--------________________-------*****************
					// b . __________-------***************-------__________
					// (code moved to private methods for ease of use)
					// brightness of sparkles displays current
					float a = Math
							.min(1, (float) (Math.log1p(getCurrent(e)) / (Math
									.log(2) * 20)));

					spawnSparkleRing(worldObj, x, y, z,
							redChannel(voltage) * a, greenChannel(voltage) * a,
							blueChannel(voltage) * a, e, 4,
							connections[e.getIndex()], 20, 0.4F, 1.0F,
							redChannel(-ol.getVoltage()) * a,
							greenChannel(-ol.getVoltage()) * a,
							blueChannel(-ol.getVoltage()) * a, 0.5F);
				}
			}
		}
		voltage = nextVoltage;

		if (Math.abs(voltage) > t.getMaxVoltage()) {
			// Kaboom! Maybe...
			Triangulum.proxy.wispFX(worldObj, pos.getX() + 0.5,
					pos.getY() + 0.5, pos.getZ() + 0.5, 0.2F, 1.0F, 1.0F, 2F,
					0.25F);
			voltage = 0;
			dirtied = true;
		}

		// dirtied = true;
		if (dirtied) {
			this.markDirty();
		}
	}

	private float redChannel(float voltage) {
		return voltage <= 0 ? normalizeRange(0, -4, voltage) : normalizeRange(
				16, 64, voltage);
	}

	private float greenChannel(float voltage) {
		return voltage <= 0 ? normalizeRange(-16, -64, voltage)
				: normalizeRange(0, 4, voltage);
	}

	private float blueChannel(float voltage) {
		return voltage <= 0 ? normalizeRange(-16, -4, voltage)
				: normalizeRange(16, 4, voltage);
	}

	/**
	 * Takes the whole number line and squishes it so that the range [min, max]
	 * maps onto the range [0, 1]. Returns the value of val after this squishing
	 * has taken place, clamped into the range [0, 1]. Works fine if min > max-
	 * this just flips the number line around.
	 * 
	 * @param min
	 *            the number that maps to 0
	 * @param max
	 *            the number that maps to 1
	 * @param val
	 *            the number to apply the mapping to
	 * @return
	 */
	private float normalizeRange(float min, float max, float val) {
		if ((val > max && max > min) || (val < max && max < min)) {
			return 1;
		} else if ((val < min && min < max) || (val > min && min > max)) {
			return 0;
		}
		return (val - min) / (max - min);
	}

	private boolean updatePower(EnumFacing e, Octahedron o, OctahedronLogic ol) {
		Octahedron t = (Octahedron) blockType;
		boolean dirtied = false;

		int d = connections[e.getIndex()]; // meters
		float dt = 1 / 40F; // seconds

		float v1 = this.voltage; // volts
		float c1 = t.getCapacitance(); // farads
		if (!Float.isFinite(v1)) {
			v1 = 0;
		}

		float v2 = ol.getVoltage(); // volts
		float c2 = o.getCapacitance(); // farads
		if (!Float.isFinite(v2)) {
			v2 = 0;
		}

		float r = d * (t.getResistivity() + o.getResistivity()) / 2; // ohms
		float l = d * (t.getInductivity() + o.getInductivity()) / 2; // henries
		float i = (getCurrent(e) - ol.getCurrent(e.getOpposite())) / 2; // amperes
		if (!Float.isFinite(i)) {
			i = 0;
		}

		/**
		 * The following formulas were derived from these equations: <br>
		 * V = IR (Ohm's Law) <br>
		 * V = L dI/dt (Inductor law) <br>
		 * C = Q / V (Capacitor law) <br>
		 * Q = int I dt (Capacitor charging law)
		 */

		float di = (v1 - v2 - i * r) / l;
		float dv1 = -i / c1;
		float dv2 = i / c2;

		if (!Float.isFinite(di)) {
			di = 0;
		}

		setCurrent(e, i + di);
		ol.setCurrent(e.getOpposite(), -i - di);

		nextVoltage += dv1 * dt;
		ol.setVoltage(v2 + dv2 * dt);

		dirtied |= di != 0 || dv1 != 0;

		return dirtied;
	}

	/**
	 * Spawns a ring of sparkles at the designated location moving in the given
	 * direction at the given speed, set to despawn when they've traveled the
	 * given distance. Also spawns a wisp where the sparkles despawn with its
	 * own (given) color and size, set to despawn where the sparkles spawned.
	 * 
	 * @param world
	 *            the world to spawn these particles in
	 * @param x
	 *            x coordinate of the wisp and the center of the ring
	 * @param y
	 *            y coordinate of the wisp and the center of the ring
	 * @param z
	 *            z coordinate of the wisp and the center of the ring
	 * @param r
	 *            red component of sparkles' color
	 * @param g
	 *            green component of sparkles' color
	 * @param b
	 *            blue component of sparkles' color
	 * @param e
	 *            direction to send particles
	 * @param speed
	 *            speed of the particles
	 * @param dist
	 *            distance particles should travel before despawning
	 * @param n
	 *            number of particles to spawn in the ring
	 * @param radius
	 *            radius of the ring
	 * @param pSize
	 *            size of the sparkles
	 * @param wr
	 *            red component of wisp's color
	 * @param wg
	 *            green component of wisp's color
	 * @param wb
	 *            blue component of wisp's color
	 * @param wSize
	 *            maximum size of the wisp
	 */
	private void spawnSparkleRing(World world, double x, double y, double z,
			float r, float g, float b, EnumFacing e, float speed, float dist,
			int n, float radius, float pSize, float wr, float wg, float wb,
			float wSize) {
		float vx = e.getDirectionVec().getX();
		float vy = e.getDirectionVec().getY();
		float vz = e.getDirectionVec().getZ();

		float ageMul = dist / speed;

		double dAngle = 2.0 * Math.PI / n;
		double startAngle = 2.0 * Math.PI * Math.random();
		for (int i = 0; i < n; i++) {
			double u = Math.cos(startAngle + i * dAngle) * radius;
			double v = 0;
			double w = Math.sin(startAngle + i * dAngle) * radius;

			if (e.getAxis() == EnumFacing.Axis.X) {
				v = u;
				u = 0;
			} else if (e.getAxis() == EnumFacing.Axis.Z) {
				v = w;
				w = 0;
			}

			Triangulum.proxy.sparkleFX(worldObj, x + u, y + v, z + w, r, g, b,
					vx * speed, vy * speed, vz * speed, pSize, ageMul);
		}
		Triangulum.proxy.wispFX(worldObj, x + vx * dist, y + vy * dist, z + vz
				* dist, wr, wg, wb, vx * -speed, vy * -speed, vz * -speed,
				wSize, ageMul);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound root = new NBTTagCompound();
		writeToNBT(root);
		return new SPacketUpdateTileEntity(pos, 0, root);
	}

	@Override
	public void onDataPacket(NetworkManager manager,
			SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	public void onDataPacket(SPacketNetUpdate message) {
		voltage = message.getVoltage();
		currents = Arrays.copyOf(message.getCurrents(), 6);
	}
}
