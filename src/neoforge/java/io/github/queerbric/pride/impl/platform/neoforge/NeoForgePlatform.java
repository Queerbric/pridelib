package io.github.queerbric.pride.impl.platform.neoforge;

import dev.yumi.mc.core.api.ModContainer;
import io.github.queerbric.pride.PrideLoader;
import io.github.queerbric.pride.impl.platform.Platform;
import io.github.queerbric.pride.impl.platform.PlatformProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.LinkedHashMap;
import java.util.Map;

public class NeoForgePlatform implements Platform, PlatformProvider {
	public static final NeoForgePlatform INSTANCE = new NeoForgePlatform();
	final Map<Identifier, PreparableReloadListener> reloaders = new LinkedHashMap<>();

	private NeoForgePlatform() {}

	@Override
	public Platform getPlatform(ModContainer mod) {
		return this;
	}

	@Override
	public void registerReloader(PrideLoader reloader) {
		this.reloaders.put(reloader.id(), reloader);
	}
}
