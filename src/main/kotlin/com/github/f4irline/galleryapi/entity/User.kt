package com.github.f4irline.galleryapi.entity

import java.util.*
import javax.persistence.*

@Entity
class User (
        val name: String,
        val token: UUID = UUID.randomUUID(),
        @OneToMany(cascade = [CascadeType.ALL])
        val imageList: MutableSet<Image> = mutableSetOf(),
        @Id @GeneratedValue val id: Long? = null
)