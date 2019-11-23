package com.github.f4irline.galleryapi.service

import java.io.File
import java.nio.file.Path
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

class CustomServletContextListener(private val path: Path) : ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent?) {
        super.contextInitialized(sce)

        val directory = File(this.path.resolve("").toUri())
        if (!directory.exists()) {
            directory.mkdir()
        }
        println("Callback triggered - ContextInitialized")
    }
}