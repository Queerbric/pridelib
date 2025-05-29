package io.github.queerbric.pride;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.github.queerbric.pride.shape.PrideFlagShape;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.io.Resource;
import net.minecraft.resources.io.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PrideLoader implements SimpleResourceReloadListener<List<PrideFlag>> {
	private static final Identifier ID = Identifier.of("pride", "flags");
	private static final Logger LOGGER = LoggerFactory.getLogger("pride");
	private static final Gson GSON = new Gson();

	static class Config {
		String[] flags;
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	public CompletableFuture<List<PrideFlag>> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> loadFlags(manager));
	}

	@Override
	public CompletableFuture<Void> apply(List<PrideFlag> list, ResourceManager manager, Executor executor) {
		return CompletableFuture.runAsync(() -> applyFlags(list));
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

		var prideFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "pride.json");
		if (prideFile.exists()) {
			try (var reader = new FileReader(prideFile)) {
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
