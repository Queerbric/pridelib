package io.github.queerbric.pride.impl.platform.fabric;

import dev.yumi.mc.core.api.ModContainer;
import io.github.queerbric.pride.PrideLoader;
import io.github.queerbric.pride.impl.platform.Platform;
import io.github.queerbric.pride.impl.platform.PlatformProvider;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.io.ResourceType;

public class FabricPlatform implements PlatformProvider, Platform {
	@Override
	public Platform getPlatform(ModContainer mod) {
		return this;
	}

	@Override
	public void registerReloader(PrideLoader reloader) {
		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(reloader.id(), reloader);
	}
}
