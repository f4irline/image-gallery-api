package com.github.f4irline.galleryapi.controller

import com.github.f4irline.galleryapi.entity.Image
import com.github.f4irline.galleryapi.dto.ImageDTO
import com.github.f4irline.galleryapi.exception.NoSuchImageException
import com.github.f4irline.galleryapi.exception.NoSuchUserException
import com.github.f4irline.galleryapi.repository.ImageRepository
import com.github.f4irline.galleryapi.repository.UserRepository
import com.github.f4irline.galleryapi.response.Success
import com.github.f4irline.galleryapi.util.ImageUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.*
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping

@RestController
@RequestMapping("/api/image")
class ImageController(
        private val imageRepository: ImageRepository,
        private val userRepository: UserRepository,
        private val path: Path,
        private val imageUtil: ImageUtil
) {
    @GetMapping("/")
    fun listFiles(): ResponseEntity<List<ImageDTO>> {
        val imageList = imageRepository.findAll()
                .map { imageUtil.mapImageToDTO(it, null) }
        return ResponseEntity.ok().body(imageList)
    }

    @GetMapping("/{token}")
    fun listFilesWithToken(@PathVariable("token") token: UUID): ResponseEntity<List<ImageDTO>> {
        val imageList = imageRepository.findAll()
                .map { imageUtil.mapImageToDTO(it, token) }
        return ResponseEntity.ok().body(imageList)
    }


    @GetMapping("/user/{token}")
    fun getUserImages(@PathVariable("token") token: UUID): ResponseEntity<List<ImageDTO>> {
        val user = userRepository.findByToken(token) ?: throw NoSuchUserException("No such user.")

        val imageList = imageRepository.findByAuthor(user.name)
                .map { imageUtil.mapImageToDTO(it, token) }

        return ResponseEntity.ok().body(imageList)
    }

    @PostMapping("/{token}")
    @Throws
    fun uploadImage(
            @RequestPart("file") file: MultipartFile?,
            @RequestPart("properties") properties: ImageDTO,
            @PathVariable("token") token: UUID) {
        if (file == null) {
            return
        }
        val uuid = UUID.randomUUID().toString()
        val imagePath = path.resolve("$uuid.jpg").toString()
        val user = userRepository.findByToken(token) ?: throw NoSuchUserException("No such user.")

        val imageList: MutableSet<Image> = user.imageList
        imageList.add(Image(imagePath, properties.name, properties.description, user.name, user))

        imageUtil.compressAndSave(path.resolve(imagePath), file.inputStream)

        userRepository.save(user)
    }

    @DeleteMapping("/{userToken}/{imageId}")
    fun deleteImage(
            @PathVariable("userToken") userToken: UUID,
            @PathVariable("imageId") imageId: Long): ResponseEntity<*> {
        val image: Image = imageRepository.findByIdOrNull(imageId) ?: throw NoSuchImageException("No such image.")

        return if (image.user.token != userToken) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Error("Unauthorized token."))
        } else {
            imageRepository.deleteById(imageId)
            ResponseEntity.ok().body(Success("Deleted image successfully"))
        }
    }

    @PutMapping("/vote/{userToken}/{imageId}/{upVote}")
    fun voteImage(
            @PathVariable("userToken") userToken: UUID,
            @PathVariable("imageId") imageId: Long,
            @PathVariable("upVote") upVote: Boolean
    ): ResponseEntity<*> {
        val image: Image = imageRepository.findByIdOrNull(imageId) ?: throw NoSuchImageException("No such image.")
        return if (upVote) {
            image.downVotedUsers.remove(userToken)
            image.upVotedUsers.add(userToken)
            imageRepository.save(image)
            ResponseEntity.ok().body(Success("Upvoted successfully."))
        } else {
            image.upVotedUsers.remove(userToken)
            image.downVotedUsers.add(userToken)
            imageRepository.save(image)
            ResponseEntity.ok().body(Success("Downvoted successfully."))
        }
    }
}