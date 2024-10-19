import net.fabricmc.loom.api.mappings.layered.MappingContext
import net.fabricmc.loom.api.mappings.layered.MappingLayer
import net.fabricmc.loom.api.mappings.layered.MappingsNamespace
import net.fabricmc.loom.api.mappings.layered.spec.MappingsSpec
import net.fabricmc.loom.configuration.providers.mappings.intermediary.IntermediaryMappingLayer
import net.fabricmc.loom.configuration.providers.mappings.utils.DstNameFilterMappingVisitor
import net.fabricmc.loom.util.download.DownloadException
import net.fabricmc.mappingio.MappingVisitor
import net.fabricmc.mappingio.adapter.MappingDstNsReorder
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch
import net.fabricmc.mappingio.format.proguard.ProGuardFileReader
import net.fabricmc.mappingio.tree.MemoryMappingTree
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

plugins {
	id("fabric-loom") version "1.8.+"
	`java-library`
	`maven-publish`
}

base.archivesName.set("pridelib")
val minecraftVersion = project.property("minecraft_version") as String
version = project.property("mod_version") as String
group = "io.github.queerbric"

// This field defines the Java version your mod target.
val targetJavaVersion = 21

val testmod: SourceSet by sourceSets.creating {
	this.compileClasspath += sourceSets.main.get().compileClasspath
	this.runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

loom {
	runtimeOnlyLog4j = true
	accessWidenerPath = file("src/main/resources/pride.accesswidener")

	runs {
		register("testmodClient") {
			client()
			source(testmod)
		}
	}
}

repositories {
	maven {
		name = "Gegy"
		url = uri("https://maven.gegy.dev/releases/")
	}
	maven {
		name = "ParchmentMC"
		url = uri("https://maven.parchmentmc.org")
	}
	mavenLocal()
}

// Based off Loom, this is required as the releases at the time of writing this buildscript have
// a flaw with the mapping layering preventing the usage of the usual MojangMappingLayer.
@Suppress("UnstableApiUsage")
internal data class MojangMappingLayer(
	val clientMappings: Path, val serverMappings: Path, val nameSyntheticMembers: Boolean,
	val intermediaryMappings: MemoryMappingTree, val logger: Logger
) : MappingLayer {
	@Throws(IOException::class)
	override fun visit(mappingVisitor: MappingVisitor) {
		val mojmap = MemoryMappingTree()

		// Filter out field names matching the pattern
		val nameFilter = DstNameFilterMappingVisitor(mojmap, SYNTHETIC_NAME_PATTERN)

		// Make official the source namespace
		val nsSwitch = MappingSourceNsSwitch(if (nameSyntheticMembers) mojmap else nameFilter, MappingsNamespace.OFFICIAL.toString())

		Files.newBufferedReader(clientMappings).use { clientBufferedReader ->
			Files.newBufferedReader(serverMappings).use { serverBufferedReader ->
				ProGuardFileReader.read(
					clientBufferedReader,
					MappingsNamespace.NAMED.toString(),
					MappingsNamespace.OFFICIAL.toString(),
					nsSwitch
				)
				ProGuardFileReader.read(
					serverBufferedReader,
					MappingsNamespace.NAMED.toString(),
					MappingsNamespace.OFFICIAL.toString(),
					nsSwitch
				)
			}
		}

		intermediaryMappings.accept(MappingDstNsReorder(mojmap, MappingsNamespace.INTERMEDIARY.toString()))

		val switch = MappingSourceNsSwitch(MappingDstNsReorder(mappingVisitor, MappingsNamespace.NAMED.toString()), MappingsNamespace.INTERMEDIARY.toString(), true)
		mojmap.accept(switch)
	}

	override fun getSourceNamespace(): MappingsNamespace {
		return MappingsNamespace.INTERMEDIARY
	}

	override fun dependsOn(): List<Class<out MappingLayer?>> {
		return listOf(IntermediaryMappingLayer::class.java)
	}

	companion object {
		private val SYNTHETIC_NAME_PATTERN: Pattern = Pattern.compile("^(access|this|val\\\$this|lambda\\$.*)\\$[0-9]+$")
	}
}

@Suppress("UnstableApiUsage")
internal data class MojangMappingsSpec(val nameSyntheticMembers: Boolean) : MappingsSpec<MojangMappingLayer?> {
	override fun createLayer(context: MappingContext): MojangMappingLayer {
		val versionInfo = context.minecraftProvider().versionInfo
		val clientDownload = versionInfo.download(MANIFEST_CLIENT_MAPPINGS)
		val serverDownload = versionInfo.download(MANIFEST_SERVER_MAPPINGS)

		if (clientDownload == null) {
			throw RuntimeException("Failed to find official mojang mappings for " + context.minecraftVersion())
		}

		val clientMappings = context.workingDirectory("mojang").resolve("client.txt")
		val serverMappings = context.workingDirectory("mojang").resolve("server.txt")

		try {
			context.download(clientDownload.url())
				.sha1(clientDownload.sha1())
				.downloadPath(clientMappings)

			context.download(serverDownload.url())
				.sha1(serverDownload.sha1())
				.downloadPath(serverMappings)
		} catch (e: DownloadException) {
			throw UncheckedIOException("Failed to download mappings", e)
		}

		return MojangMappingLayer(
			clientMappings,
			serverMappings,
			nameSyntheticMembers,
			context.intermediaryTree().get(),
			context.logger
		)
	}

	companion object {
		// Keys in dependency manifest
		private const val MANIFEST_CLIENT_MAPPINGS = "client_mappings"
		private const val MANIFEST_SERVER_MAPPINGS = "server_mappings"
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		addLayer(MojangMappingsSpec(false))
		// Parchment is currently broken when used with the hacked mojmap layer due to remapping shenanigans.
		//parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${project.property("parchment_version")}@zip")
		mappings("dev.lambdaurora:yalmm:${minecraftVersion}+build.${project.property("yalmm_version")}")
	})
	modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

	modImplementation(fabricApi.module("fabric-resource-loader-v0", project.property("fabric_api_version") as String))

	"testmodImplementation"(sourceSets.main.get().output)
}

java {
	sourceCompatibility = JavaVersion.toVersion(targetJavaVersion)
	targetCompatibility = JavaVersion.toVersion(targetJavaVersion)

	withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	options.release.set(targetJavaVersion)
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])

			pom {
				name = "Pride Lib"
				description = "A unified library for data-driven flags"
			}
		}
	}

	repositories {
		mavenLocal()
		maven {
			name = "BuildDirLocal"
			url = uri("${layout.buildDirectory.get()}/repo")
		}

		val prideLibMaven = System.getenv("PRIDELIB_MAVEN")
		if (prideLibMaven != null) {
			maven {
				name = "PrideLibMaven"
				url = uri(prideLibMaven)
				credentials {
					username = (project.findProperty("gpr.user") ?: System.getenv("MAVEN_USERNAME")) as String
					password = (project.findProperty("gpr.key") ?: System.getenv("MAVEN_PASSWORD")) as String
				}
			}
		}
	}
}
