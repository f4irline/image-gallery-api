package com.github.f4irline.galleryapi.service

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.annotation.PostConstruct

@Service
class AmazonClient () {
    @Value("\${amazonProperties.endpointUrl}")
    private lateinit var endpointUrl: String

    @Value("\${amazonProperties.bucketName}")
    private lateinit var bucketName: String

    private lateinit var accessKey: String

    private lateinit var secretKey: String

    private lateinit var s3Client: AmazonS3

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun amazonS3Client(credentialsProvider: AWSCredentialsProvider): AmazonS3 {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.EU_NORTH_1)
                .withCredentials(credentialsProvider)
                .build()
    }

    @PostConstruct
    fun initAws() {
        this.accessKey = System.getenv("AWS_ACCESS_KEY_ID")
        this.secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        logger.debug("Access Key: $accessKey")
        logger.debug("Secret: $secretKey")
        val credentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)
        this.s3Client = amazonS3Client(AWSStaticCredentialsProvider(credentials))
    }

    @Throws(IOException::class)
    private fun convertMultiPartToFile(file: MultipartFile, fileName: String): File {
        val convertedFile = File(file.originalFilename?: fileName)
        val outputStream = FileOutputStream(convertedFile)
        outputStream.write(file.bytes)
        outputStream.close()
        return convertedFile
    }

    private fun uploadFileToS3(fileName: String, file: File) {
        s3Client.putObject(PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead))
    }

    fun uploadFile(multipartFile: MultipartFile, fileName: String): String {
        logger.debug("Endpoint: $endpointUrl")
        logger.debug("Bucket: $bucketName")
        var fileUrl = ""
        try {
            val file: File = convertMultiPartToFile(multipartFile, fileName)
            fileUrl = "$endpointUrl/$bucketName/$fileName"
            uploadFileToS3(fileName, file)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileUrl
    }
}