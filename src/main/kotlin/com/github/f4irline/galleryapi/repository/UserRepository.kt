package com.github.f4irline.galleryapi.repository

import com.github.f4irline.galleryapi.entity.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, Long> {
    fun findByToken(token: UUID): User?
}