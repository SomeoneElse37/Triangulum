package se37.triangulum;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.LinkedList;
import java.util.List;

import se37.triangulum.core.Anglotron;
import se37.triangulum.core.Octahedron;
import se37.triangulum.core.OctahedronLogic;
import se37.triangulum.machine.BrickFurnace;
import se37.triangulum.machine.BrickFurnaceLogic;
import se37.triangulum.packets.SPacketNetUpdate;
import se37.triangulum.powergen.GeneratorCoal;
import se37.triangulum.powergen.GeneratorCoalLogic;
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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
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
	public static final String VERSION = "1.0.0";

	@Instance(MODID)
	public static Triangulum instance;

	@SidedProxy(serverSide = "se37.triangulum.proxy.CommonProxy", clientSide = "se37.triangulum.proxy.ClientProxy")
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper networkWrapper;

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

	private static Block generatorCoal;

	private static Block brickFurnace;

	private static List<String> itemNames;

	/**
	 * Sets the Item's unlocalized name to the given name, registers both with
	 * GameRegistry, and adds the item's name to the local list of item names.
	 * 
	 * @param i
	 *            the item to register
	 * @param name
	 *            the name to give it
	 * @return the item, for convenience
	 */
	private static Item registerItem(Item i, String name) {
		i.setUnlocalizedName(name);
		GameRegistry.registerItem(i, name);
		itemNames.add(name);
		return i;
	}

	/**
	 * Sets the Block's unlocalized name to the given name, registers both with
	 * GameRegistry, and adds the name to the local list of item names.
	 * 
	 * @param i
	 *            the item to register
	 * @param name
	 *            the name to give it
	 * @return the block, for convenience
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

		triangleWood = registerItem(
				new Item().setCreativeTab(CreativeTabs.tabMisc),
				"triangle_wood");
		triangleIron = registerItem(
				new Item().setCreativeTab(CreativeTabs.tabMisc),
				"triangle_iron");
		triangleGold = registerItem(
				new Item().setCreativeTab(CreativeTabs.tabMisc),
				"triangle_gold");
		triangleDiamond = registerItem(
				new Item().setCreativeTab(CreativeTabs.tabMisc),
				"triangle_diamond");

		anglotronWood = registerItem(new Anglotron(1, 1), "anglotron_wood");
		anglotronIron = registerItem(new Anglotron(4, 4), "anglotron_iron");
		anglotronGold = registerItem(new Anglotron(16, 16), "anglotron_gold");
		anglotronDiamond = registerItem(new Anglotron(64, 64),
				"anglotron_diamond");
		// C . V . R/d . L/d . d
		octahedronWood = registerBlock(new Octahedron(Material.wood, 4, 1,
				0.008F, 4, 8), "octahedron_wood");
		octahedronIron = registerBlock(new Octahedron(Material.rock, 16, 4,
				0.004F, 4, 16), "octahedron_iron");
		octahedronGold = registerBlock(new Octahedron(Material.rock, 64, 16,
				0.002F, 4, 32), "octahedron_gold");
		octahedronDiamond = registerBlock(new Octahedron(Material.rock, 256,
				64, 0.001F, 4, 64), "octahedron_diamond");

		generatorCoal = registerBlock(new GeneratorCoal(Material.rock),
				"generator_coal");

		brickFurnace = registerBlock(new BrickFurnace(Material.rock),
				"furnace_brick");

		GameRegistry.registerTileEntity(OctahedronLogic.class,
				"octahedronLogic");
		GameRegistry.registerTileEntity(GeneratorCoalLogic.class,
				"generatorCoalLogic");
		GameRegistry.registerTileEntity(BrickFurnaceLogic.class,
				"brickFurnaceLogic");

		proxy.registerClientHandlers();

		networkWrapper = NetworkRegistry.INSTANCE
				.newSimpleChannel("TriangulumNetWrapper");
		networkWrapper.registerMessage(SPacketNetUpdate.Handler.class,
				SPacketNetUpdate.class, 0, Side.CLIENT);

		// Debug
		ByteBufAllocator alloc = new UnpooledByteBufAllocator(true);
		ByteBuf buf = alloc.buffer();

		SPacketNetUpdate p1 = new SPacketNetUpdate(1, new float[] { 2, 3, 4, 5,
				6, 7, 8 }, new BlockPos(9, 10, 11));
		p1.toBytes(buf);
		System.out.println(p1);

		SPacketNetUpdate p2 = new SPacketNetUpdate();
		p2.fromBytes(buf);
		System.out.println(p2);
	};

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerItemModels(itemNames.toArray(new String[itemNames.size()]));

		// For use in crafting recipes
		ItemStack woodTri = new ItemStack(triangleWood);
		ItemStack ironTri = new ItemStack(triangleIron);
		ItemStack goldTri = new ItemStack(triangleGold);
		ItemStack diamTri = new ItemStack(triangleDiamond);

		// Register recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(woodTri, " s ", "   ",
				"s s", 's', "stickWood"));
		GameRegistry.addRecipe(new ItemStack(triangleIron, 2), " s ", "   ",
				"s s", 's', new ItemStack(Items.iron_ingot));
		GameRegistry.addRecipe(new ItemStack(triangleGold, 2), " s ", "   ",
				"s s", 's', new ItemStack(Items.gold_ingot));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				triangleDiamond, 2), " s ", "   ", "s s", 's', "gemDiamond"));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(anglotronWood), "l", "t", "r", 'l', "gemLapis",
				't', woodTri, 'r', "dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(anglotronIron), "l", "t", "r", 'l', "gemLapis",
				't', ironTri, 'r', "dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(anglotronGold), "l", "t", "r", 'l', "gemLapis",
				't', goldTri, 'r', "dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				anglotronDiamond), "l", "t", "r", 'l', "gemLapis", 't',
				diamTri, 'r', "dustRedstone"));

		GameRegistry.addRecipe(new ItemStack(octahedronWood, 2), "ttt", "t t",
				"ttt", 't', woodTri);
		GameRegistry.addRecipe(new ItemStack(octahedronIron, 2), "ttt", "t t",
				"ttt", 't', ironTri);
		GameRegistry.addRecipe(new ItemStack(octahedronGold, 2), "ttt", "t t",
				"ttt", 't', goldTri);
		GameRegistry.addRecipe(new ItemStack(octahedronDiamond, 2), "ttt",
				"t t", "ttt", 't', diamTri);

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(generatorCoal), " t ", "ifi", "iri", 't',
				woodTri, 'f', Blocks.furnace, 'i', "ingotBrick", 'r',
				"dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(brickFurnace),
				" t ", "ifi", "ili", 't', woodTri, 'f', Blocks.furnace, 'i',
				"ingotBrick", 'l', "gemLapis"));
	};

}
