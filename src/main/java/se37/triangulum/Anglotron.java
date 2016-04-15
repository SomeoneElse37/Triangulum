package se37.triangulum;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
		if (b instanceof Octahedron) {
			Octahedron o = (Octahedron) b;
			OctahedronLogic ol = (OctahedronLogic) world.getTileEntity(pos);
			if (o.getMaxVoltage() == this.maxVoltage) {
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
						"Set voltage to " + maxVoltage));
				ol.setVoltage(this.maxVoltage);
				return EnumActionResult.PASS;
			}
		}
		return EnumActionResult.FAIL;
	}
}
