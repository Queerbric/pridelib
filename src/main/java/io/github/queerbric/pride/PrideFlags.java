package io.github.queerbric.pride;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

public class PrideFlags {
	private static List<PrideFlag> flags = new ArrayList<>();
	private static Map<String, PrideFlag> flagsById = new HashMap<>();
	private static Random defaultRandom = new Random();
	
	// use Locale.ENGLISH to ensure we get a Gregorian calendar
	private static final boolean PRIDE_MONTH = Calendar.getInstance(Locale.ENGLISH).get(Calendar.MONTH) == Calendar.JUNE
			|| Boolean.getBoolean("everyMonthIsPrideMonth");

	protected static void setFlags(List<PrideFlag> flags) {
		PrideFlags.flags = Collections.unmodifiableList(flags);
		Map<String, PrideFlag> bldr = new HashMap<>(flags.size());
		for (PrideFlag flag : flags) {
			bldr.put(flag.getId(), flag);
		}
		flagsById = Collections.unmodifiableMap(bldr);
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
		return getRandomFlag(defaultRandom);
	}
	
	public static boolean isPrideMonth() {
		return PRIDE_MONTH;
	}
}
