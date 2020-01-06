package com.github.f4irline.galleryapi

import com.github.f4irline.galleryapi.service.AmazonClient
import com.github.f4irline.galleryapi.service.CustomServletContextListener
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import java.nio.file.Path
import java.nio.file.Paths
import javax.annotation.PostConstruct
import javax.servlet.ServletContextListener

@EnableCaching
@SpringBootApplication
class GalleryApiApplication(
		private val amazonClient: AmazonClient
) {
	@Bean
	fun path(): Path {
		return Paths.get(System.getProperty("java.io.tmpdir")+"/image-gallery-api")
	}

	@Bean
	fun servletListener(): ServletListenerRegistrationBean<ServletContextListener> {
		val servletRegistrationBean: ServletListenerRegistrationBean<ServletContextListener> = ServletListenerRegistrationBean()
		servletRegistrationBean.listener = CustomServletContextListener(path())
		return servletRegistrationBean
	}

	@PostConstruct
	private fun init() {
		amazonClient.downloadImages()
	}
}

fun main(args: Array<String>) {
	runApplication<GalleryApiApplication>(*args)
}
