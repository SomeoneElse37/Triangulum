package se37.triangulum.machine;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BrickFurnace extends MachineBase {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyInteger TEMPERATURE = PropertyInteger.create(
			"temperature", 0, 3);

	public BrickFurnace(Material materialIn) {
		super(materialIn);
		setCreativeTab(CreativeTabs.tabBlock);
		setHardness(2.0F);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		setDefaultFacing(worldIn, pos, state);
	}

	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			IBlockState northState = worldIn.getBlockState(pos.north());
			IBlockState southState = worldIn.getBlockState(pos.south());
			IBlockState westState = worldIn.getBlockState(pos.west());
			IBlockState eastState = worldIn.getBlockState(pos.east());
			EnumFacing facing = state.getValue(FACING);

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

			worldIn.setBlockState(pos, state.withProperty(FACING, facing)
					.withProperty(TEMPERATURE, 0), 2);
		}
	}

	/**
	 * Called by ItemBlocks after a block is set in the world, to allow
	 * post-place logic
	 */
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer
				.getHorizontalFacing().getOpposite()), 2);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof BrickFurnaceLogic) {
				InventoryHelper.dropInventoryItems(worldIn, pos,
						(BrickFurnaceLogic) tileentity);
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return getDefaultState().withProperty(FACING, enumfacing).withProperty(
				TEMPERATURE, meta >> 2);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = state.getValue(FACING).getHorizontalIndex();
		meta = meta | state.getValue(TEMPERATURE) << 2;
		return meta;
	}

	/**
	 * Returns the blockstate with the given rotation from the passed
	 * blockstate. If inapplicable, returns the passed blockstate.
	 */
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 */
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return (new BlockStateContainer.Builder(this)).add(FACING)
				.add(TEMPERATURE).build();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new BrickFurnaceLogic();
	}

	@Override
	public EnumFacing getPowerFace(IBlockState state) {
		return EnumFacing.UP;
	}

}
