package io.github.queerbric.pride.impl.platform.fabric;

import dev.yumi.mc.core.api.ModContainer;
import io.github.queerbric.pride.PrideLoader;
import io.github.queerbric.pride.impl.platform.Platform;
import io.github.queerbric.pride.impl.platform.PlatformProvider;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.io.ResourceManager;
import net.minecraft.resources.io.ResourceType;
import net.minecraft.util.profiling.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FabricPlatform implements PlatformProvider, Platform {
	@Override
	public Platform getPlatform(ModContainer mod) {
		return this;
	}

	@Override
	public void registerReloader(PrideLoader reloader) {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
				.registerReloadListener(new IdentifiableResourceReloadListener() {
					@Override
					public Identifier getFabricId() {
						return reloader.id();
					}

					@Override
					public CompletableFuture<Void> reload(
							Synchronizer synchronizer,
							ResourceManager resourceManager,
							Profiler prepareProfiler,
							Profiler applyProfiler,
							Executor prepareExecutor,
							Executor applyExecutor
					) {
						return reloader.reload(
								synchronizer, resourceManager,
								prepareProfiler, applyProfiler,
								prepareExecutor, applyExecutor
						);
					}
				});
	}
}
