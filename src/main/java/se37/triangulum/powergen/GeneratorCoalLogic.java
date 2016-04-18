package se37.triangulum.powergen;

import org.apache.logging.log4j.LogManager;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;

public class GeneratorCoalLogic extends GeneratorLogicBase implements
		ISidedInventory {
	private ItemStack fuelItem;
	private int burnTime;
	private int maxBurnTime;
	public static final float POWER = 1 / 16F;

	public GeneratorCoalLogic() {
		super();
		fuelItem = null;
		burnTime = 0;
		maxBurnTime = 0;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index == 0 ? fuelItem : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (index == 0 && fuelItem != null) {
			ItemStack tmp = null;
			if (count < fuelItem.stackSize) {
				fuelItem.stackSize -= count;
				tmp = fuelItem.copy();
				tmp.stackSize = count;
			} else {
				tmp = fuelItem;
				fuelItem = null;
			}
			return tmp;
		}
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (index == 0) {
			ItemStack tmp = fuelItem;
			fuelItem = null;
			return tmp;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index == 0) {
			fuelItem = stack;
		}
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
		// Do I open the GUI here, or is that handled elsewhere?
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// Do I close the GUI here, or is that handled elsewhere?
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index == 0;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "Coal Generator";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return false;
	}

	@Override
	public float powerCurve(float voltage) {
		return voltage < 0.95 ? POWER : 0;
	}

	public void update() {
		Block b = getBlockType();
		if (!(b instanceof GeneratorCoal)) {
			LogManager.getLogger().error(
					"Broken Coal Generator at " + pos + "! Abort!");
			return;
		}

		boolean dirtied = false;

		if (running) {
			burnTime--;
			if (burnTime == 0) {
				running = burnItem();
			}
			dirtied = true;
		} else {
			running = burnItem();
			dirtied |= running;
		}

		super.update();
		
		if(dirtied) {
			this.markDirty();
		}
	}

	/**
	 * Call this method whenever the generator tries to burn an item.
	 * 
	 * Warning: Calling this method will waste any partially-spent fuel in the
	 * generator! Do not call while the generator is running!
	 * 
	 * @return true if an item was burnt
	 */
	private boolean burnItem() {
		burnTime = TileEntityFurnace.getItemBurnTime(fuelItem);
		if (burnTime > 0) {
			maxBurnTime = burnTime;
			decrStackSize(0, 1);
			return true;
		}
		return false;
	}
}
