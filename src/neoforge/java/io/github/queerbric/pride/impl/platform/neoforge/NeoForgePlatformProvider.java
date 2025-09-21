package io.github.queerbric.pride.impl.platform.neoforge;

import dev.yumi.mc.core.api.ModContainer;
import io.github.queerbric.pride.impl.platform.Platform;
import io.github.queerbric.pride.impl.platform.PlatformProvider;

public class NeoForgePlatformProvider implements PlatformProvider {
	@Override
	public Platform getPlatform(ModContainer mod) {
		return new NeoForgePlatform(mod);
	}
}
