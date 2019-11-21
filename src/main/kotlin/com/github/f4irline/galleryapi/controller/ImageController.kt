package com.github.f4irline.galleryapi.controller

import com.github.f4irline.galleryapi.entity.Image
import com.github.f4irline.galleryapi.dto.ImageDTO
import com.github.f4irline.galleryapi.repository.ImageRepository
import com.github.f4irline.galleryapi.repository.UserRepository
import com.github.f4irline.galleryapi.util.ImageUtil
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
                .map { imageUtil.mapImageToDTO(it) }
        return ResponseEntity.ok().body(imageList)
    }

    @GetMapping("/user/{token}")
    fun getUserImages(@PathVariable("token") token: UUID): ResponseEntity<List<ImageDTO>> {
        val user = userRepository.findByToken(token) ?: throw Exception()

        val imageList = imageRepository.findByUser(user)
                .map { imageUtil.mapImageToDTO(it) }

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
        println(file)

        val user = userRepository.findByToken(token) ?: throw Exception()

        val imageList: MutableSet<Image> = user.imageList
        imageList.add(Image(imagePath, properties.name, properties.description, user))

        imageUtil.compressAndSave(path.resolve(imagePath), file.inputStream)

        userRepository.save(user)
    }
}