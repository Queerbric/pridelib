package io.github.queerbric.pride.impl;

import dev.yumi.mc.core.api.ModContainer;
import dev.yumi.mc.core.api.YumiMods;
import dev.yumi.mc.core.api.entrypoint.client.ClientModInitializer;
import io.github.queerbric.pride.PrideClient;
import io.github.queerbric.pride.impl.platform.PlatformProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class PrideClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		var platform = YumiMods.get()
				.getEntrypoints("pride:platform_provider", PlatformProvider.class)
				.getFirst()
				.value()
				.getPlatform(mod);

		PrideClient.init(platform);
	}
}
