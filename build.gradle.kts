plugins {
	id("fabric-loom") version "1.7.+"
	`java-library`
	`maven-publish`
}

base.archivesName.set("pridelib")
val minecraftVersion = project.property("minecraft_version") as String
version = "${project.property("mod_version")}+${minecraftVersion}"
group = "io.github.queerbric"

// This field defines the Java version your mod target.
val targetJavaVersion = 21

repositories {
	maven {
		name = "Quilt"
		url = uri("https://maven.quiltmc.org/repository/release")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	@Suppress("UnstableApiUsage")
	mappings("org.quiltmc:quilt-mappings:${minecraftVersion}+build.${project.property("quilt_mappings")}:intermediary-v2")
	modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

	modImplementation(fabricApi.module("fabric-resource-loader-v0", project.property("fabric_api_version") as String))
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
