package io.github.queerbric.pride;

import io.github.queerbric.pride.impl.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PrideClient {
	public static void init(Platform platform) {
		platform.registerReloader(new PrideLoader());
	}
}