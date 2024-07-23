package io.github.queerbric.pride;

import com.mojang.blaze3d.vertex.MatrixStack;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface PrideFlagShape {
	@Environment(EnvType.CLIENT)
	void render(IntList colors, MatrixStack matrices, float x, float y, float width, float height);
}
