package com.github.f4irline.galleryapi.repository

import com.github.f4irline.galleryapi.entity.Image
import com.github.f4irline.galleryapi.entity.User
import org.springframework.data.repository.CrudRepository

interface ImageRepository : CrudRepository<Image, Long> {
    fun findByUser(user: User): List<Image>
}