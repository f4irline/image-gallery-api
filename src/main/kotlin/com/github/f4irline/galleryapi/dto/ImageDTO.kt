package com.github.f4irline.galleryapi.dto

import com.github.f4irline.galleryapi.entity.Comment

class ImageDTO (
        val name: String,
        val description: String,
        val author: String?,
        val comments: MutableSet<Comment>? = mutableSetOf(),
        val id: Long?,
        val file: ByteArray? = null
)