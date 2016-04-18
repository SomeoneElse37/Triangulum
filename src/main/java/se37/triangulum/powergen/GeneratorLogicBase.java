package se37.triangulum.powergen;

import se37.triangulum.core.MachineBase;
import se37.triangulum.core.Octahedron;
import se37.triangulum.core.OctahedronLogic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public abstract class GeneratorLogicBase extends TileEntity implements
		ITickable {
	protected boolean running;

	public GeneratorLogicBase() {
		super();
		running = false;
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
	 * Returns whether the generator is running or not. Used to short-circuit
	 * the power code if false.
	 * 
	 * @return true if this generator is producing power, false if not
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * This method does not do any null or type checking on the blockType field.
	 * Be sure to refresh it with getBlockType() before calling this method!
	 */
	@Override
	public void update() {
		if (isRunning()) {
			IBlockState state = worldObj.getBlockState(pos);
			MachineBase g = (MachineBase) blockType;
			Block b = worldObj.getBlockState(pos.offset(g.getPowerFace(state)))
					.getBlock();
			TileEntity te = worldObj
					.getTileEntity(pos.offset(g.getPowerFace(state)));
			if (b instanceof Octahedron && te instanceof OctahedronLogic) {
				Octahedron o = (Octahedron) b;
				OctahedronLogic ol = (OctahedronLogic) te;
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
		}
	}
}
