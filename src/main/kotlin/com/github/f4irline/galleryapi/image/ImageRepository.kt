package com.github.f4irline.galleryapi.image

import org.springframework.data.repository.CrudRepository

interface ImageRepository : CrudRepository<Image, Long> {
    fun findByPath(text: String): Image
    fun findFirstByPath(text: String): Image
}