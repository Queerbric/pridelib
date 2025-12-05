import dev.lambdaurora.mcdev.api.MappingVariant
import dev.lambdaurora.mcdev.task.ConvertAccessWidenerToTransformer

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
	mappings(loom.officialMojangMappings())
	modImplementation(libs.fabric.loader)

	modImplementation(libs.yumi.mc.foundation)
	modImplementation(fabricApi.module("fabric-resource-loader-v1", project.property("fabric_api_version") as String))

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

val convertAWtoAT by tasks.registering(ConvertAccessWidenerToTransformer::class) {
	this.group = "generation"
	this.input = project.file("src/main/resources/pride.accesswidener")
	this.output = project.layout.buildDirectory.get().file("generated/accesstransformer.cfg")
}

val mojmapJar by tasks.registering(Jar::class) {
	this.group = "build"
	this.dependsOn(tasks.jar, convertAWtoAT)
	this.from(zipTree(tasks.jar.flatMap { it.archiveFile }))
	this.from(neoforge.output)
	this.from(convertAWtoAT) {
		into("META-INF")
	}
	this.archiveClassifier = "mojmap"
}

val mojmapSourcesJar by tasks.registering(Jar::class) {
	this.group = "build"
	this.dependsOn(tasks["sourcesJar"], convertAWtoAT)
	this.from(zipTree(tasks.getByName("sourcesJar", Jar::class).archiveFile))
	this.from(neoforge.java.sourceDirectories)
	this.from(neoforge.resources.sourceDirectories)
	this.from(convertAWtoAT) {
		into("META-INF")
	}
	this.archiveClassifier = "mojmap-sources"
}

mojmap.setJarArtifact(mojmapJar)
mojmap.setSourcesArtifact(mojmapSourcesJar)
tasks.build.get().dependsOn(mojmapJar, mojmapSourcesJar)

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
