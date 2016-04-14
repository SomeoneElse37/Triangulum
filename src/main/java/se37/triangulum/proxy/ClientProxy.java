/*
 * Much of the code in this class (specifically, all the particle stuff) was 
 * created by Azanor and ripped wholesale from Vazkii's mod Psi. Much thanks to them
 * for providing the sources and being awesome.
 * 
 * --SE37
 */

package se37.triangulum.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import se37.triangulum.particles.FXSparkle;
import se37.triangulum.particles.FXWisp;
import se37.triangulum.particles.ParticleRenderDispatcher;

public class ClientProxy extends CommonProxy {
	public void registerClientHandlers() {
		MinecraftForge.EVENT_BUS.register(new ParticleRenderDispatcher());
	}

	/**
	 * Register all the items named in the given array with the renderer.
	 * 
	 * @param names
	 *            the registry names of the Items and ItemBlocks to register
	 */
	@Override
	public void registerItemModels(String[] names) {
		for (String name : names) {
			Item i = GameRegistry.findItem("triangulum", name);
			ModelResourceLocation loc = new ModelResourceLocation("triangulum:"
					+ name, "inventory");
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
					.register(i, 0, loc);
		}
	}

	@Override
	public void sparkleFX(World world, double x, double y, double z, float r,
			float g, float b, float motionx, float motiony, float motionz,
			float size, float m) {
		if (!doParticle(world))
			return;

		FXSparkle sparkle = new FXSparkle(world, x, y, z, size, r, g, b, m);
		sparkle.setSpeed(motionx / 20, motiony / 20, motionz / 20);	//convert m/s to m/tick
		Minecraft.getMinecraft().effectRenderer.addEffect(sparkle);
	}

	private static boolean distanceLimit = true;
	private static boolean depthTest = true;

	@Override
	public void setWispFXDistanceLimit(boolean limit) {
		distanceLimit = limit;
	}

	@Override
	public void setWispFXDepthTest(boolean test) {
		depthTest = test;
	}

	@Override
	public void wispFX(World world, double x, double y, double z, float r,
			float g, float b, float motionx, float motiony, float motionz,
			float size, float maxAgeMul) {
		if (!doParticle(world))
			return;

		FXWisp wisp = new FXWisp(world, x, y, z, size, r, g, b, distanceLimit,
				depthTest, maxAgeMul);
		wisp.setSpeed(motionx / 20, motiony / 20, motionz / 20);	//convert m/s to m/tick

		Minecraft.getMinecraft().effectRenderer.addEffect(wisp);
	}

	private boolean doParticle(World world) {
		if (!world.isRemote)
			return false;

		float chance = 1F;
		if (Minecraft.getMinecraft().gameSettings.particleSetting == 1)
			chance = 0.6F;
		else if (Minecraft.getMinecraft().gameSettings.particleSetting == 2)
			chance = 0.2F;

		return chance == 1F || Math.random() < chance;
	}
}
