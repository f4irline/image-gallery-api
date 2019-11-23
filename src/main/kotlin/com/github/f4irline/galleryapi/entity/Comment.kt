package com.github.f4irline.galleryapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Comment (
        val comment: String,
        var author: String,
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "user_id")
        var user: User,
        @Id @GeneratedValue val commentId: Long? = null
)