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
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new OctahedronLogic();
	}

}
