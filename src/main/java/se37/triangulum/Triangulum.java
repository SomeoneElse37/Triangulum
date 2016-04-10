package se37.triangulum;

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

	// public static final PacketPipeline packetPipeline = new PacketPipeline();

	public static Item triangleWood;
	public static Item triangleIron;
	public static Item triangleGold;
	public static Item triangleDiamond;

	public static Item anglotronWood;
	public static Item anglotronIron;
	public static Item anglotronGold;
	public static Item anglotronDiamond;

	public static Block octahedronWood;
	public static Block octahedronIron;
	public static Block octahedronGold;
	public static Block octahedronDiamond;

	@EventHandler
	public void preInit(FMLPreInitializationEvent preInitEvent) {
		triangleWood = new Item().setUnlocalizedName("triangle_wood")
				.setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerItem(triangleWood, "triangle_wood");
		
		triangleIron = new Item().setUnlocalizedName("triangle_iron")
				.setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerItem(triangleIron, "triangle_iron");
		triangleGold = new Item().setUnlocalizedName("triangle_gold")
				.setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerItem(triangleGold, "triangle_gold");
		triangleDiamond = new Item().setUnlocalizedName("triangle_diamond")
				.setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerItem(triangleDiamond, "triangle_diamond");

		anglotronWood = new Anglotron(1, 1).setUnlocalizedName("anglotron_wood");
		GameRegistry.registerItem(anglotronWood, "anglotron_wood");
		anglotronIron = new Anglotron(4, 4).setUnlocalizedName("anglotron_iron");
		GameRegistry.registerItem(anglotronIron, "anglotron_iron");
		anglotronGold = new Anglotron(16, 16)
				.setUnlocalizedName("anglotron_gold");
		GameRegistry.registerItem(anglotronGold, "anglotron_gold");
		anglotronDiamond = new Anglotron(64, 64)
				.setUnlocalizedName("anglotron_diamond");
		GameRegistry.registerItem(anglotronDiamond, "anglotron_diamond");

		octahedronWood = new Octahedron(Material.wood, 4, 1).setHardness(2.0F)
				.setUnlocalizedName("octahedron_wood")
				.setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.registerBlock(octahedronWood, "octahedron_wood");
		octahedronIron = new Octahedron(Material.rock, 16, 4).setHardness(2.0F)
				.setUnlocalizedName("octahedron_iron")
				.setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.registerBlock(octahedronIron, "octahedron_iron");
		octahedronGold = new Octahedron(Material.rock, 64, 16)
				.setHardness(2.0F).setUnlocalizedName("octahedron_gold")
				.setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.registerBlock(octahedronGold, "octahedron_gold");
		octahedronDiamond = new Octahedron(Material.rock, 256, 64)
				.setHardness(2.0F).setUnlocalizedName("octahedron_diamond")
				.setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.registerBlock(octahedronDiamond, "octahedron_diamond");

		GameRegistry.registerTileEntity(se37.triangulum.OctahedronLogic.class,
				"octahedronLogic");

	};

	/**
	 * Register the named item with the renderer
	 * @param name internal name of item to register (e.x. "anglotron_wood")
	 */
	private void registerItemModel(String name) {
		Item i = GameRegistry.findItem("triangulum", name);
		ModelResourceLocation loc = new ModelResourceLocation("triangulum:" + name, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(i, 0, loc);
	}
	
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Register item models
		registerItemModel("triangle_wood");
		registerItemModel("triangle_iron");
		registerItemModel("triangle_gold");
		registerItemModel("triangle_diamond");
		registerItemModel("anglotron_wood");
		registerItemModel("anglotron_iron");
		registerItemModel("anglotron_gold");
		registerItemModel("anglotron_diamond");
		registerItemModel("octahedron_wood");
		registerItemModel("octahedron_iron");
		registerItemModel("octahedron_gold");
		registerItemModel("octahedron_diamond");
		
		
		// For future use, when I add things that use these in crafting recipes
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
