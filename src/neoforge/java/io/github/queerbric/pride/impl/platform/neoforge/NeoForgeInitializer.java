package io.github.queerbric.pride.impl.platform.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = "pride", dist = Dist.CLIENT)
public class NeoForgeInitializer {
	public NeoForgeInitializer(IEventBus eventBus) {
		NeoForgePlatform.INSTANCE.withEventBus(eventBus);
	}
}
