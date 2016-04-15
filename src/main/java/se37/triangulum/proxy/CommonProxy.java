/*
 * Much of the code in this class (specifically, all the particle stuff) was 
 * created by Azanor and ripped wholesale from Vazkii's mod Psi. Much thanks to them
 * for providing the sources and being awesome.
 * 
 * --SE37
 */

package se37.triangulum.proxy;

import net.minecraft.world.World;

public class CommonProxy {
	public void registerClientHandlers() {
		//Proxy override
	}
	
	public void registerItemModels(String[] names) {
		// Proxy override
	}

	public void setWispFXDistanceLimit(boolean limit) {
		// Proxy override
	}

	public void setWispFXDepthTest(boolean depth) {
		// Proxy override
	}

	public void sparkleFX(World world, double x, double y, double z, float r, float g, float b, float size, float m) {
		sparkleFX(world, x, y, z, r, g, b, 0, 0, 0, size, m);
	}

	public void sparkleFX(World world, double x, double y, double z, float r, float g, float b, float motionx, float motiony, float motionz, float size, float m) {
		// Proxy override
	}

	public void wispFX(World world, double x, double y, double z, float r, float g, float b, float size, float maxAgeMul) {
		wispFX(world, x, y, z, r, g, b, 0.0F, 0.0F, 0.0F, size, maxAgeMul);
	}

	public void wispFX(World world, double x, double y, double z, float r, float g, float b, float motionx, float motiony, float motionz, float size, float maxAgeMul) {
		// Proxy override
	}
}
