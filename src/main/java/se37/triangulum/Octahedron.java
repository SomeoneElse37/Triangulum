package se37.triangulum;

import java.util.List;
import java.util.Random;
import java.lang.Math;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Octahedron extends BlockContainer {

	public int capacitance = 1;
	public int maxVoltage = 1;

	public Octahedron(Material material) {
		super(material);
	};

	public Octahedron(Material material, int cap, int volt) {
		super(material);
		this.capacitance = cap;
		this.maxVoltage = volt;

	};

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	public void randomDisplayTick(IBlockState p, World par1World,
			BlockPos state, Random random) {
		double dx = state.getX() + 0.5F;
		double dy = state.getY() + 0.5F;
		double dz = state.getZ() + 0.5F;

		for (int i = 0; i < 20; i++) {
			double angle = 2.0D * Math.PI * random.nextDouble();

			par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, dx
					+ (0.4D * Math.cos(angle)), dy
					+ (random.nextDouble() * 1.0F),
					dz + (0.4D * Math.sin(angle)), 0.0D, 0.1D, 0.0D);
		}
		;

		for (int i = 0; i < 5; i++) {
			double angle = 2.0D * Math.PI * random.nextDouble();

			par1World.spawnParticle(EnumParticleTypes.FLAME,
					dx + (0.2D * Math.cos(angle)), dy
							+ (random.nextDouble() * 1.0F),
					dz + (0.2D * Math.sin(angle)), 0.0D, 0.1D, 0.0D);
		}
		;
	};

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new OctahedronLogic();
	}

}
