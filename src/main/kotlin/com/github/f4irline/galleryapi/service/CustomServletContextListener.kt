package com.github.f4irline.galleryapi.service

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

class CustomServletContextListener : ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent?) {
        super.contextInitialized(sce)
        println("Callback triggered - ContextInitialized")
    }
}