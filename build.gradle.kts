import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.api.mappings.layered.MappingsNamespace
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import task.AssembleNeoForgeJarTask

plugins {
	id("pride")
	alias(libs.plugins.loom)
	alias(libs.plugins.lambdamcdev)
	`java-library`
	`maven-publish`
}

base.archivesName.set("pridelib")

// This field defines the Java version your mod target.
val targetJavaVersion = Integer.parseInt(project.property("java_version") as String)

val mojmap = lambdamcdev.setupMojmapRemapping()
mojmap.sourceSet().compileClasspath += sourceSets.main.get().compileClasspath
mojmap.sourceSet().runtimeClasspath += sourceSets.main.get().runtimeClasspath
mojmap.sourceSet().java {
	this.srcDir("src/neoforge/java")
}
mojmap.sourceSet().resources {
	this.srcDir("src/neoforge/resources")
}

loom {
	runtimeOnlyLog4j = true
}

afterEvaluate {
	val shims: SourceSet by sourceSets.creating {
		this.compileClasspath += configurations["minecraftNamedCompile"]
	}

	dependencies {
		"shimsCompileOnly"(libs.fabric.loader) // Due to MC classes referring to EnvType.
		"shimsCompileOnly"(libs.neoforge.loader)
		"mojmapCompileOnly"(shims.output)
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
	maven {
		name = "NeoForge"
		url = uri("https://maven.neoforged.net/")
		content {
			includeGroupByRegex("net\\.neoforged.*")
			includeGroupByRegex("cpw\\.mods.*")
		}
	}
	mavenLocal()
}

dependencies {
	minecraft(libs.minecraft)
	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		officialMojangMappings()
		// Parchment is currently broken when used with the hacked mojmap layer due to remapping shenanigans.
		//parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${project.property("parchment_version")}@zip")
		mappings("dev.lambdaurora:yalmm:${libs.versions.minecraft.get()}+build.${project.property("yalmm_version")}")
	})
	modImplementation(libs.fabric.loader)

	modImplementation(libs.yumi.mc.foundation)
	modImplementation(fabricApi.module("fabric-resource-loader-v0", project.property("fabric_api_version") as String))

	"mojmapCompileOnly"(libs.neoforge.loader)
	"mojmapImplementation"(sourceSets.main.get().output)
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
		expand("version" to (inputs.properties["version"] as String))
	}
}

tasks.getByName("processMojmapResources") {
	this as ProcessResources
	inputs.property("version", project.version)

	filesMatching("META-INF/neoforge.mods.toml") {
		expand("version" to (inputs.properties["version"] as String))
	}
}

tasks.jar {
	inputs.property("archivesName", base.archivesName)

	from("LICENSE") {
		rename { "${it}_${inputs.properties["archivesName"]}" }
	}
}

val neoforgeJarTask = tasks.getByName("mojmapJar", Jar::class)

val remapNeoforgeJar = tasks.register<RemapJarTask>("remapNeoforgeJarToIntermediary") {
	this.group = "remapping"
	this.dependsOn(neoforgeJarTask)
	this.inputFile.set(neoforgeJarTask.archiveFile)
	this.classpath.from(mojmap.sourceSet().compileClasspath)
	this.archiveClassifier = "neoforge-intermediary"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")

	addNestedDependencies = false // Jars will be included later.
}

val remapMojmap by tasks.registering(RemapJarTask::class) {
	this.group = "remapping"
	this.dependsOn(tasks.remapJar)

	inputFile.set(tasks.remapJar.flatMap { it.archiveFile })
	customMappings.from(mojmap.mappingsConfiguration())
	sourceNamespace = "intermediary"
	targetNamespace = "named"
	archiveClassifier = "mojmap"
	classpath.setFrom((loom as LoomGradleExtension).getMinecraftJars(MappingsNamespace.INTERMEDIARY))

	this.archiveClassifier = "mojmap"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs")

	addNestedDependencies = false // Jars will be included later.
}

val remapNeoforgeJarToMojmap by tasks.registering(RemapJarTask::class) {
	this.group = "remapping"
	this.dependsOn(remapNeoforgeJar)

	inputFile.set(remapNeoforgeJar.flatMap { it.archiveFile })
	customMappings.from(mojmap.mappingsConfiguration())
	sourceNamespace = "intermediary"
	targetNamespace = "named"
	classpath.setFrom((loom as LoomGradleExtension).getMinecraftJars(MappingsNamespace.INTERMEDIARY))

	this.archiveClassifier = "neoforge-mojmap"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")

	this.nestedJars.setFrom(this.nestedJars.files.stream().filter {
		it.name.endsWith("-mojmap.jar")
	}.toList())
}

val assembleNeoforgeJar by tasks.registering(AssembleNeoForgeJarTask::class) {
	this.group = "build"
	this.dependsOn(
		remapMojmap,
		remapNeoforgeJarToMojmap,
	)

	this.mojmapJar.set(remapMojmap.flatMap { it.archiveFile })
	this.neoforgeJar.set(remapNeoforgeJarToMojmap.flatMap { it.archiveFile })
	this.archiveClassifier = "mojmap"
}

val neoforgeSourcesJarTask = tasks.getByName("mojmapSourcesJar", Jar::class) {
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")
}

val remapNeoforgeSourcesJar = tasks.register<RemapSourcesJarTask>("remapNeoforgeSourcesJarToIntermediary") {
	this.group = "remapping"
	this.dependsOn(neoforgeSourcesJarTask)
	this.inputFile.set(neoforgeSourcesJarTask.archiveFile)
	this.classpath.from(mojmap.sourceSet().compileClasspath)
	this.archiveClassifier = "neoforge-intermediary-sources"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")
}

val remapSourcesMojmap by tasks.registering(RemapSourcesJarTask::class) {
	this.group = "remapping"
	this.dependsOn(tasks.remapSourcesJar)

	inputFile.set(tasks.remapSourcesJar.flatMap { it.archiveFile })
	customMappings.from(mojmap.mappingsConfiguration())
	sourceNamespace = "intermediary"
	targetNamespace = "named"
	archiveClassifier = "mojmap"
	classpath.setFrom((loom as LoomGradleExtension).getMinecraftJars(MappingsNamespace.INTERMEDIARY))

	this.archiveClassifier = "mojmap-sources"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs")
}

val remapNeoforgeSourcesJarToMojmap by tasks.registering(RemapSourcesJarTask::class) {
	this.group = "remapping"
	this.dependsOn(remapNeoforgeSourcesJar)

	inputFile.set(remapNeoforgeSourcesJar.flatMap { it.archiveFile })
	customMappings.from(mojmap.mappingsConfiguration())
	sourceNamespace = "intermediary"
	targetNamespace = "named"
	classpath.setFrom((loom as LoomGradleExtension).getMinecraftJars(MappingsNamespace.INTERMEDIARY))

	this.archiveClassifier = "neoforge-mojmap-sources"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")
}

val assembleNeoforgeSourcesJar by tasks.registering(AssembleNeoForgeJarTask::class) {
	this.group = "build"
	this.dependsOn(
		remapSourcesMojmap,
		remapNeoforgeSourcesJarToMojmap,
	)

	this.mojmapJar.set(remapSourcesMojmap.flatMap { it.archiveFile })
	this.neoforgeJar.set(remapNeoforgeSourcesJarToMojmap.flatMap { it.archiveFile })
	this.archiveClassifier = "mojmap-sources"
}

mojmap.setJarArtifact(assembleNeoforgeJar)
mojmap.setSourcesArtifact(assembleNeoforgeSourcesJar)
tasks.build.get().dependsOn(assembleNeoforgeJar, assembleNeoforgeSourcesJar)

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
