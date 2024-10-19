package io.github.queerbric.pride;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;

public interface PrideFlagShape {
	@Environment(EnvType.CLIENT)
	void render(GuiGraphics graphics, IntList colors, int x, int y, int width, int height);
}
