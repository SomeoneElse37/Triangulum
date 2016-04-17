package se37.triangulum.core;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class MachineBase extends BlockContainer {

	/**
	 * @param materialIn
	 */
	public MachineBase(Material materialIn) {
		super(materialIn);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param materialIn
	 * @param color
	 */
	public MachineBase(Material materialIn, MapColor color) {
		super(materialIn, color);
		// TODO Auto-generated constructor stub
	}
	
	public abstract EnumFacing getPowerFace(IBlockState state);

}
