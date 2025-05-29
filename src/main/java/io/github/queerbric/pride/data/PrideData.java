package io.github.queerbric.pride.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.function.Function;
import java.util.regex.Pattern;

public final class PrideData {
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

	public static final Codec<Integer> COLOR_CODEC = Codec.STRING.flatXmap(
			raw -> {
				if (!HEX_COLOR_PATTERN.matcher(raw).matches()) {
					return DataResult.error(() -> raw + " is not a valid color, must be a six-digit hex color like #ff00ff");
				}

				int color = Integer.parseInt(raw.substring(1), 16);
				return DataResult.success(color | 0xff000000);
			},
			color -> {
				int rgb = color & 0xffffff;

				var hex = Integer.toHexString(rgb);
				if (hex.length() < 6) {
					hex = "0".repeat(6 - hex.length()) + hex;
				}

				return DataResult.success("#" + hex);
			}
	);

	public static final Codec<IntList> COLOR_LIST_CODEC = COLOR_CODEC.listOf()
			.xmap(colors -> {
				var list = new IntArrayList(colors.size());
				list.addAll(colors);
				return list;
			}, Function.identity());

	private PrideData() {
		throw new UnsupportedOperationException("PrideData only contains static definitions.");
	}
}
