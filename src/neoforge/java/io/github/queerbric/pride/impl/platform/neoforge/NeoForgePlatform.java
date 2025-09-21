package io.github.queerbric.pride.impl.platform.neoforge;

import dev.yumi.mc.core.api.ModContainer;
import io.github.queerbric.pride.PrideLoader;
import io.github.queerbric.pride.impl.platform.Platform;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

public class NeoForgePlatform implements Platform {
	private final ModContainer mod;
	private final IEventBus eventBus;

	public NeoForgePlatform(ModContainer mod) {
		this.mod = mod;

		this.eventBus = ModList.get().getModContainerById(this.mod.id())
				.orElseThrow(() -> new IllegalStateException(
						"Could not find NeoForge mod container despite mod being initialized as %s."
								.formatted(this.mod.id())
				))
				.getEventBus();
	}

	@Override
	public void registerReloader(PrideLoader reloader) {
		this.eventBus.addListener(AddClientReloadListenersEvent.class, event -> {
			event.addListener(reloader.id(), reloader);
		});
	}
}
