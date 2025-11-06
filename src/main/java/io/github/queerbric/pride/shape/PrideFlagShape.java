package io.github.queerbric.pride.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.github.queerbric.pride.PrideClient;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;

public interface PrideFlagShape {
	/**
	 * Represents the Codec of a pride flag shape.
	 */
	Codec<PrideFlagShape> CODEC = Codec.withAlternative(
			Type.CODEC.dispatch("shape", PrideFlagShape::type, Type::codec),
			HorizontalPrideFlagShape.CODEC.codec()
	);

	Type HORIZONTAL_STRIPES_TYPE = Type.register(
			PrideClient.id("horizontal_stripes"),
			HorizontalPrideFlagShape.CODEC
	);
	Type VERTICAL_STRIPES_TYPE = Type.register(
			PrideClient.id("vertical_stripes"),
			VerticalPrideFlagShape.CODEC
	);
	Type CIRCLE_TYPE = Type.register(PrideClient.id("circle"), CirclePrideFlagShape.CODEC);
	Type ARROW_TYPE = Type.register(PrideClient.id("arrow"), ArrowPrideFlagShape.CODEC);

	Type type();

	@Environment(EnvType.CLIENT)
	void render(GuiGraphics graphics, int x, int y, int width, int height);

	/**
	 * Represents a type of pride flag shape.
	 *
	 * @param id the identifier of this type
	 * @param codec the codec of this type
	 */
	record Type(Identifier id, MapCodec<? extends PrideFlagShape> codec) {
		private static final Map<Identifier, Type> TYPES = new Object2ObjectOpenHashMap<>();
		/**
		 * Represents the Codec of a pride flag shape type.
		 */
		public static final Codec<Type> CODEC = Identifier.CODEC.flatXmap(
				name -> Optional.ofNullable(TYPES.get(name))
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> "Unknown element name:" + name)),
				type -> DataResult.success(type.id)
		);

		public static Type register(
				Identifier id,
				MapCodec<? extends PrideFlagShape> codec
		) {
			var type = new Type(id, codec);
			TYPES.put(id, type);
			return type;
		}
	}
}
