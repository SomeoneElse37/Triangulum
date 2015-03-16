package se37.triangulum;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class OctahedronLogic extends TileEntity {

    public float voltage;

    @Override
    public void writeToNBT(NBTTagCompound par1) {
	super.writeToNBT(par1);
	par1.setFloat("Voltage", voltage);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
	super.readFromNBT(par1);
	this.voltage = par1.getFloat("Voltage");
    }
}
