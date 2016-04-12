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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Octahedron extends BlockContainer {
	
	protected static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);

	private float capacitance = 1;	//farads
	private float maxVoltage = 1;	//volts
	private float resistivity = 1;	//ohms per meter (averaged with resistivity of connecting octahedron)
	private float inductivity = 1;	//henries per meter
	private float range = 1;		//meters

	public Octahedron(Material material) {
		super(material);
		setCreativeTab(CreativeTabs.tabBlock);
		setHardness(2.0F);
	};

	public Octahedron(Material material, float c, float v, float r, float i, float d) {
		this(material);
		capacitance = c;
		maxVoltage = v;
		resistivity = r;
		inductivity = i;
		range = d;
	};

	public float getCapacitance() {
		return capacitance;
	}

	public float getMaxVoltage() {
		return maxVoltage;
	}

	public float getResistivity() {
		return resistivity;
	}

	public float getInductivity() {
		return inductivity;
	}

	public float getRange() {
		return range;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX;
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
	public void randomDisplayTick(IBlockState p, World world, BlockPos pos,
			Random random) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;

		float vx = 0.0f;
		float vy = 0.2f;
		float vz = 0.0f;

		//spawn n particles in a ring
		int n = 20;
		double dAngle = 2.0 * Math.PI / n;
		double startAngle = 2.0 * Math.PI * random.nextDouble();
		for (int i = 0; i < n; i++) {
			Triangulum.proxy.sparkleFX(world,
					x + 0.4D * Math.cos(startAngle + i * dAngle), y,
					z + 0.4D * Math.sin(startAngle + i * dAngle), 1.0F, 0.5F,
					0.0F, vx, vy, vz, 1.0F, 1.0F);

			// par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, dx
			// + (0.4D * Math.cos(angle)), dy
			// + (random.nextDouble() * 1.0F),
			// dz + (0.4D * Math.sin(angle)), 0.0D, 0.1D, 0.0D);
		}
		//spawn particle at center of ring
		Triangulum.proxy.wispFX(world, x, y, z, 1.0F, 0.0F, 0.0F, vx, vy, vz, 0.5F, 1.0F);

	//	for (int i = 0; i < 5; i++) {
	//		double angle = 2.0D * Math.PI * random.nextDouble();

			// par1World.spawnParticle(EnumParticleTypes.FLAME,
			// dx + (0.2D * Math.cos(angle)), dy
			// + (random.nextDouble() * 1.0F),
			// dz + (0.2D * Math.sin(angle)), 0.0D, 0.1D, 0.0D);
	//	}
		
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new OctahedronLogic();
	}

}
