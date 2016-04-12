package se37.triangulum;

import java.util.LinkedList;
import java.util.List;

import se37.triangulum.proxy.CommonProxy;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = Triangulum.MODID, version = Triangulum.VERSION)
public class Triangulum {

	public static final String MODID = "triangulum";
	public static final String VERSION = "0.0.0";

	@Instance(MODID)
	public static Triangulum instance;

	@SidedProxy(serverSide = "se37.triangulum.proxy.CommonProxy", clientSide = "se37.triangulum.proxy.ClientProxy")
	public static CommonProxy proxy;

	private static Item triangleWood;
	private static Item triangleIron;
	private static Item triangleGold;
	private static Item triangleDiamond;

	private static Item anglotronWood;
	private static Item anglotronIron;
	private static Item anglotronGold;
	private static Item anglotronDiamond;

	private static Block octahedronWood;
	private static Block octahedronIron;
	private static Block octahedronGold;
	private static Block octahedronDiamond;
	
	private static List<String> itemNames;

	/**
	 * Sets the Item's unlocalized name to the given name, registers both with GameRegistry,
	 * and adds the item's name to the local list of item names.
	 * @param i the item to register
	 * @param name the name to give it
	 * @return the item, for convenience
	 */
	private static Item registerItem(Item i, String name) {
		i.setUnlocalizedName(name);
		GameRegistry.registerItem(i, name);
		itemNames.add(name);
		return i;
	}
	
	/**
	 * Sets the Block's unlocalized name to the given name, registers both with GameRegistry,
	 * and adds the name to the local list of item names.
	 * @param i the item to register
	 * @param name the name to give it
	 * @return the item, for convenience
	 */
	private static Block registerBlock(Block b, String name) {
		b.setUnlocalizedName(name);
		GameRegistry.registerBlock(b, name);
		itemNames.add(name);
		return b;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent preInitEvent) {
		itemNames = new LinkedList<String>();
		
		triangleWood = registerItem(new Item().setCreativeTab(CreativeTabs.tabMisc), "triangle_wood");
		triangleIron = registerItem(new Item().setCreativeTab(CreativeTabs.tabMisc), "triangle_iron");
		triangleGold = registerItem(new Item().setCreativeTab(CreativeTabs.tabMisc), "triangle_gold");
		triangleDiamond = registerItem(new Item().setCreativeTab(CreativeTabs.tabMisc), "triangle_diamond");


		anglotronWood = registerItem(new Anglotron(1, 1), "anglotron_wood");
		anglotronIron = registerItem(new Anglotron(4, 4), "anglotron_iron");
		anglotronGold = registerItem(new Anglotron(16, 16), "anglotron_gold");
		anglotronDiamond = registerItem(new Anglotron(64, 64), "anglotron_diamond");

																//		  c   v   r   i   d
		octahedronWood = registerBlock(new Octahedron(Material.wood,      4,  1,  8,  2,  8), "octahedron_wood");
		octahedronIron = registerBlock(new Octahedron(Material.rock,     16,  4,  8,  4, 16), "octahedron_iron");
		octahedronGold = registerBlock(new Octahedron(Material.rock,     64, 16,  8,  8, 32), "octahedron_gold");
		octahedronDiamond = registerBlock(new Octahedron(Material.rock, 256, 64,  8, 16, 64), "octahedron_diamond");

		GameRegistry.registerTileEntity(se37.triangulum.OctahedronLogic.class,
				"octahedronLogic");

		proxy.registerClientHandlers();
	};

		
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerItemModels(itemNames.toArray(new String[itemNames.size()]));
		
		// For use in crafting recipes
		ItemStack woodTri = new ItemStack(Triangulum.triangleWood);
		ItemStack ironTri = new ItemStack(Triangulum.triangleIron);
		ItemStack goldTri = new ItemStack(Triangulum.triangleGold);
		ItemStack diamTri = new ItemStack(Triangulum.triangleDiamond);

		//Register recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(woodTri, " s ", "   ",
				"s s", 's', "stickWood"));
		GameRegistry.addRecipe(new ItemStack(Triangulum.triangleIron, 2),
				" s ", "   ", "s s", 's', new ItemStack(Items.iron_ingot));
		GameRegistry.addRecipe(new ItemStack(Triangulum.triangleGold, 2),
				" s ", "   ", "s s", 's', new ItemStack(Items.gold_ingot));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Triangulum.triangleDiamond, 2), " s ", "   ", "s s", 's',
				"gemDiamond"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Triangulum.anglotronWood), "l", "t", "r", 'l', "dyeBlue", 't',
				woodTri, 'r', "dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Triangulum.anglotronIron), "l", "t", "r", 'l', "dyeBlue", 't',
				ironTri, 'r', "dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Triangulum.anglotronGold), "l", "t", "r", 'l', "dyeBlue", 't',
				goldTri, 'r', "dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Triangulum.anglotronDiamond), "l", "t", "r", 'l', "dyeBlue",
				't', diamTri, 'r', "dustRedstone"));

		GameRegistry.addRecipe(new ItemStack(Triangulum.octahedronWood, 2),
				"ttt", "t t", "ttt", 't', woodTri);
		GameRegistry.addRecipe(new ItemStack(Triangulum.octahedronIron, 2),
				"ttt", "t t", "ttt", 't', ironTri);
		GameRegistry.addRecipe(new ItemStack(Triangulum.octahedronGold, 2),
				"ttt", "t t", "ttt", 't', goldTri);
		GameRegistry.addRecipe(new ItemStack(Triangulum.octahedronDiamond, 2),
				"ttt", "t t", "ttt", 't', diamTri);
	};

}
