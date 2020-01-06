package com.github.f4irline.galleryapi.service

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.annotation.PostConstruct

@Service
class AmazonClient (
        var s3Client: AmazonS3
) {
    @Value("\${amazonProperties.endpointUrl}")
    private lateinit var endpointUrl: String

    @Value("\${amazonProperties.bucketName}")
    private lateinit var bucketName: String

    @Value("\${AWS_ACCESS_KEY_ID}")
    private lateinit var accessKey: String

    @Value("\${AWS_SECRET_ACCESS_KEY}")
    private lateinit var secretKey: String

    @PostConstruct
    fun initAws() {
        val credentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)
        this.s3Client = AmazonS3ClientBuilder.standard().withCredentials(AWSStaticCredentialsProvider(credentials)).build()
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