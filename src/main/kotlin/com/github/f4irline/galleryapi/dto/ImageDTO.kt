package com.github.f4irline.galleryapi.dto

class ImageDTO (
        val name: String,
        val description: String,
        val userCanDelete: Boolean?,
        val author: String?,
        val comments: List<CommentDTO>? = listOf(),
        val score: Int? = 0,
        val userUpVoted: Boolean?,
        val userDownVoted: Boolean?,
        val id: Long?,
        val file: ByteArray? = null,
        val height: Int,
        val width: Int
)