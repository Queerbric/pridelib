package io.github.queerbric.pride.impl.platform.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@Mod(value = "pride", dist = Dist.CLIENT)
public class NeoForgeInitializer {
	public NeoForgeInitializer(IEventBus modBus) {
		modBus.addListener(AddClientReloadListenersEvent.class, event -> {
			NeoForgePlatform.INSTANCE.reloaders.forEach(event::addListener);
		});
	}
}
