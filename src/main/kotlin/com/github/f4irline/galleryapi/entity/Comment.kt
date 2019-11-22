package com.github.f4irline.galleryapi.entity

import javax.persistence.*

@Entity
class Comment (
        val comment: String,
        var author: String,
        @Id @GeneratedValue val id: Long? = null
)