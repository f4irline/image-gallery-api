package com.github.f4irline.galleryapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
class Comment (
        val comment: String,
        var author: String,
        val timeStamp: Long,
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "user_id")
        var user: User,
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "image_id")
        var image: Image,
        @Id @GeneratedValue val commentId: Long? = null
)