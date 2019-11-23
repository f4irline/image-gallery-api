package com.github.f4irline.galleryapi.dto

import com.github.f4irline.galleryapi.entity.Comment

class ImageDTO (
        val name: String,
        val description: String,
        val userCanDelete: Boolean?,
        val author: String?,
        val comments: List<CommentDTO>? = listOf(),
        val id: Long?,
        val file: ByteArray? = null
)