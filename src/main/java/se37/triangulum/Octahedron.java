package se37.triangulum;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Octahedron extends Block {

    public int capacitance = 1;
    public int maxVoltage = 1;
    public String textureName = "";

    public Octahedron(Material material) {
	super(material);
    };

    public Octahedron(Material material, String tex, int cap, int volt) {
	super(material);
	this.capacitance = cap;
	this.maxVoltage = volt;
	this.textureName = tex;
    };

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    };

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.blockIcon = iconRegister.registerIcon(textureName);
    };

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick (World par1World, int par2, int par3, int par4, Random par5Random)
    {
	int meta = par1World.getBlockMetadata(par2, par3, par4);
	double d0 = (double) ((float) par2 + 0.5F);
	double d1 = (double) ((float) par3 + 0.5F);
	double d2 = (double) ((float) par4 + 0.5F);

	par1World.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
	par1World.spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);
    };

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
	return new OctahedronLogic();
    };
}


