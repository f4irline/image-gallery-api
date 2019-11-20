package com.github.f4irline.galleryapi.image

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Image (
        var path: String,
        @Id @GeneratedValue var id: Long? = null
)