package com.github.f4irline.galleryapi.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
class Image (
        val path: String,
        val name: String,
        val description: String,
        val author: String,
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "user_id")
        val user: User,
        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "image", orphanRemoval = true)
        val comments: MutableList<Comment> = mutableListOf(),
        @ElementCollection
        @JsonIgnore
        val upVotedUsers: MutableSet<UUID> = mutableSetOf(),
        @ElementCollection
        @JsonIgnore
        val downVotedUsers: MutableSet<UUID> = mutableSetOf(),
        @Id @GeneratedValue val imageId: Long? = null
)