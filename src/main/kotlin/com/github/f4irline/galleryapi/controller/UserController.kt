package com.github.f4irline.galleryapi.controller

import com.github.f4irline.galleryapi.response.Success
import com.github.f4irline.galleryapi.response.Error
import com.github.f4irline.galleryapi.security.AuthDetails
import com.github.f4irline.galleryapi.entity.User
import com.github.f4irline.galleryapi.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val repository: UserRepository) {
    @PostMapping("/register")
    fun register(@RequestBody authDetails: AuthDetails): ResponseEntity<*> {
        val user: User
        return when {
            authDetails.name.length < 3 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error("Name should be at least 3 letters long"))
            repository.findByName(authDetails.name) !== null -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error("User with this name already exists"))
            else -> {
                user = User(authDetails.name)
                repository.save(user)
                ResponseEntity.status(HttpStatus.OK).body(Success("Saved user successfully", user.token))
            }
        }
    }
}