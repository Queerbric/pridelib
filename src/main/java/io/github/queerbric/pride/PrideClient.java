package io.github.queerbric.pride;

import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.resource.ResourceManagerHelper;

public class PrideClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.getInstance().registerReloadListener(new PrideLoader());
	}
}