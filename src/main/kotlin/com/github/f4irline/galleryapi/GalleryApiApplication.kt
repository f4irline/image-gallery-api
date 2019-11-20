package com.github.f4irline.galleryapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.nio.file.Path
import java.nio.file.Paths

@SpringBootApplication
class GalleryApiApplication {
	@Bean
	fun path(): Path {
		return Paths.get(System.getProperty("java.io.tmpdir"))
	}
}

fun main(args: Array<String>) {
	runApplication<GalleryApiApplication>(*args)
}
