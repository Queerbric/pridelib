package io.github.queerbric.pride.impl;

import io.github.queerbric.pride.PrideClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class PrideClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PrideClient.init();
	}
}
