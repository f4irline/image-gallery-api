package com.github.f4irline.galleryapi.image

import com.github.f4irline.galleryapi.user.UserRepository
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import org.springframework.core.io.UrlResource
import org.springframework.util.StreamUtils
import java.net.MalformedURLException
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.GetMapping

@RestController
@RequestMapping("/api/image")
class ImageController(
        private val imageRepository: ImageRepository,
        private val userRepository: UserRepository,
        private val path: Path
) {
    @GetMapping("/")
    fun listFiles(): ResponseEntity<List<ImageDTO>> {
/*
        val imageList = imageRepository.findAll()
                .map { ImageDTO(it.name, it.description, StreamUtils.copyToByteArray(UrlResource(this.path.resolve(ClassPathResource(it.path).filename).toUri()).inputStream)) }
                .toList()
*/
        val imageList = imageRepository.findAll()
                .map { mapOf("resource" to ClassPathResource(it.path), "properties" to ImageDTO(it.name, it.description)) }
                .map { mapOf("resource" to UrlResource(this.path.resolve((it["resource"] as ClassPathResource).filename).toUri()), "properties" to it["properties"]) }
                .map { ImageDTO((it["properties"] as ImageDTO).name, (it["properties"] as ImageDTO).description, StreamUtils.copyToByteArray((it["resource"] as UrlResource).inputStream)) }
/*
        val imageList = imageRepository.findAll()
                .map { ClassPathResource(it.path) }
                .map { UrlResource(this.path.resolve(it.filename).toUri()) }
                .map { ImageDTO("name", "Test", StreamUtils.copyToByteArray(it.inputStream)) }
                .toList()
*/

        return ResponseEntity.ok().body(imageList)
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    @Throws(MalformedURLException::class)
    fun serveFile(@PathVariable filename: String): ResponseEntity<Resource> {

        val file = this.path.resolve(filename)
        val resource = UrlResource(file.toUri())

        return ResponseEntity
                .ok()
                .body<Resource>(resource)
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
        imageList.add(Image(imagePath, properties.name, properties.description))

        Files.copy(file.inputStream, path.resolve(imagePath))

        userRepository.save(user)
    }
}