package com.github.f4irline.galleryapi.image

import com.github.f4irline.galleryapi.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.Principal
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/image")
class ImageController(
        private val imageRepository: ImageRepository,
        private val userRepository: UserRepository,
        private val path: Path
) {
    @PostMapping("/")
    @Throws
    fun uploadImage(@RequestBody file: MultipartFile, principal: Principal) {
        val uuid = UUID.randomUUID().toString()
        val imagePath = path.resolve("$uuid.jpg").toString()

        val user = userRepository.findByName(principal.name) ?: throw Exception()

        val imageList: MutableSet<Image> = user.imageList
        imageList.add(Image(imagePath))

        Files.copy(file.inputStream, path.resolve(imagePath))

        userRepository.save(user)
    }
}