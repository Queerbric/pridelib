package io.github.queerbric.pride;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.yumi.mc.core.api.YumiMods;
import io.github.queerbric.pride.shape.PrideFlagShape;
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

public class PrideLoader extends SinglePreparationResourceReloader<List<PrideFlag>> {
	public static final Identifier ID = Identifier.of("pride", "flags");
	private static final Logger LOGGER = LoggerFactory.getLogger("pride");
	private static final Gson GSON = new Gson();

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
				var rawJson = JsonParser.parseReader(reader);

				if (!rawJson.isJsonObject()) {
					LOGGER.warn("[pride] Failed to pride flag \"{}\". Expected JSON object in file.", id);
					continue;
				}

				var loaded = PrideFlagShape.CODEC.parse(JsonOps.INSTANCE, rawJson);

				loaded.ifError(error -> {
					LOGGER.warn("[pride] Failed to load pride flag \"{}\" due to error: {}", id, error.message());
				});

				var result = loaded.result();
				if (result.isPresent()) {
					flags.add(new PrideFlag(name, result.get()));
				}
			} catch (Exception e) {
				LOGGER.warn("[pride] Malformed flag data for flag {}", name, e);
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
