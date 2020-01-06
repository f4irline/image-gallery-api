package com.github.f4irline.galleryapi.util

import com.github.f4irline.galleryapi.dto.CommentDTO
import com.github.f4irline.galleryapi.dto.ImageDTO
import com.github.f4irline.galleryapi.entity.Comment
import com.github.f4irline.galleryapi.exception.NoSuchFileException
import com.github.f4irline.galleryapi.entity.Image
import com.github.f4irline.galleryapi.repository.ImageRepository
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.awt.image.BufferedImage
import java.io.*
import java.nio.file.Path
import java.util.*
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.ImageOutputStream

@Component
class ImageUtil(
        private val path: Path,
        private val imageRepository: ImageRepository
) {
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
                .map { mapCommentToDTO(it, token) }
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

    fun mapCommentToDTO(comment: Comment, token: UUID?): CommentDTO {
        val userCanDelete = token?.equals(comment.user.token)
        return CommentDTO(comment.author, comment.comment, comment.commentId, userCanDelete)
    }

    fun compressAndSave(path: Path, image: BufferedImage) {
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