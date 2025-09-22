package io.github.queerbric.pride.impl.platform.neoforge;

import net.minecraft.resources.io.ResourceReloader;
import net.neoforged.bus.api.IEventBus;

public interface ResourceReloaderRegistration {
	void register(ResourceReloader reloader);

	void withEventBus(IEventBus eventBus);
}
