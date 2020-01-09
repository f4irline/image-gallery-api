package com.github.f4irline.galleryapi.repository

import com.github.f4irline.galleryapi.entity.Comment
import com.github.f4irline.galleryapi.entity.User
import org.springframework.data.repository.CrudRepository

interface CommentRepository : CrudRepository<Comment, Long> {
    fun findByUserOrderByCommentIdDesc(user: User): List<Comment>
}