package com.github.f4irline.galleryapi.util

import com.github.f4irline.galleryapi.dto.ImageDTO
import com.github.f4irline.galleryapi.exception.NoSuchFileException
import com.github.f4irline.galleryapi.entity.Image
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.ImageOutputStream

@Component
class ImageUtil(
        private val path: Path
) {
    companion object {
        const val COMPRESSION_RATIO = 0.05f
    }

    fun mapImageToDTO(image: Image): ImageDTO {
        val resource = ClassPathResource(image.path).filename ?: throw NoSuchFileException("No such file.")
        val fileName = this.path.resolve(resource)
        val urlResource = UrlResource(fileName.toUri())
        val imgBytes = StreamUtils.copyToByteArray(urlResource.inputStream)
        return ImageDTO(image.name, image.description, imgBytes)
    }

    fun compressAndSave(path: Path, input: InputStream) {
        val image: BufferedImage = ImageIO.read(input)

        val output = File(path.toUri())
        val out: OutputStream = FileOutputStream(output)

        val writer: ImageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
        val ios: ImageOutputStream = ImageIO.createImageOutputStream(out)
        writer.output = ios

        val param: ImageWriteParam = writer.defaultWriteParam
        if (param.canWriteCompressed()) {
            param.compressionMode = ImageWriteParam.MODE_EXPLICIT
            param.compressionQuality = COMPRESSION_RATIO
        }

        writer.write(null, IIOImage(image, null, null), param)

        out.close()
        ios.close()
        writer.dispose()
    }
}