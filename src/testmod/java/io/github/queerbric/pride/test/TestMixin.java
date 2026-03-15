package io.github.queerbric.pride.test;

import io.github.queerbric.pride.PrideFlag;
import io.github.queerbric.pride.PrideFlags;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TestMixin extends Screen {
	@Unique
	private PrideFlag flag;

	protected TestMixin(Component title) {
		super(title);
	}

	@Inject(
			method = "init",
			at = @At("HEAD")
	)
	private void onInit(CallbackInfo ci) {
		this.flag = PrideFlags.getRandomFlag();

		this.addRenderableWidget(
				new PlainTextButton(0, this.height - 50, 50, 10, Component.literal("Reroll"), button -> this.flag = PrideFlags.getRandomFlag(), this.font)
		);
		this.addRenderableWidget(
				new PlainTextButton(0, this.height - 40, 50, 10, Component.literal("Reload resources"), button -> this.minecraft.reloadResourcePacks(), this.font)
		);
	}

	@Inject(
			method = "extractRenderState",
			at = @At("RETURN")
	)
	private void onRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
		this.flag.extractRenderState(graphics, 0, 0, 128, 64);
	}
}
