package io.github.queerbric.pride;

import com.google.gson.Gson;
import io.github.moehreag.searchInResources.SearchableResourceManager;
import net.fabricmc.loader.api.FabricLoader;
import net.legacyfabric.fabric.api.resource.IdentifiableResourceReloadListener;
import net.legacyfabric.fabric.api.util.Identifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PrideLoader implements IdentifiableResourceReloadListener {
	private static final Identifier ID = new Identifier("pride", "flags");
	private static final Logger LOGGER = LogManager.getLogger("pride");
	private static final Gson GSON = new Gson();
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

	@Override
	public void reload(ResourceManager resourceManager) {
		applyFlags(loadFlags(resourceManager));
	}

	static class Config {
		String[] flags;
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	public static List<PrideFlag> loadFlags(ResourceManager manager) {
		List<PrideFlag> flags = new ArrayList<>();

		outer:
		for (Map.Entry<net.minecraft.util.Identifier, Resource> entry : ((SearchableResourceManager) manager)
				.findResources("flags", path -> path.getPath().endsWith(".json")).entrySet()){
			net.minecraft.util.Identifier id = entry.getKey();
			String[] parts = id.getPath().split("/");
			String name = parts[parts.length - 1];
			name = name.substring(0, name.length() - 5);

			try (InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream())) {
				PrideFlag.Properties builder = GSON.fromJson(reader, PrideFlag.Properties.class);
				for (String color : builder.colors) {
					if (!HEX_COLOR_PATTERN.matcher(color).matches()) {
						LOGGER.warn("[pride] Malformed flag data for flag " + name + ", " + color
								+ " is not a valid color, must be a six-digit hex color like #FF00FF");
						continue outer;
					}
				}

				PrideFlag flag = new PrideFlag(name, builder);
				flags.add(flag);
			} catch (Exception e) {
				LOGGER.warn("[pride] Malformed flag data for flag " + name, e);
			}
		}

		File prideFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "pride.json");
		if (prideFile.exists()) {
			try (FileReader reader = new FileReader(prideFile)) {
				Config config = GSON.fromJson(reader, Config.class);

				if (config.flags != null) {
					List<String> list = Arrays.asList(config.flags);
					flags.removeIf(flag -> !list.contains(flag.getId()));
				}
			} catch (Exception e) {
				LOGGER.warn("[pride] Malformed flag data for pride.json config");
			}
		} else {
			net.minecraft.util.Identifier id = new net.minecraft.util.Identifier("pride", "flags.json");

			try {
				Resource resource = manager.getResource(id);
				try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
					Config config = GSON.fromJson(reader, Config.class);

					if (config.flags != null) {
						List<String> list = Arrays.asList(config.flags);
						flags.removeIf(flag -> !list.contains(flag.getId()));
					}
				} catch (Exception e) {
					LOGGER.warn("[pride] Malformed flag data for flags.json", e);
				}

			} catch (Exception ignored){}
		}

		return flags;
	}

	private void applyFlags(List<PrideFlag> flags) {
		PrideFlags.setFlags(flags);
	}
}
