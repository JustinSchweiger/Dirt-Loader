package net.dirtcraft.plugins.dirtloader.data;

import org.bukkit.World;

public class Chunk {
	private final String world;
	private final int x;
	private final int z;

	public Chunk(String world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}
}
