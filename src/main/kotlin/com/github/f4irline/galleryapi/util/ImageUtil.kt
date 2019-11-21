package com.github.f4irline.galleryapi.util

import com.github.f4irline.galleryapi.dto.ImageDTO
import com.github.f4irline.galleryapi.exception.NoSuchFileException
import com.github.f4irline.galleryapi.entity.Image
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.nio.file.Path

@Component
class ImageUtil(
        private val path: Path
) {
    fun mapImageToDTO(image: Image): ImageDTO {
        val resource = ClassPathResource(image.path).filename ?: throw NoSuchFileException("No such file.")
        val fileName = this.path.resolve(resource)
        val urlResource = UrlResource(fileName.toUri())
        val imgBytes = StreamUtils.copyToByteArray(urlResource.inputStream)
        return ImageDTO(image.name, image.description, imgBytes)
    }
}