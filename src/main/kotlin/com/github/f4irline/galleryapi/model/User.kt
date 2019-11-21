package com.github.f4irline.galleryapi.model

import com.github.f4irline.galleryapi.model.Image
import java.util.*
import javax.persistence.*

@Entity
class User (
        var name: String,
        val token: UUID = UUID.randomUUID(),
        @OneToMany(cascade = [CascadeType.ALL])
        val imageList: MutableSet<Image> = mutableSetOf(),
        @Id @GeneratedValue var id: Long? = null
)