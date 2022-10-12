package io.github.queerbric.pride;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PrideFlags {
	private static List<PrideFlag> flags = new ArrayList<>();
	private static Map<String, PrideFlag> flagsById = new Object2ObjectOpenHashMap<>();
	private static final Random DEFAULT_RANDOM = new Random();

	// use Locale.ENGLISH to ensure we get a Gregorian calendar
	private static final boolean PRIDE_MONTH = Calendar.getInstance(Locale.ENGLISH).get(Calendar.MONTH) == Calendar.JUNE
			|| Boolean.getBoolean("everyMonthIsPrideMonth");

	protected static void setFlags(List<PrideFlag> flags) {
		PrideFlags.flags = Collections.unmodifiableList(flags);
		Object2ObjectOpenHashMap<String, PrideFlag> flagsById = new Object2ObjectOpenHashMap<String, PrideFlag>(flags.size());
		for (PrideFlag flag : flags) {
			flagsById.put(flag.getId(), flag);
		}
		PrideFlags.flagsById = Collections.unmodifiableMap(flagsById);
	}

	public static List<PrideFlag> getFlags() {
		return flags;
	}

	public static @Nullable PrideFlag getFlag(String id) {
		return flagsById.get(id);
	}

	public static @Nullable PrideFlag getRandomFlag(Random random) {
		if (flags.isEmpty()) return null;
		return flags.get(random.nextInt(flags.size()));
	}

	public static @Nullable PrideFlag getRandomFlag() {
		return getRandomFlag(DEFAULT_RANDOM);
	}

	public static boolean isPrideMonth() {
		return PRIDE_MONTH;
	}
}
