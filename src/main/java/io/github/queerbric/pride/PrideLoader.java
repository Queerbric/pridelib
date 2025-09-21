package io.github.queerbric.pride;

import com.google.gson.Gson;
import dev.yumi.mc.core.api.YumiMods;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.io.Resource;
import net.minecraft.resources.io.ResourceManager;
import net.minecraft.resources.io.SinglePreparationResourceReloader;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class PrideLoader extends SinglePreparationResourceReloader<List<PrideFlag>> {
	public static final Identifier ID = Identifier.of("pride", "flags");
	private static final Logger LOGGER = LoggerFactory.getLogger("pride");
	private static final Gson GSON = new Gson();
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

	static class Config {
		String[] flags;
	}

	public @NotNull Identifier id() {
		return ID;
	}

	@Override
	public List<PrideFlag> prepare(ResourceManager manager, ProfilerFiller profiler) {
		return loadFlags(manager);
	}

	@Override
	public void apply(List<PrideFlag> list, ResourceManager manager, ProfilerFiller profiler) {
		applyFlags(list);
	}

	public static List<PrideFlag> loadFlags(ResourceManager manager) {
		var flags = new ArrayList<PrideFlag>();

		outer:
		for (var entry : manager.findResources("flags", path -> path.path().endsWith(".json")).entrySet()) {
			Identifier id = entry.getKey();
			String[] parts = id.path().split("/");
			String name = parts[parts.length - 1];
			name = name.substring(0, name.length() - 5);

			try (var reader = new InputStreamReader(entry.getValue().open())) {
				PrideFlag.Properties builder = GSON.fromJson(reader, PrideFlag.Properties.class);

				for (String color : builder.colors) {
					if (!HEX_COLOR_PATTERN.matcher(color).matches()) {
						LOGGER.warn("[pride] Malformed flag data for flag " + name + ", " + color
								+ " is not a valid color, must be a six-digit hex color like #FF00FF");
						continue outer;
					}
				}

				var flag = new PrideFlag(name, builder);
				flags.add(flag);
			} catch (Exception e) {
				LOGGER.warn("[pride] Malformed flag data for flag " + name, e);
			}
		}

		var pridePath = YumiMods.get().getConfigDirectory().resolve("pride.json");
		if (Files.exists(pridePath)) {
			try (var reader = Files.newBufferedReader(pridePath)) {
				Config config = GSON.fromJson(reader, Config.class);

				if (config.flags != null) {
					List<String> list = Arrays.asList(config.flags);
					flags.removeIf(flag -> !list.contains(flag.getId()));
				}
			} catch (Exception e) {
				LOGGER.warn("[pride] Malformed flag data for pride.json config");
			}
		} else {
			var id = Identifier.of("pride", "flags.json");

			Optional<Resource> resource = manager.getResource(id);
			if (resource.isPresent()) {
				try (var reader = new InputStreamReader(resource.get().open())) {
					Config config = GSON.fromJson(reader, Config.class);

					if (config.flags != null) {
						List<String> list = Arrays.asList(config.flags);
						flags.removeIf(flag -> !list.contains(flag.getId()));
					}
				} catch (Exception e) {
					LOGGER.warn("[pride] Malformed flag data for flags.json", e);
				}
			}
		}

		return flags;
	}

	private static void applyFlags(List<PrideFlag> flags) {
		PrideFlags.setFlags(flags);
	}
}
