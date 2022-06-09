# PrideLib

![Java 16](https://img.shields.io/badge/language-Java%2017-9B599A.svg?style=flat-square)
[![GitHub license](https://img.shields.io/github/license/Queerbric/pridelib?style=flat-square)](https://raw.githubusercontent.com/Queerbric/pridelib/1.19/LICENSE)
![Environment: Client](https://img.shields.io/badge/environment-client-1976d2?style=flat-square)
[![Mod loader: Fabric]][fabric]
![Version](https://img.shields.io/github/v/tag/Queerbric/pridelib?label=version&style=flat-square)

PrideLib is a small JIJ dependency for modders who want to use a centralized data-driven and configurable source of pride flags in their mods. PrideLib can then be used to draw randomized graphical elements, or generate other colored visuals. PrideLib lets flags be added through resource packs, optionally part of a mod. Flags used by PrideLib can be configured either with resource packs, or a config file.

## Including PrideLib in a Mod
```gradle
repositories {
	maven {
		url 'https://maven.gegy.dev'
	}
}
dependencies {
	modImplementation include("io.github.queerbric:pridelib:${pridelib_version}")
}
```

## Adding Flags
Flags are json files located at `assets/[namespace]/flags/[name].json`. The namespace is unimportant, the name of the file is its ID, and only one of each ID will be loaded. Flags have a shape (which if omitted will default to `pride:horizontal_stripes`) and a set of colors. The following example is the trans flag.
```json
{
	"shape": "horizontal_stripes",
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
		"transgender"
	]
}
```

## Default Flags
In order to provide a small amount of structure, PrideLib comes bundled with a small handful of common flags.

| ID | Description |
| --- | --- |
| `asexual` | Asexual pride |
| `bisexual` | Bisexual pride |
| `intersex` | Intersex pride |
| `lesbian` | Lesbian pride |
| `nonbinary` | Non-binary pride |
| `pansexual` | Pansexual pride |
| `rainbow` | Rainbow/queer/gay/umbrella pride |
| `transgender` | Transgender pride |

## Using in Code
Flags can be obtained from the class `PrideFlags`, it also provides helper methods for selecting a random flag. The returned `PrideFlag` type also offers a `render` method for quick 2D rendering in a GUI or similar. For more complex usage, you can retrieve the raw data. **Note that if no flags are available for whatever reason, getRandomFlag will return `null`**.

Since you may particularly want to make use of this library during Pride Month (i.e. June), there is also a utility method offered in `PrideFlags`: `isPrideMonth`. This also checks for a system property, `everyMonthIsPrideMonth`, which if it is set to `true` the method will always return `true`. You can do this by adding this to your JVM arguments: `-DeveryMonthIsPrideMonth=true`

## Built-in Flag Shapes
The following flag shapes are built-in to PrideLib. Any mod can contribute additional shapes by registering them with `PrideFlagShapes`.

## horizontal_stripes
A basic flag consisting of an arbitrary number of equally-sized colored stripes. Each color is a stripe. The first color is the top stripe, the last is the bottom stripe.

You can accomplish non-equally-sized stripes by repeating the same color multiple times.

Examples: rainbow flag, trans flag, pan flag, nonbinary flag, many more

## vertical_stripes
A basic flag consisting of an arbitrary number of equally-sized colored stripes. Each color is a stripe. The first color is the left stripe, the last is the right stripe.

You can accomplish non-equally-sized stripes by repeating the same color multiple times.

Examples: androgynous flag (not built-in)

## circle
A flag with a solid background and a hollow circle in the foreground. The first color is the background, the second color is the color of the circle. Additional colors are ignored.

Examples: intersex flag

## arrow
A flag with a triangle pointing to the right on the left side of the flag. The first color is the color of the triangle, the rest are treated as in horizontal_stripes and make up the background.

Examples: demisexual flag (not built-in)

## progress
Something very close to the "Progress" pride flag design. Hardcoded due to its unique shape; ignores all colors.

Not provided by any built-in flags; you can add this JSON file to provide it:

```json
{
	"shape": "progress",
	"colors": []
}
```

[fabric]: https://fabricmc.net
[Mod loader: Fabric]: https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAAFHGlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNS42LWMxNDIgNzkuMTYwOTI0LCAyMDE3LzA3LzEzLTAxOjA2OjM5ICAgICAgICAiPiA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPiA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RFdnQ9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZUV2ZW50IyIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ0MgMjAxOCAoV2luZG93cykiIHhtcDpDcmVhdGVEYXRlPSIyMDE4LTEyLTE2VDE2OjU0OjE3LTA4OjAwIiB4bXA6TW9kaWZ5RGF0ZT0iMjAxOS0wNy0yOFQyMToxNzo0OC0wNzowMCIgeG1wOk1ldGFkYXRhRGF0ZT0iMjAxOS0wNy0yOFQyMToxNzo0OC0wNzowMCIgZGM6Zm9ybWF0PSJpbWFnZS9wbmciIHBob3Rvc2hvcDpDb2xvck1vZGU9IjMiIHBob3Rvc2hvcDpJQ0NQcm9maWxlPSJzUkdCIElFQzYxOTY2LTIuMSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDowZWRiMWMyYy1mZjhjLWU0NDEtOTMxZi00OTVkNGYxNGM3NjAiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6MGVkYjFjMmMtZmY4Yy1lNDQxLTkzMWYtNDk1ZDRmMTRjNzYwIiB4bXBNTTpPcmlnaW5hbERvY3VtZW50SUQ9InhtcC5kaWQ6MGVkYjFjMmMtZmY4Yy1lNDQxLTkzMWYtNDk1ZDRmMTRjNzYwIj4gPHhtcE1NOkhpc3Rvcnk+IDxyZGY6U2VxPiA8cmRmOmxpIHN0RXZ0OmFjdGlvbj0iY3JlYXRlZCIgc3RFdnQ6aW5zdGFuY2VJRD0ieG1wLmlpZDowZWRiMWMyYy1mZjhjLWU0NDEtOTMxZi00OTVkNGYxNGM3NjAiIHN0RXZ0OndoZW49IjIwMTgtMTItMTZUMTY6NTQ6MTctMDg6MDAiIHN0RXZ0OnNvZnR3YXJlQWdlbnQ9IkFkb2JlIFBob3Rvc2hvcCBDQyAyMDE4IChXaW5kb3dzKSIvPiA8L3JkZjpTZXE+IDwveG1wTU06SGlzdG9yeT4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz4/HiGMAAAAtUlEQVRYw+XXrQqAMBQF4D2P2eBL+QIG8RnEJFaNBjEum+0+zMQLtwwv+wV3ZzhhMDgfJ0wUSinxZUQWgKos1JP/AbD4OneIDyQPwCFniA+EJ4CaXm4TxAXCC0BNHgLhAdAnx9hC8PwGSRtAFVMQjF7cNTWED8B1cgwW20yfJgAvrssAsZ1cB3g/xckAxr6FmCDU5N6f488BrpCQ4rQBJkiMYh4ACmLzwOQF0CExinkCsvw7vgGikl+OotaKRwAAAABJRU5ErkJggg==
