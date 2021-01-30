package io.github.queerbric.pride;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.Gson;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class PrideLoader implements SimpleResourceReloadListener<List<PrideFlag>> {
	private static final Gson GSON = new Gson();

	class Config {
		String[] flags;
	}

	@Override
	public Identifier getFabricId() {
		return null;
	}

	@Override
	public CompletableFuture<List<PrideFlag>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			return getFlags(manager);
		});
	}

	@Override
	public CompletableFuture<Void> apply(List<PrideFlag> list, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			applyFlags(list);
		});
	}

	public static void firstLoad() {
		applyFlags(getFlags(MinecraftClient.getInstance().getResourceManager()));
	}

	public static List<PrideFlag> getFlags(ResourceManager manager) {
		List<PrideFlag> flags = new ArrayList<>();
	
		outer: for (Identifier id : manager.findResources("flags", path -> path.endsWith(".json"))) {
			String[] parts = id.getPath().split("/");
			String name = parts[parts.length - 1];
			name = name.substring(0, name.length() - 5);
			try {
				InputStreamReader reader = new InputStreamReader(manager.getResource(id).getInputStream());
				PrideFlag.Builder builder = GSON.fromJson(reader, PrideFlag.Builder.class);
				reader.close();
				for (String color : builder.colors) {
					if (!color.matches("#[0-9a-fA-F]{6}")) {
						System.err.println("[pride] Malformed flag data for flag " + name + ", " + color
								+ " is not a valid color, must match #[0-9a-fA-F]{6}");
						continue outer;
					}
				}
				PrideFlag flag = new PrideFlag(name, builder);
				flags.add(flag);
			} catch (Exception e) {
				System.err.println("[pride] Malformed flag data for flag " + name);
			}
		}

		File f = new File("config/pride.json");
		if (f.exists()) {
			try {
				InputStreamReader reader = new FileReader(f);
				Config config = GSON.fromJson(reader, Config.class);
				reader.close();
				if (config.flags != null) {
					List<String> list = Arrays.asList(config.flags);
					flags.removeIf(flag -> !list.contains(flag.getId()));
				}
			} catch (Exception e) {
				System.err.println("[pride] Malformed flag data for pride.json config");
			}
		} else {
			Identifier id = new Identifier("pride", "flags.json");
			if (manager.containsResource(id)) {
				try {
					InputStreamReader reader = new InputStreamReader(manager.getResource(id).getInputStream());
					Config config = GSON.fromJson(reader, Config.class);
					reader.close();
					if (config.flags != null) {
						List<String> list = Arrays.asList(config.flags);
						flags.removeIf(flag -> !list.contains(flag.getId()));
					}
				} catch (Exception e) {
					System.err.println("[pride] Malformed flag data for flags.json");
				}
			}
		}
		return flags;
	}

	public static void applyFlags(List<PrideFlag> flags) {
		PrideFlags.setFlags(flags);
	}
}
