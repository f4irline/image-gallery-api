package com.github.f4irline.galleryapi

import com.github.f4irline.galleryapi.service.CustomServletContextListener
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean
import org.springframework.context.annotation.Bean
import java.nio.file.Path
import java.nio.file.Paths
import javax.servlet.ServletContextListener

@SpringBootApplication
class GalleryApiApplication {
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
}

fun main(args: Array<String>) {
	runApplication<GalleryApiApplication>(*args)
}
