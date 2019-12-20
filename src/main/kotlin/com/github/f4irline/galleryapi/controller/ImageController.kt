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
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import javax.imageio.ImageIO

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
        val imageList = imageRepository.findAllByOrderByImageIdDesc()
                .map { imageUtil.mapImageToDTO(it, null) }
        return ResponseEntity.ok().body(imageList)
    }

    @GetMapping("/{token}")
    fun listFilesWithToken(@PathVariable("token") token: UUID): ResponseEntity<List<ImageDTO>> {
        val imageList = imageRepository.findAllByOrderByImageIdDesc()
                .map { imageUtil.mapImageToDTO(it, token) }
        return ResponseEntity.ok().body(imageList)
    }

    @GetMapping("/single/{id}")
    fun getSingleImage(@PathVariable("id") id: Long): ResponseEntity<ImageDTO> {
        val image = imageRepository.findByIdOrNull(id) ?: throw NoSuchImageException("No such image.")
        val imageDTO = imageUtil.mapImageToDTO(image, null)

        return ResponseEntity.ok().body(imageDTO)
    }

    @GetMapping("/single/{id}/{token}")
    fun getSingleImageWithToken(@PathVariable("token") token: UUID, @PathVariable("id") id: Long): ResponseEntity<ImageDTO> {
        val image = imageRepository.findByIdOrNull(id) ?: throw NoSuchImageException("No such image.")
        val imageDTO = imageUtil.mapImageToDTO(image, token)

        return ResponseEntity.ok().body(imageDTO)
    }

    @GetMapping("/user/{token}")
    fun getUserImages(@PathVariable("token") token: UUID): ResponseEntity<List<ImageDTO>> {
        val user = userRepository.findByToken(token) ?: throw NoSuchUserException("No such user.")

        val imageList = imageRepository.findByUserOrderByImageIdDesc(user)
                .map { imageUtil.mapImageToDTO(it, token) }

        return ResponseEntity.ok().body(imageList)
    }

    @PostMapping("/{token}")
    @Throws
    fun uploadImage(
            @RequestPart("file") file: MultipartFile?,
            @RequestPart("name") name: String,
            @RequestPart("description") description: String,
            @PathVariable("token") token: UUID): ResponseEntity<*> {
        if (file == null) { throw FileNotFoundException() }
        val uuid = UUID.randomUUID().toString()
        val imagePath = path.resolve("$uuid.jpg").toString()
        val user = userRepository.findByToken(token) ?: throw NoSuchUserException("No such user.")

        val image: BufferedImage = ImageIO.read(file.inputStream)

        val imageList: MutableSet<Image> = user.imageList
        val newImage = Image(imagePath, name, description, user.name, image.width, image.height, user)
        imageList.add(newImage)

        imageUtil.compressAndSave(path.resolve(imagePath), image)
        userRepository.save(user)

        return ResponseEntity.ok().body(Success("Uploaded image successfully"))
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
    ): ResponseEntity<ImageDTO> {
        val image: Image = imageRepository.findByIdOrNull(imageId) ?: throw NoSuchImageException("No such image.")
        return when {
            upVote -> {
                image.downVotedUsers.remove(userToken)
                image.upVotedUsers.add(userToken)
                imageRepository.save(image)
                val imageDTO: ImageDTO = imageUtil.mapImageToDTO(image, userToken)
                ResponseEntity.ok().body(imageDTO)
            }
            else -> {
                image.upVotedUsers.remove(userToken)
                image.downVotedUsers.add(userToken)
                imageRepository.save(image)
                val imageDTO: ImageDTO = imageUtil.mapImageToDTO(image, userToken)
                ResponseEntity.ok().body(imageDTO)
            }
        }
    }

    @PutMapping("/vote/{userToken}/{imageId}/reset")
    fun resetVote(
            @PathVariable("userToken") userToken: UUID,
            @PathVariable("imageId") imageId: Long
    ): ResponseEntity<ImageDTO> {
        val image: Image = imageRepository.findByIdOrNull(imageId) ?: throw NoSuchImageException("No such image.")
        image.upVotedUsers.remove(userToken)
        image.downVotedUsers.remove(userToken)
        imageRepository.save(image)
        val imageDTO: ImageDTO = imageUtil.mapImageToDTO(image, userToken)
        return ResponseEntity.ok().body(imageDTO)
    }
}