package com.github.f4irline.galleryapi.user

import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, Long> {
    fun findByName(name: String): User?
    fun findByToken(token: UUID): User?
}