package com.github.f4irline.galleryapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Comment (
        val comment: String,
        var author: String,
        @JsonIgnore
        @ManyToOne
        var user: User,
        @Id @GeneratedValue val id: Long? = null
)