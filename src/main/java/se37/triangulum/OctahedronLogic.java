package se37.triangulum;

import org.apache.logging.log4j.LogManager;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class OctahedronLogic extends TileEntity implements ITickable {

	public OctahedronLogic() {
		voltage = 0;
		connections = new int[EnumFacing.VALUES.length];
		currents = new float[EnumFacing.VALUES.length];
		loadedDirs = new boolean[EnumFacing.HORIZONTALS.length];
	}

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
	 * connection is nonexistant.
	 */
	private boolean[] loadedDirs;

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
			LogManager
					.getLogger()
					.error(blockType
							+ " at "
							+ pos
							+ "is not an Octahedron! Scan likely to fail catastrophically!");
			// Is this when I invalidate the TE?
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
			// last time
			if (!connected && connections[e.getIndex()] != 0) {
				connections[e.getIndex()] = 0;
				dirtied = true;
			}
		}
		return dirtied;
	}

	@Override
	public void update() {
		boolean dirtied = false;
		if ((worldObj.getWorldTime() + pos.getX() + pos.getY() << 2 + pos
				.getZ() << 4) % (20 * 4) == 0) {
			dirtied |= scan();
		}

		Octahedron t = null;
		if (getBlockType() instanceof Octahedron) {
			t = (Octahedron) blockType;
		} else {
			LogManager.getLogger().error(
					blockType + " at " + pos
							+ " is not an Octahedron! Stuff's broke!");
			// This will probably lead to a crash later on.
		}

		for (EnumFacing e : EnumFacing.VALUES) {
			if (connections[e.getIndex()] > 0
					&& (e.getHorizontalIndex() < 0 || loadedDirs[e
							.getHorizontalIndex()])) {

				Octahedron o = null;
				Block b = worldObj.getBlockState(
						pos.offset(e, connections[e.getIndex()])).getBlock();
				if (b instanceof Octahedron) {
					o = (Octahedron) b;
				} else { // if there was no octahedron at the cached location,
							// rescan and skip this iteration
					dirtied |= scan();
					continue;
				}

				double x = pos.getX() + 0.5;
				double y = pos.getY() + 0.5;
				double z = pos.getZ() + 0.5;

				float speed = 4;

				float vx = e.getDirectionVec().getX() * speed;
				float vy = e.getDirectionVec().getY() * speed;
				float vz = e.getDirectionVec().getZ() * speed;

				float ageMul = connections[e.getIndex()] / speed;

				// spawn n particles in a ring
				int n = 20;
				double dAngle = 2.0 * Math.PI / n;
				double startAngle = 2.0 * Math.PI * Math.random();
				for (int i = 0; i < n; i++) {
					double u = Math.cos(startAngle + i * dAngle) * 0.4;
					double v = 0;
					double w = Math.sin(startAngle + i * dAngle) * 0.4;

					if (e.getAxis() == EnumFacing.Axis.X) {
						v = u;
						u = 0;
					} else if (e.getAxis() == EnumFacing.Axis.Z) {
						v = w;
						w = 0;
					}

					Triangulum.proxy.sparkleFX(worldObj, x + u, y + v, z + w,
							0.1F, 0.8F, 1.0F, vx, vy, vz, 1.0F, ageMul);
				}
				// spawn particle at center of ring
				Triangulum.proxy.wispFX(worldObj, x, y, z, 0.4F, 0.0F, 1.0F,
						vx, vy, vz, 0.5F, ageMul);

			}
		}
		if (dirtied) {
			this.markDirty();
		}
	}
}
