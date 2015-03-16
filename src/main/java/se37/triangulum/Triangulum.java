package se37.triangulum;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
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
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import se37.triangulum.packethandling.PacketPipeline;


@Mod(modid = Triangulum.MODID, version = Triangulum.VERSION)
public class Triangulum {

    public static final String MODID = "triangulum";
    public static final String VERSION = "0.0.0";

    public static final PacketPipeline packetPipeline = new PacketPipeline();

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
	triangleWood = new Triangle().setUnlocalizedName("triangleWood").setTextureName("triangulum:triangleWood");
	GameRegistry.registerItem(triangleWood, triangleWood.getUnlocalizedName());
	triangleIron = new Triangle().setUnlocalizedName("triangleIron").setTextureName("triangulum:triangleIron");
	GameRegistry.registerItem(triangleIron, triangleIron.getUnlocalizedName());
	triangleGold = new Triangle().setUnlocalizedName("triangleGold").setTextureName("triangulum:triangleGold");
	GameRegistry.registerItem(triangleGold, triangleGold.getUnlocalizedName());
	triangleDiamond = new Triangle().setUnlocalizedName("triangleDiamond").setTextureName("triangulum:triangleDiamond");
	GameRegistry.registerItem(triangleDiamond, triangleDiamond.getUnlocalizedName());

	anglotronWood = new Anglotron(1, 1).setUnlocalizedName("anglotronWood").setTextureName("triangulum:anglotronWood");
	GameRegistry.registerItem(anglotronWood, anglotronWood.getUnlocalizedName());
	anglotronIron = new Anglotron(4, 4).setUnlocalizedName("anglotronIron").setTextureName("triangulum:anglotronIron");
	GameRegistry.registerItem(anglotronIron, anglotronIron.getUnlocalizedName());
	anglotronGold = new Anglotron(16, 16).setUnlocalizedName("anglotronGold").setTextureName("triangulum:anglotronGold");
	GameRegistry.registerItem(anglotronGold, anglotronGold.getUnlocalizedName());
	anglotronDiamond = new Anglotron(64, 64).setUnlocalizedName("anglotronDiamond").setTextureName("triangulum:anglotronDiamond");
	GameRegistry.registerItem(anglotronDiamond, anglotronDiamond.getUnlocalizedName());

	octahedronWood = new Octahedron(Material.wood, "triangulum:octahedronWood", 4, 1).setHardness(2.0F).setBlockName("octahedronWood").setCreativeTab(CreativeTabs.tabBlock);
	GameRegistry.registerBlock(octahedronWood, "octahedronWood");
	octahedronIron = new Octahedron(Material.rock, "triangulum:octahedronIron", 16, 4).setHardness(2.0F).setBlockName("octahedronIron").setCreativeTab(CreativeTabs.tabBlock);
	GameRegistry.registerBlock(octahedronIron, "octahedronIron");
	octahedronGold = new Octahedron(Material.rock, "triangulum:octahedronGold", 64, 16).setHardness(2.0F).setBlockName("octahedronGold").setCreativeTab(CreativeTabs.tabBlock);
	GameRegistry.registerBlock(octahedronGold, "octahedronGold");
	octahedronDiamond = new Octahedron(Material.rock, "triangulum:octahedronDiamond", 256, 64).setHardness(2.0F).setBlockName("octahedronDiamond").setCreativeTab(CreativeTabs.tabBlock);
	GameRegistry.registerBlock(octahedronDiamond, "octahedronDiamond");

	GameRegistry.registerTileEntity(se37.triangulum.OctahedronLogic.class, "octahedronLogic");

    };

    @EventHandler
    public void init(FMLInitializationEvent event) {
	ItemStack woodTri = new ItemStack(Triangulum.triangleWood);	//For future use, when I add things that use these
	ItemStack ironTri = new ItemStack(Triangulum.triangleIron);	//in crafting recipes
	ItemStack goldTri = new ItemStack(Triangulum.triangleGold);
	ItemStack diamTri = new ItemStack(Triangulum.triangleDiamond);

	GameRegistry.addRecipe(new ShapedOreRecipe(woodTri, " s ", "   ", "s s", 's', "stickWood"));
	GameRegistry.addRecipe(new ItemStack(Triangulum.triangleIron, 2), " s ", "   ", "s s", 's', new ItemStack(Items.iron_ingot));
	GameRegistry.addRecipe(new ItemStack(Triangulum.triangleGold, 2), " s ", "   ", "s s", 's', new ItemStack(Items.gold_ingot));
	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Triangulum.triangleDiamond, 2), " s ", "   ", "s s", 's', "gemDiamond"));

	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Triangulum.anglotronWood), "l", "t", "r"
		, 'l', "dyeBlue", 't', woodTri, 'r', "dustRedstone"));
	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Triangulum.anglotronIron), "l", "t", "r"
		, 'l', "dyeBlue", 't', ironTri, 'r', "dustRedstone"));
	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Triangulum.anglotronGold), "l", "t", "r"
		, 'l', "dyeBlue", 't', goldTri, 'r', "dustRedstone"));
	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Triangulum.anglotronDiamond), "l", "t", "r"
		, 'l', "dyeBlue", 't', diamTri, 'r', "dustRedstone"));

	GameRegistry.addRecipe(new ItemStack(Triangulum.octahedronWood, 2), "ttt", "t t", "ttt", 't', woodTri);
	GameRegistry.addRecipe(new ItemStack(Triangulum.octahedronIron, 2), "ttt", "t t", "ttt", 't', ironTri);
	GameRegistry.addRecipe(new ItemStack(Triangulum.octahedronGold, 2), "ttt", "t t", "ttt", 't', goldTri);
	GameRegistry.addRecipe(new ItemStack(Triangulum.octahedronDiamond, 2), "ttt", "t t", "ttt", 't', diamTri);
    };

    @EventHandler
    public void initialise(FMLInitializationEvent evt) {
	packetPipeline.initialise();
    };

    @EventHandler
    public void postInitialise(FMLPostInitializationEvent evt) {
	packetPipeline.postInitialise();
    };
}




















