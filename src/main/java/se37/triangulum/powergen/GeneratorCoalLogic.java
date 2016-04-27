package se37.triangulum.powergen;

import org.apache.logging.log4j.LogManager;

import se37.triangulum.Triangulum;
import se37.triangulum.core.MachineBase;
import se37.triangulum.core.Octahedron;
import se37.triangulum.core.OctahedronLogic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class GeneratorCoalLogic extends GeneratorLogicBase implements
		ISidedInventory {
	private ItemStack fuelItem;
	private int burnTime;
	private int maxBurnTime;
	public static final float POWER = 1 / 16F;

	public GeneratorCoalLogic() {
		super();
		fuelItem = null;
		burnTime = -5;
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
		return index == 0 && TileEntityFurnace.getItemBurnTime(stack) > 0;
	}

	@Override
	/**
	 * 0: burnTime
	 * 1: maxBurnTime
	 */
	public int getField(int id) {
		switch (id) {
		case 0:
			return burnTime;
		case 1:
			return maxBurnTime;
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
			burnTime = value;
		case 1:
			maxBurnTime = value;
		}
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

	@Override
	public void clear() {
		fuelItem = null;
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
		return new TextComponentString("Coal Generator at " + pos);
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

		if (!worldObj.isRemote) {
			System.out.println("Running: " + burnTime + "/" + maxBurnTime);
		}
		
		if (!(getBlockType() instanceof GeneratorCoal)) {
			LogManager.getLogger().error(
					"Broken Coal Generator at " + pos + "! Abort!");
			return;
		}

		boolean dirtied = false;

		IBlockState state = worldObj.getBlockState(pos);
		MachineBase g = (MachineBase) blockType;
		Block b = worldObj.getBlockState(pos.offset(g.getPowerFace(state)))
				.getBlock();
		TileEntity te = worldObj
				.getTileEntity(pos.offset(g.getPowerFace(state)));
		if (b instanceof Octahedron && te instanceof OctahedronLogic) {
			Octahedron o = (Octahedron) b;
			OctahedronLogic ol = (OctahedronLogic) te;

			boolean wasBurning = burnTime > 0;

			if (burnTime > 0) {
				update(o, ol);
				burnTime--;
				dirtied = true;
			}

			if (burnTime <= 0 && canBurn()) {
				if (burnItem()) {
					dirtied = true;
				}
			}

			if (wasBurning && burnTime <= 0) {
				worldObj.setBlockState(pos,
						state.withProperty(GeneratorCoal.RUNNING, false), 2);
				this.validate();
				worldObj.setTileEntity(pos, this);
				System.out.println("Turning off");
			} else if (!wasBurning && burnTime > 0) {
				worldObj.setBlockState(pos,
						state.withProperty(GeneratorCoal.RUNNING, true), 2);
				this.validate();
				worldObj.setTileEntity(pos, this);
				System.out.println("Turning on");
			}

		} else {
			worldObj.setBlockState(pos,
					state.withProperty(GeneratorCoal.RUNNING, false), 2);
		}

		if (dirtied) {
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
		int time = TileEntityFurnace.getItemBurnTime(fuelItem);
		if (time > 0) {
			burnTime = time;
			maxBurnTime = time;
			if (fuelItem.stackSize == 1
					&& fuelItem.getItem().hasContainerItem(fuelItem)) {
				fuelItem.setItem(fuelItem.getItem().getContainerItem());
			} else {
				decrStackSize(0, 1);
			}
			System.out.println("Burned item: " + burnTime + "/" + maxBurnTime);
			return true;
		}
		System.out.println("Failed to burn an item");
		return false;
	}

	private boolean canBurn() {
		return TileEntityFurnace.getItemBurnTime(fuelItem) > 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound root) {
		super.writeToNBT(root);
		root.setInteger("BurnTime", burnTime);
		root.setInteger("MaxBurnTime", maxBurnTime);

		if (fuelItem != null) {
			NBTTagCompound item = new NBTTagCompound();
			fuelItem.writeToNBT(item);
			root.setTag("FuelItem", item);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound root) {
		super.readFromNBT(root);
		burnTime = root.getInteger("BurnTime");
		maxBurnTime = root.getInteger("MaxBurnTime");

		if (root.hasKey("FuelItem")) {
			fuelItem = ItemStack.loadItemStackFromNBT(root
					.getCompoundTag("FuelItem"));
		}
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
