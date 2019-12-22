package com.github.f4irline.galleryapi.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "GALLERY_USER")
class User (
        val name: String,
        val token: UUID = UUID.randomUUID(),
        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
        val imageList: MutableSet<Image> = mutableSetOf(),
        @Id @GeneratedValue val userId: Long? = null
)