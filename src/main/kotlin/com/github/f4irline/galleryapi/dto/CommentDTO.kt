package com.github.f4irline.galleryapi.dto

import com.github.f4irline.galleryapi.entity.Comment

class CommentDTO (
        val author: String,
        val comment: String,
        val id: Long?,
        val userCanDelete: Boolean?
)