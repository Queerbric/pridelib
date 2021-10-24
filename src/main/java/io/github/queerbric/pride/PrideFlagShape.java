package io.github.queerbric.pride;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.math.MatrixStack;

public interface PrideFlagShape {
	void render(IntList colors, MatrixStack matrices, float x, float y, float width, float height);
}
