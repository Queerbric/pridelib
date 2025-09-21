import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.api.mappings.layered.MappingsNamespace
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register

plugins {
	id("fabric-loom") version "1.11.+"
	id("dev.lambdaurora.mcdev") version "1.5.+"
	`java-library`
	`maven-publish`
}

base.archivesName.set("pridelib")

// This field defines the Java version your mod target.
val targetJavaVersion = Integer.parseInt(project.property("java_version") as String)

val neoforge: SourceSet by sourceSets.creating {
	this.compileClasspath += sourceSets.main.get().compileClasspath
	this.runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

loom {
	runtimeOnlyLog4j = true
}

afterEvaluate {
	val shims: SourceSet by sourceSets.creating {
		this.compileClasspath += configurations["minecraftNamedCompile"]
	}

	dependencies {
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
	mappings(loom.layered {
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

tasks.getByName("processNeoforgeResources") {
	this as ProcessResources
	inputs.property("version", project.version)

	filesMatching("META-INF/neoforge.mods.toml") {
		expand("version" to inputs.properties["version"])
	}
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

val neoforgeJar = tasks.register<Jar>("neoforgeJar") {
	this.group = "build"
	this.from(neoforge.output)
	this.archiveClassifier = "neoforge-dev"
	this.destinationDirectory = project.file("build/devlibs/neoforge")
}

val remapNeoforgeJar = tasks.register<RemapJarTask>("remapNeoforgeJarToIntermediary") {
	this.group = "remapping"
	this.dependsOn(neoforgeJar.get())
	this.inputFile.set(neoforgeJar.get().archiveFile)
	this.classpath.from(neoforge.compileClasspath)
	this.archiveClassifier = "neoforge-intermediary"
	this.destinationDirectory = project.file("build/devlibs/neoforge")

	addNestedDependencies = false // Jars will be included later.
}
tasks.build.get().dependsOn(remapNeoforgeJar)

//region Mojmap
val mojmap = lambdamcdev.setupMojmapRemapping()

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
	this.destinationDirectory = project.file("build/devlibs")

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
	this.destinationDirectory = project.file("build/devlibs/neoforge")

	this.nestedJars.setFrom(this.nestedJars.files.stream().filter {
		it.name.endsWith("-mojmap.jar")
	}.toList())
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
