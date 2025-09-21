package io.github.queerbric.pride.impl.platform;

import dev.yumi.mc.core.api.ModContainer;

public interface PlatformProvider {
	Platform getPlatform(ModContainer mod);
}
