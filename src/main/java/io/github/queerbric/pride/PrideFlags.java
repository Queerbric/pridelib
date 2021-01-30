package io.github.queerbric.pride;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrideFlags {
	private static List<PrideFlag> flags = new ArrayList<>();
	private static Random defaultRandom = new Random();

	protected static void setFlags(List<PrideFlag> flags) {
		PrideFlags.flags = flags;
	}

	public static List<PrideFlag> getFlags() {
		return flags;
	}

	public static PrideFlag getRandomFlag(Random random) {
		return flags.get(random.nextInt(flags.size()));
	}

	public static PrideFlag getRandomFlag() {
		return getRandomFlag(defaultRandom);
	}
}
