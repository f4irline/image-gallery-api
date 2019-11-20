package com.github.f4irline.galleryapi.user

import com.github.f4irline.galleryapi.image.Image
import javax.persistence.*

@Entity
class User (
        var name: String,
        var password: String,
        var enabled: Boolean,
        @OneToMany(cascade = [CascadeType.ALL])
        val imageList: MutableSet<Image> = mutableSetOf(),
        @Id @GeneratedValue var id: Long? = null
)