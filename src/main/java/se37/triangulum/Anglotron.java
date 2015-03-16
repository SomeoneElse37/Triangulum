package se37.triangulum;

import net.minecraft.item.Item;
import net.minecraft.creativetab.CreativeTabs;

public class Anglotron extends Item {

    public int capacitance = 1;
    public int maxVoltage = 1;

    public Anglotron() {
	setMaxStackSize(1);
	setCreativeTab(CreativeTabs.tabMisc);
    };

    public Anglotron(int cap, int volt) {
	this.capacitance = cap;
	this.maxVoltage = volt;
    };

}
