package com.github.f4irline.galleryapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Image (
        val path: String,
        val name: String,
        val description: String,
        val author: String,
        @JsonIgnore
        @ManyToOne
        val user: User,
        @OneToMany(cascade = [CascadeType.ALL])
        val comments: MutableSet<Comment> = mutableSetOf(),
        @Id @GeneratedValue val id: Long? = null
)