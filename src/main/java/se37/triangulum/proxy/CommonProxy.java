package se37.triangulum.proxy;

import net.minecraft.world.World;

public class CommonProxy {
	
	public void registerItemModels(String[] names) {
		// Proxy override
	}

	public void setWispFXDistanceLimit(boolean limit) {
		// Proxy override
	}

	public void setWispFXDepthTest(boolean depth) {
		// Proxy override
	}

	public void sparkleFX(World world, double x, double y, double z, float r, float g, float b, float size, int m) {
		sparkleFX(world, x, y, z, r, g, b, 0, 0, 0, size, m);
	}

	public void sparkleFX(World world, double x, double y, double z, float r, float g, float b, float gravity, float size, int m) {
		sparkleFX(world, x, y, z, r, g, b, 0, -gravity, 0, size, m);
	}

	public void sparkleFX(World world, double x, double y, double z, float r, float g, float b, float motionx, float motiony, float motionz, float size, int m) {
		// Proxy override
	}

	public void wispFX(World world, double x, double y, double z, float r, float g, float b, float size) {
		wispFX(world, x, y, z, r, g, b, size, 0F);
	}

	public void wispFX(World world, double x, double y, double z, float r, float g, float b, float size, float gravity) {
		wispFX(world, x, y, z, r, g, b, size, gravity, 1F);
	}

	public void wispFX(World world, double x, double y, double z, float r, float g, float b, float size, float gravity, float maxAgeMul) {
		wispFX(world, x, y, z, r, g, b, size, 0, -gravity, 0, maxAgeMul);
	}

	public void wispFX(World world, double x, double y, double z, float r, float g, float b, float size, float motionx, float motiony, float motionz) {
		wispFX(world, x, y, z, r, g, b, size, motionx, motiony, motionz, 1F);
	}

	public void wispFX(World world, double x, double y, double z, float r, float g, float b, float size, float motionx, float motiony, float motionz, float maxAgeMul) {
		// Proxy override
	}
}
