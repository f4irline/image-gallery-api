package com.github.f4irline.galleryapi.controller

import com.github.f4irline.galleryapi.entity.Comment
import com.github.f4irline.galleryapi.repository.CommentRepository
import com.github.f4irline.galleryapi.repository.ImageRepository
import com.github.f4irline.galleryapi.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.util.*

@RestController
@RequestMapping("/api/comment")
class CommentController(
        private val imageRepository: ImageRepository,
        private val userRepository: UserRepository,
        private val commentRepository: CommentRepository
) {
    @PostMapping("/{imageId}/{userToken}")
    @Throws
    fun addComment(
            @PathVariable("imageId") imageId: Long,
            @PathVariable("userToken") userToken: UUID,
            @RequestBody comment: Comment) {
        val image = imageRepository.findByIdOrNull(imageId) ?: throw Exception("No such image.")
        val user = userRepository.findByToken(userToken) ?: throw Exception("No such user.")

        comment.author = user.name

        image.comments.add(comment)
        commentRepository.save(comment)
    }
}