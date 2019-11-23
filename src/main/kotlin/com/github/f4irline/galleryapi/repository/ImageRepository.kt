package com.github.f4irline.galleryapi.repository

import com.github.f4irline.galleryapi.entity.Image
import com.github.f4irline.galleryapi.entity.User
import org.springframework.data.repository.CrudRepository

interface ImageRepository : CrudRepository<Image, Long> {
    fun findByUserOrderByImageIdDesc(user: User): List<Image>
    fun findAllByOrderByImageIdDesc(): List<Image>
}