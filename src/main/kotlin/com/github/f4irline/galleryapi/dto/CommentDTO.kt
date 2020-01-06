package com.github.f4irline.galleryapi.dto

class CommentDTO (
        val author: String,
        val comment: String,
        val id: Long?,
        val userCanDelete: Boolean?,
        val timeStamp: Long,
        val imageAuthor: String,
        val imageTitle: String,
        val imageId: Long?
)