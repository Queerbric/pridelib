plugins {
	id("fabric-loom") version "1.10.+"
	id("dev.lambdaurora.mcdev") version "1.2.+"
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

dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	@Suppress("UnstableApiUsage")
	mappings(lambdamcdev.layered {
		officialMojangMappings()
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
		expand("version" to inputs.properties["version"])
	}
}

tasks.jar {
	inputs.property("archivesName", base.archivesName)

	from("LICENSE") {
		rename { "${it}_${inputs.properties["archivesName"]}" }
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
