package io.github.queerbric.pride;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;

public class PrideClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MinecraftClient.getInstance().execute(() -> {
			PrideLoader.firstLoad();
			((ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager()).registerListener(new PrideLoader());
		});
	}
}