import dev.lambdaurora.mcdev.api.MappingVariant
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

java {
	sourceCompatibility = JavaVersion.toVersion(targetJavaVersion)
	targetCompatibility = JavaVersion.toVersion(targetJavaVersion)

	withSourcesJar()
}

val mojmap = lambdamcdev.setupMojmapRemapping()

val neoforge: SourceSet by sourceSets.creating {
	this.compileClasspath += sourceSets.main.get().compileClasspath
	this.runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

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

afterEvaluate {
	val shims: SourceSet by sourceSets.creating {
		this.compileClasspath += configurations["minecraftNamedCompile"]
	}

	dependencies {
		"shimsCompileOnly"(libs.fabric.loader) // Due to MC classes referring to EnvType.
		"shimsCompileOnly"(libs.neoforge.loader)
		"neoforgeCompileOnly"(shims.output)
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
	mappings(lambdamcdev.layered {
		officialMojangMappings()
		// Parchment is currently broken when used with the hacked mojmap layer due to remapping shenanigans.
		//parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${project.property("parchment_version")}@zip")
		mappings("dev.lambdaurora:yalmm:${libs.versions.minecraft.get()}+build.${project.property("yalmm_version")}")
	})
	modImplementation(libs.fabric.loader)

	modImplementation(libs.yumi.mc.foundation)
	modImplementation(fabricApi.module("fabric-resource-loader-v0", project.property("fabric_api_version") as String))

	"neoforgeCompileOnly"(libs.neoforge.loader)
	"neoforgeImplementation"(sourceSets.main.get().output)

	"mojmapImplementation"(libs.yumi.mc.foundation) {
		attributes {
			attribute(MappingVariant.ATTRIBUTE, objects.named(MappingVariant.MOJMAP))
		}
	}

	"testmodImplementation"(sourceSets.main.get().output)
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

tasks.getByName("processNeoforgeResources") {
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

val neoforgeJarTask = tasks.register("neoforgeJar", Jar::class) {
	this.group = "build"
	this.from(neoforge.output)
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")
}

val remapNeoforgeJar = tasks.register<RemapJarTask>("remapNeoforgeJarToIntermediary") {
	this.group = "remapping"
	this.dependsOn(neoforgeJarTask)
	this.inputFile.set(neoforgeJarTask.flatMap{ it.archiveFile })
	this.classpath.from(neoforge.compileClasspath)
	this.archiveClassifier = "neoforge-intermediary"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")

	addNestedDependencies = false // Jars will be included later.
}

val remapMojmap = mojmap.registerRemap(tasks.remapJar) {
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs")

	addNestedDependencies = false // Jars will be included later.
}

val remapNeoforgeJarToMojmap = mojmap.registerRemap("remapNeoforgeJarToMojmap") {
	this.dependsOn(remapNeoforgeJar)

	inputFile.set(remapNeoforgeJar.flatMap { it.archiveFile })

	this.archiveClassifier = "neoforge-mojmap"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")
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

val neoforgeSourcesJar by tasks.registering(Jar::class) {
	this.from(neoforge.java.sourceDirectories)
	this.from(neoforge.resources.sourceDirectories)
	this.archiveClassifier = "sources"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")
}

val remapNeoforgeSourcesJar = tasks.register<RemapSourcesJarTask>("remapNeoforgeSourcesJarToIntermediary") {
	this.group = "remapping"
	this.dependsOn(neoforgeSourcesJar)
	this.inputFile.set(neoforgeSourcesJar.flatMap { it.archiveFile })
	this.classpath.from(mojmap.sourceSet().compileClasspath)
	this.archiveClassifier = "neoforge-intermediary-sources"
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs/neoforge")
}

val remapSourcesMojmap = mojmap.registerSourcesRemap(tasks.remapSourcesJar) {
	this.destinationDirectory = project.layout.buildDirectory.get().dir("devlibs")
}

val remapNeoforgeSourcesJarToMojmap = mojmap.registerSourcesRemap("remapNeoforgeSourcesJarToMojmap") {
	this.dependsOn(remapNeoforgeSourcesJar)

	inputFile.set(remapNeoforgeSourcesJar.flatMap { it.archiveFile })

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
