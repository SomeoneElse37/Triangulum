package se37.triangulum.core;

import se37.triangulum.machine.BrickFurnace;
import se37.triangulum.machine.BrickFurnaceLogic;
import se37.triangulum.powergen.GeneratorCoal;
import se37.triangulum.powergen.GeneratorCoalLogic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

public class Anglotron extends Item {

	private int capacitance = 1;
	private int maxVoltage = 1;

	public Anglotron() {
		super();
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMisc);
	};

	public Anglotron(int cap, int volt) {
		this();
		this.capacitance = cap;
		this.maxVoltage = volt;
	};

	/**
	 * Called when a Block is right-clicked with this Item <br>
	 * <br>
	 * A lot of this code is currently debug code. In the future, the anglotron
	 * will be the mod's wrench and portable battery.
	 */
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player,
			World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		Block b = world.getBlockState(pos).getBlock();
		TileEntity te = world.getTileEntity(pos);
		if (b instanceof Octahedron) {
			Octahedron o = (Octahedron) b;
			OctahedronLogic ol = (OctahedronLogic) te;
			if (o.getMaxVoltage() == this.maxVoltage) {

				if (world.isRemote) {
					player.addChatComponentMessage(new TextComponentString(
							"--- Clientside Readings ---"));
				} else {
					player.addChatComponentMessage(new TextComponentString(
							"--- Serverside Readings ---"));
				}

				player.addChatComponentMessage(new TextComponentString(
						"Voltage: " + ol.getVoltage() + " volts"));
				player.addChatComponentMessage(new TextComponentString(
						"Charge: " + ol.getVoltage() * o.getCapacitance()
								+ " coulombs"));
				player.addChatComponentMessage(new TextComponentString(
						"Energy in Capacitor: " + ol.getVoltage()
								* ol.getVoltage() * o.getCapacitance() / 2
								+ " joules"));
				player.addChatComponentMessage(new TextComponentString(
						"Current: " + ol.getCurrent(facing) + " amperes"));
				player.addChatComponentMessage(new TextComponentString(
						"Power Transferred: " + ol.getCurrent(facing)
								* ol.getVoltage() + " watts"));
				player.addChatComponentMessage(new TextComponentString(
						"Power Dissipated: " + ol.getCurrent(facing)
								* ol.getCurrent(facing) * o.getResistivity()
								* ol.getDistance(facing) + " watts"));
				player.addChatComponentMessage(new TextComponentString(
						"Energy in Inductor: " + ol.getCurrent(facing)
								* ol.getCurrent(facing) * o.getInductivity()
								* ol.getDistance(facing) / 2 + " joules"));
				return EnumActionResult.SUCCESS;
			} else {
				player.addChatComponentMessage(new TextComponentString(
						"Set voltage to " + maxVoltage * 4));
				ol.setVoltage(this.maxVoltage * 4);
				return EnumActionResult.PASS;
			}
		} else if (b instanceof GeneratorCoal) {
			GeneratorCoal g = (GeneratorCoal) b;
			GeneratorCoalLogic gl = (GeneratorCoalLogic) te;

			if (world.isRemote) {
				player.addChatComponentMessage(new TextComponentString(
						"--- Clientside Readings ---"));
			} else {
				player.addChatComponentMessage(new TextComponentString(
						"--- Serverside Readings ---"));
			}

			if (gl.getStackInSlot(0) != null) {
				player.addChatComponentMessage(new TextComponentString(
						"Contents: " + gl.getStackInSlot(0)));
			} else {
				player.addChatComponentMessage(new TextComponentString(
						"Contents: Nothing"));
			}
			
			player.addChatComponentMessage(new TextComponentString(
					"Burn time: " + gl.getField(0)));
			player.addChatComponentMessage(new TextComponentString(
					"Maximum Burn Time: " + gl.getField(1)));
		} else if (b instanceof BrickFurnace) {
			BrickFurnace g = (BrickFurnace) b;
			BrickFurnaceLogic gl = (BrickFurnaceLogic) te;

			if (world.isRemote) {
				player.addChatComponentMessage(new TextComponentString(
						"--- Clientside Readings ---"));
			} else {
				player.addChatComponentMessage(new TextComponentString(
						"--- Serverside Readings ---"));
			}

			String s1 = null;
			String s2 = null;
			
			if (gl.getStackInSlot(0) != null) {
				s1 = gl.getStackInSlot(0).toString();
			} else {
				s1 = "nothing";
			}
			
			if (gl.getStackInSlot(1) != null) {
				s2 = gl.getStackInSlot(1).toString();
			} else {
				s2 = "nothing";
			}
			
			player.addChatComponentMessage(new TextComponentString("Smelting " + s1 + " into " + s2));
			
			player.addChatComponentMessage(new TextComponentString(
					"Progress: " + Float.intBitsToFloat(gl.getField(0))));
		}
		return EnumActionResult.FAIL;
	}
}
