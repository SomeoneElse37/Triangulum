package se37.triangulum.powergen;

import se37.triangulum.core.MachineBase;
import se37.triangulum.core.Octahedron;
import se37.triangulum.core.OctahedronLogic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public abstract class GeneratorLogicBase extends TileEntity implements
		ITickable {

	public GeneratorLogicBase() {
		super();
	}

	/**
	 * The generator's power curve. Returns the power (in watts) that the
	 * generator should be producing right now, based on its current state and
	 * the given voltage stored in its attached Octahedron.
	 * 
	 * This method should have no side effects.
	 * 
	 * @param voltage
	 *            the voltage this generator must work against
	 * @return the power (in watts) that this generator is producing right now
	 */
	public abstract float powerCurve(float voltage);

	/**
	 * Updates the given Octahedron's voltage based on the amount of power this generator is
	 * pumping into it. Call this from the generator's update() method.
	 */
	public void update(Octahedron o, OctahedronLogic ol) {
		/**
		 * E = 1/2 C V^2 <br>
		 * V^2 = 2E / C <br>
		 * V(E) = sqrt(2E / C) <br>
		 * dE = P dt <br>
		 * V(E + dE) = sqrt(2(E + dE) / C) <br>
		 * V(E + dE) = sqrt(2(1/2 C V^2 + P dt) / C)
		 */
		float v = ol.getVoltage();
		float e = o.getCapacitance() * v * v / 2 + powerCurve(v) / 20;
		ol.setVoltage((float) Math.sqrt(2 * e / o.getCapacitance()));
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
