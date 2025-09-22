package io.github.queerbric.pride.impl.platform.neoforge;

import dev.yumi.mc.core.api.ModContainer;
import io.github.queerbric.pride.PrideLoader;
import io.github.queerbric.pride.impl.platform.Platform;
import io.github.queerbric.pride.impl.platform.PlatformProvider;
import net.minecraft.resources.io.ResourceReloader;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.util.ArrayList;
import java.util.List;

public class NeoForgePlatform implements Platform, PlatformProvider {
	public static final NeoForgePlatform INSTANCE = new NeoForgePlatform();
	private ResourceReloaderRegistration resourceReloaderRegistrar = new ResourceReloaderRegistration() {
		private final List<ResourceReloader> resourceReloaders = new ArrayList<>();

		@Override
		public void register(ResourceReloader reloader) {
			this.resourceReloaders.add(reloader);
		}

		@Override
		public void withEventBus(IEventBus eventBus) {
			var newHandler = new ResourceReloaderRegistration() {
				@Override
				public void register(ResourceReloader reloader) {
					eventBus.addListener(RegisterClientReloadListenersEvent.class, event -> {
						event.registerReloadListener(reloader);
					});
				}

				@Override
				public void withEventBus(IEventBus eventBus) {}
			};
			resourceReloaderRegistrar = newHandler;
			this.resourceReloaders.forEach(newHandler::register);
		}
	};

	private NeoForgePlatform() {}

	@Override
	public Platform getPlatform(ModContainer mod) {
		return this;
	}

	@Override
	public void registerReloader(PrideLoader reloader) {
		this.resourceReloaderRegistrar.register(reloader);
	}

	public void withEventBus(IEventBus eventBus) {
		this.resourceReloaderRegistrar.withEventBus(eventBus);
	}
}
