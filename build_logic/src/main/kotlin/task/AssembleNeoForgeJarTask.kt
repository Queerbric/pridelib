package task

import dev.lambdaurora.mcdev.api.AccessWidenerToTransformer
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.Predicate
import javax.inject.Inject

abstract class AssembleNeoForgeJarTask @Inject constructor() : Jar() {
	@get:InputFile
	abstract val mojmapJar: RegularFileProperty

	@get:InputFile
	abstract val neoforgeJar: RegularFileProperty

	@TaskAction
	fun assemble() {
		val outputJar = this.archiveFile.get().asFile.toPath()
		val mojmapJarPath = this.mojmapJar.get().asFile.toPath()
		val neoforgeJarPath = this.neoforgeJar.get().asFile.toPath()

		FileSystems.newFileSystem(outputJar).use { outFs ->
			FileSystems.newFileSystem(mojmapJarPath).use { mojmapFs ->
				FileSystems.newFileSystem(neoforgeJarPath).use { neoFs ->
					neoFs.rootDirectories.forEach { rootDir ->
						Files.list(rootDir).use { stream ->
							stream.forEach { this.copy(it, outFs.getPath(it.toString())) { true } }
						}
					}

					mojmapFs.rootDirectories.forEach { rootDir ->
						Files.list(rootDir).use { stream ->
							stream.forEach {
								this.copy(it, outFs.getPath(it.toString())) { path ->
									path.fileName.toString() != "fabric.mod.json"
											&& !path.fileName.toString().endsWith(".accesswidener")
											&& !path.toString().contains("platform/fabric")
								}
							}
						}
					}

					val accessWidenerPath = mojmapFs.getPath("pride.accesswidener")

					if (Files.exists(accessWidenerPath)) {
						AccessWidenerToTransformer.convert(
							accessWidenerPath,
							outFs.getPath("META-INF/accesstransformer.cfg")
						)
					}
				}
			}
		}
	}

	private fun copy(source: Path, target: Path, predicate: Predicate<Path>) {
		if (target.parent != null) {
			Files.createDirectories(target.parent)
		}

		if (Files.isRegularFile(source) && predicate.test(source)) {
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
			return
		}

		Files.walkFileTree(source, object : SimpleFileVisitor<Path>() {
			fun resolve(subSource: Path): Path {
				val relative = source.relativize(subSource)
				return target.resolve(relative)
			}

			override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
				if (predicate.test(dir)) {
					Files.createDirectories(this.resolve(dir))
					return FileVisitResult.CONTINUE
				} else {
					return FileVisitResult.SKIP_SUBTREE
				}
			}

			override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
				if (predicate.test(file)) {
					Files.copy(
						file,
						this.resolve(file),
						StandardCopyOption.REPLACE_EXISTING
					)
				}
				return FileVisitResult.CONTINUE
			}
		})
	}
}