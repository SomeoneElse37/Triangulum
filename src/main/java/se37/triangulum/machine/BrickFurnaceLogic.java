package se37.triangulum.machine;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import org.apache.logging.log4j.LogManager;

import se37.triangulum.core.Octahedron;
import se37.triangulum.core.OctahedronLogic;
import se37.triangulum.powergen.GeneratorCoal;

public class BrickFurnaceLogic extends MachineLogicBase implements
		ISidedInventory {
	public static float RESISTANCE = 0.5F;
	public static float ENERGY_PER_SMELT = 1;
	private ItemStack[] inv;
	private float progress;

	public BrickFurnaceLogic() {
		super();
		inv = new ItemStack[getSizeInventory()];
		progress = 0;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (inv[index] != null) {
			ItemStack tmp = null;
			if (count < inv[index].stackSize) {
				inv[index].stackSize -= count;
				tmp = inv[index].copy();
				tmp.stackSize = count;
			} else {
				tmp = inv[index];
				inv[index] = null;
			}
			return tmp;
		}
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack tmp = inv[index];
		inv[index] = null;
		return tmp;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// Nope
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// Nada
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index == 0;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? Float.floatToIntBits(progress) : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0) {
			progress = Float.intBitsToFloat(value);
		}
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		for (int i = 0; i < inv.length; i++) {
			inv[i] = null;
		}
	}

	@Override
	public String getName() {
		return "Brick Furnace";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("Coal Generator");
	}

	@Override
	public void update() {
		boolean dirtied = false;

		if (!(getBlockType() instanceof BrickFurnace)) {
			LogManager.getLogger().error(
					"Broken Brick Furnace at " + pos + "! Abort!");
			return;
		}

		float powerConsumed = 0;
		IBlockState state = worldObj.getBlockState(pos);
		MachineBase g = (MachineBase) blockType;
		Block b = worldObj.getBlockState(pos.offset(g.getPowerFace(state)))
				.getBlock();
		TileEntity te = worldObj
				.getTileEntity(pos.offset(g.getPowerFace(state)));
		if (b instanceof Octahedron && te instanceof OctahedronLogic) {
			Octahedron o = (Octahedron) b;
			OctahedronLogic ol = (OctahedronLogic) te;

			if (canSmelt()) {
				powerConsumed = update(o, ol);
				progress += powerConsumed / 20;
				if (progress > ENERGY_PER_SMELT) {
					progress -= ENERGY_PER_SMELT;
					ItemStack result = FurnaceRecipes.instance()
							.getSmeltingResult(inv[0]);
					if (inv[1] == null) {
						inv[1] = result;
					} else {
						inv[1].stackSize += result.stackSize;
					}
					decrStackSize(0, 1);
					dirtied = true;
				}
				dirtied |= powerConsumed == 0;
			}
		}

		if (state.getValue(BrickFurnace.TEMPERATURE) != getTemperatureTier(powerConsumed)) {
			worldObj.setBlockState(pos, state
					.withProperty(BrickFurnace.TEMPERATURE,
							getTemperatureTier(powerConsumed)), 2);
			this.validate();
			worldObj.setTileEntity(pos, this);
		}

		if (dirtied) {
			markDirty();
		}
	}

	private int getTemperatureTier(float power) {
		float time = ENERGY_PER_SMELT / power;
		if (time > 1 << 8) {
			return 0;
		} else if (time > 1 << 4) {
			return 1;
		} else if (time > 1) {
			return 2;
		} else {
			return 3;
		}
	}

	private boolean canSmelt() {
		if (inv[0] == null) {
			return false;
		} else {
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(
					inv[0]);
			if (itemstack == null)
				return false;
			if (inv[1] == null)
				return true;
			if (!inv[1].isItemEqual(itemstack))
				return false;
			int result = inv[1].stackSize + itemstack.stackSize;
			return result <= getInventoryStackLimit()
					&& result <= inv[1].getMaxStackSize();
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return index == 1;
	}

	@Override
	public void writeToNBT(NBTTagCompound root) {
		super.writeToNBT(root);

		root.setFloat("Progress", progress);

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				inv[i].writeToNBT(item);
				list.appendTag(item);
			}
		}

		root.setTag("Items", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound root) {
		super.readFromNBT(root);

		progress = root.getFloat("Progress");

		NBTTagList list = root.getTagList("Items", 10);
		inv = new ItemStack[getSizeInventory()];

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound item = list.getCompoundTagAt(i);
			int index = item.getByte("Slot");

			if (i >= 0 && i < inv.length) {
				inv[index] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	@Override
	public float resistance(float voltage) {
		return RESISTANCE;
	}

}
