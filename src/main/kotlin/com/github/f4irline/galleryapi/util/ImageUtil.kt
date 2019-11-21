package com.github.f4irline.galleryapi.util

import com.github.f4irline.galleryapi.dto.ImageDTO
import com.github.f4irline.galleryapi.exception.NoSuchFileException
import com.github.f4irline.galleryapi.model.Image
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.nio.file.Path

@Component
class ImageUtil(
        private val path: Path
) {
    fun mapImageToDTO(images: Iterable<Image>): List<ImageDTO> {
        return images
                .map { mapOf("resource" to ClassPathResource(it.path), "properties" to ImageDTO(it.name, it.description)) }
                .map { mapOf("resource" to this.path.resolve((it["resource"] as ClassPathResource).filename ?: throw NoSuchFileException("No such file.")), "properties" to it["properties"])}
                .map { mapOf("resource" to UrlResource((it["resource"] as Path).toUri()), "properties" to it["properties"]) }
                .map { ImageDTO((it["properties"] as ImageDTO).name, (it["properties"] as ImageDTO).description, StreamUtils.copyToByteArray((it["resource"] as UrlResource).inputStream)) }
    }
}