package com.github.f4irline.galleryapi.controller

import com.github.f4irline.galleryapi.dto.CommentDTO
import com.github.f4irline.galleryapi.entity.Comment
import com.github.f4irline.galleryapi.exception.NoSuchCommentException
import com.github.f4irline.galleryapi.exception.NoSuchImageException
import com.github.f4irline.galleryapi.exception.NoSuchUserException
import com.github.f4irline.galleryapi.repository.CommentRepository
import com.github.f4irline.galleryapi.repository.ImageRepository
import com.github.f4irline.galleryapi.repository.UserRepository
import com.github.f4irline.galleryapi.response.Success
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
            @RequestBody comment: Comment): ResponseEntity<CommentDTO> {
        val image = imageRepository.findByIdOrNull(imageId) ?: throw NoSuchImageException("No such image.")
        val user = userRepository.findByToken(userToken) ?: throw NoSuchUserException("No such user.")

        comment.author = user.name
        comment.user = user
        comment.image = image

        image.comments.add(comment)
        val newComment = commentRepository.save(comment)

        return ResponseEntity.ok().body(CommentDTO(newComment.author, newComment.comment, newComment.commentId, true))
    }

    @GetMapping("/{userToken}")
    @Throws
    fun getUserComments(@PathVariable("userToken") userToken: UUID): ResponseEntity<List<CommentDTO>> {
        val comments = commentRepository.findAll()
                .filter{ it.user.token == userToken }
                .map{ CommentDTO(it.author, it.comment, it.commentId, true)}
        return ResponseEntity.ok().body(comments)
    }

    @DeleteMapping("/{userToken}/{commentId}")
    fun deleteComment(
            @PathVariable("userToken") userToken: UUID, @PathVariable("commentId") commentId: Long
    ): ResponseEntity<*> {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw NoSuchCommentException("No such comment.")

        return if (comment.user.token != userToken) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Error("Unauthorized token."))
        } else {
            commentRepository.deleteById(commentId)
            ResponseEntity.ok().body(Success("Deleted comment successfully."))
        }
    }
}