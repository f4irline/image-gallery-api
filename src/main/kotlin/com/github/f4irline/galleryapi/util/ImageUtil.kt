package com.github.f4irline.galleryapi.util

import com.github.f4irline.galleryapi.dto.CommentDTO
import com.github.f4irline.galleryapi.dto.ImageDTO
import com.github.f4irline.galleryapi.entity.Comment
import com.github.f4irline.galleryapi.exception.NoSuchFileException
import com.github.f4irline.galleryapi.entity.Image
import com.github.f4irline.galleryapi.repository.ImageRepository
import com.github.f4irline.galleryapi.service.AmazonClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.io.*
import java.nio.file.Path
import java.util.*
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.ImageOutputStream
import java.awt.image.BufferedImage

@Component
class ImageUtil(
        private val path: Path,
        private val imageRepository: ImageRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val COMPRESSION_RATIO = 0.8f
    }

    @Throws(NoSuchFileException::class)
    fun mapImageToDTO(image: Image, token: UUID?): ImageDTO? {
        val resource = ClassPathResource(image.path).filename

        if (resource == null) {
            image.imageId?.let { imageRepository.deleteById(it) }
            return null
        }

        val fileName = this.path.resolve(resource)
        val urlResource = UrlResource(fileName.toUri())

        val imgBytes: ByteArray

        try {
            imgBytes = StreamUtils.copyToByteArray(urlResource.inputStream)
        } catch (e: FileNotFoundException) {
            image.imageId?.let { imageRepository.deleteById(it) }
            return null
        }

        val userCanDelete = token?.equals(image.user.token)
        val comments = image.comments
                .map { mapCommentToDTO(it, token, image) }
                .sortedByDescending { it.id }
        val score = image.upVotedUsers.size - image.downVotedUsers.size
        val userUpVoted = image.upVotedUsers.any { it == token }
        val userDownVoted = image.downVotedUsers.any { it == token }
        return ImageDTO(
                image.name,
                image.description,
                userCanDelete, image.author,
                comments,
                score,
                userUpVoted,
                userDownVoted,
                image.imageId,
                imgBytes,
                image.width,
                image.height
        )
    }

    fun mapCommentToDTO(comment: Comment, token: UUID?, image: Image): CommentDTO {
        val userCanDelete = token?.equals(comment.user.token)
        return CommentDTO(comment.author, comment.comment, comment.commentId, userCanDelete, comment.timeStamp, image.author, image.name, image.imageId)
    }

    fun compressAndSave(path: Path, image: BufferedImage): BufferedImage {
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

        var resultImage: BufferedImage = image

        if (image.width > 1920 || image.height > 1920) {
            resultImage = resizeImage(image)
        }

        writer.write(null, IIOImage(resultImage, null, null), param)

        out.close()
        ios.close()
        writer.dispose()

        return resultImage
    }

    fun resizeImage(image: BufferedImage): BufferedImage {
        val aspectRatio: Double = image.width.toDouble() / image.height.toDouble()

        logger.info("Aspect ratio: $aspectRatio")

        if (aspectRatio > 1) {
            val newWidth = 1920.0
            val newHeight = newWidth / aspectRatio

            logger.info("New width: $newWidth")
            logger.info("New height: $newHeight")

            return Thumbnails.of(image).size(newWidth.toInt(), newHeight.toInt()).asBufferedImage()
        } else {
            val newHeight = 1920.0
            val newWidth = newHeight * aspectRatio

            logger.info("New width: $newWidth")
            logger.info("New height: $newHeight")

            return Thumbnails.of(image).size(newWidth.toInt(), newHeight.toInt()).asBufferedImage()
        }
    }
}