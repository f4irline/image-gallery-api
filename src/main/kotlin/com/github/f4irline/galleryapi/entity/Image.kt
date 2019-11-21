package com.github.f4irline.galleryapi.entity

import javax.persistence.*

@Entity
class Image (
        var path: String,
        var name: String,
        var description: String,
        @ManyToOne(cascade = [CascadeType.ALL])
        val user: User,
        @Id @GeneratedValue var id: Long? = null
)