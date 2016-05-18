package se37.triangulum.machine;

import se37.triangulum.core.Octahedron;
import se37.triangulum.core.OctahedronLogic;
import se37.triangulum.machine.MachineBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public abstract class MachineLogicBase extends TileEntity implements ITickable {

	public MachineLogicBase() {
		super();
	}

	/**
	 * The generator's resistance. May depend on the generator's internal state,
	 * as well as on the voltage across its terminals, if a blatantly non-ohmic
	 * resistor is desired.
	 * 
	 * R = V/I for constant current <br>
	 * R = V^2/P for constant power
	 * 
	 * This method should have no side effects.
	 * 
	 * @param voltage
	 *            the voltage powering this machine
	 * @return the resistance of this machine
	 */
	public abstract float resistance(float voltage);

	/**
	 * Updates the given Octahedron's voltage based on the amount of power this
	 * generator is pumping into it. Call this from the generator's update()
	 * method.
	 * 
	 * @return the power the machine is consuming
	 */
	public float update(Octahedron o, OctahedronLogic ol) {
		/**
		 * P = IV <br>
		 * V = IR <br>
		 * I = V/R <br>
		 * P = V^2 / R <br>
		 * <br>
		 * E = 1/2 C V^2 <br>
		 * V^2 = 2E / C <br>
		 * V(E) = sqrt(2E / C) <br>
		 * dE = P dt <br>
		 * V(E + dE) = sqrt(2(E + dE) / C) <br>
		 * V(E + dE) = sqrt(2(1/2 C V^2 + P dt) / C)
		 */
		float v = ol.getVoltage();
		float p = v * v / resistance(v);
		float e = o.getCapacitance() * v * v / 2 - p / 20;
		ol.setVoltage((float) Math.sqrt(2 * e / o.getCapacitance()));
		return p;
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
}
