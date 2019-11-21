package com.github.f4irline.galleryapi.image

import com.github.f4irline.galleryapi.user.UserRepository
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import org.springframework.core.io.UrlResource
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import java.net.MalformedURLException
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.util.UriComponents
import java.io.File


@RestController
@RequestMapping("/api/image")
class ImageController(
        private val imageRepository: ImageRepository,
        private val userRepository: UserRepository,
        private val path: Path
) {
    @GetMapping("/")
    fun listFiles(): ResponseEntity<List<ByteArray>> {
        val imageList = imageRepository.findAll()
                .map { ClassPathResource(it.path) }
                .map { UrlResource(this.path.resolve(it.filename).toUri()) }
                .map { StreamUtils.copyToByteArray(it.inputStream) }
                .toList()

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
    fun uploadImage(@RequestBody file: MultipartFile?, @PathVariable("token") token: UUID) {
        if (file == null) {
            return
        }
        val uuid = UUID.randomUUID().toString()
        val imagePath = path.resolve("$uuid.jpg").toString()
        println(file)

        val user = userRepository.findByToken(token) ?: throw Exception()

        val imageList: MutableSet<Image> = user.imageList
        imageList.add(Image(imagePath))

        Files.copy(file.inputStream, path.resolve(imagePath))

        userRepository.save(user)
    }
}