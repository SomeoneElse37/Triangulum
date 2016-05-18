package se37.triangulum.powergen;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import se37.triangulum.machine.MachineBase;

public class GeneratorCoal extends MachineBase {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool RUNNING = PropertyBool.create("burning");

	public GeneratorCoal(Material materialIn) {
		super(materialIn);
		setCreativeTab(CreativeTabs.tabBlock);
		setHardness(2.0F);
	}

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.setDefaultFacing(worldIn, pos, state);
	}

	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			IBlockState northState = worldIn.getBlockState(pos.north());
			IBlockState southState = worldIn.getBlockState(pos.south());
			IBlockState westState = worldIn.getBlockState(pos.west());
			IBlockState eastState = worldIn.getBlockState(pos.east());
			EnumFacing facing = (EnumFacing) state.getValue(FACING);

			if (facing == EnumFacing.NORTH && northState.isFullBlock()
					&& !southState.isFullBlock()) {
				facing = EnumFacing.SOUTH;
			} else if (facing == EnumFacing.SOUTH && southState.isFullBlock()
					&& !northState.isFullBlock()) {
				facing = EnumFacing.NORTH;
			} else if (facing == EnumFacing.WEST && westState.isFullBlock()
					&& !eastState.isFullBlock()) {
				facing = EnumFacing.EAST;
			} else if (facing == EnumFacing.EAST && eastState.isFullBlock()
					&& !westState.isFullBlock()) {
				facing = EnumFacing.WEST;
			}

			worldIn.setBlockState(pos, state.withProperty(FACING, facing).withProperty(RUNNING, false), 2);
		}
	}

	/**
	 * Called by ItemBlocks after a block is set in the world, to allow
	 * post-place logic
	 */
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer
				.getHorizontalFacing().getOpposite()), 2);
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof GeneratorCoalLogic) {
				InventoryHelper.dropInventoryItems(worldIn, pos,
						(GeneratorCoalLogic) tileentity);
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(RUNNING, meta <= 8);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		int meta = ((EnumFacing) state.getValue(FACING)).getIndex();
		if(state.getValue(RUNNING)) {
			meta += 8;
		}
		return meta;
	}

	/**
	 * Returns the blockstate with the given rotation from the passed
	 * blockstate. If inapplicable, returns the passed blockstate.
	 */
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING,
				rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 */
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state
				.getValue(FACING)));
	}

	protected BlockStateContainer createBlockState() {
		return (new BlockStateContainer.Builder(this)).add(FACING).add(RUNNING).build();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new GeneratorCoalLogic();
	}

	@Override
	public EnumFacing getPowerFace(IBlockState state) {
		return EnumFacing.UP;
	}

}
