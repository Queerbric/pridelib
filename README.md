# PrideLib
PrideLib is a small JIJ dependency for modders who want to use a centralized data-driven and configurable source of pride flags in their mods. PrideLib can then be used to draw randomized graphical elements, or generate other colored visuals. PrideLib lets flags be added through resource packs, optionally part of a mod. Flags used by PrideLib can be configured either with resource packs, or a config file.

## Including PrideLib in a Mod
```gradle
repositories {
	maven {
		url = "https://jitpack.io"
	}
}
dependencies {
	modCompile include("com.github.queerbric:pridelib:${pridelib_version}")
}
```

## Adding Flags
Flags are json files located at `assets/[namespace]/flags/[name].json`. The namespace is unimportant, the name of the file is its ID, and only one of each ID will be loaded. Flags have a shape (which if omitted will default to `stripes`) and a set of colors. The following example is the trans flag.
```json
{
	"shape": "stripes",
	"colors": [
		"#55cdfc",
		"#f7a8b8",
		"#ffffff",
		"#f7a8b8",
		"#55cdfc"
	]
}
```

## Configuring Flags
Sometimes you don't actually want all of the available flags to be used in game. In this scenario, you'd use a flag configuration file. These are located in 2 different places, either `assets/pride/flags.json` for resource packs, or `config/pride.json` for a config file. Config files have a higher priority than resource pack ones. They use the same format, but the scenario where you might use one over the other may vary. Here is an example limiting available flags to only the trans flag, even if other flags are registered.
```json
{
	"flags": [
		"trans"
	]
}
```

## Default Flags
In order to provide a small amount of structure, PrideLib comes bundled with a small handful of common flags.

| ID | Description |
| --- | --- |
| `ace` | Asexual pride |
| `bi` | Bisexual pride |
| `lesbian` | Lesbian pride |
| `nonbinary` | Non-binary pride |
| `pan` | Pansexual pride |
| `rainbow` | Rainbow/queer/gay/umbrella pride |
| `trans` | Transgender pride |

## Using in Code
Flags can be obtained from the class `PrideFlags`, it also provides helper methods for selecting a random flag.