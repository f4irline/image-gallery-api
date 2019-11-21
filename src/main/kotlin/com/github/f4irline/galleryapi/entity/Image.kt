package com.github.f4irline.galleryapi.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Image (
        var path: String,
        var name: String,
        var description: String,
        @Id @GeneratedValue var id: Long? = null
)