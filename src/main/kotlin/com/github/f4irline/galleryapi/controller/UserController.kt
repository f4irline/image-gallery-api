package com.github.f4irline.galleryapi.controller

import com.github.f4irline.galleryapi.response.Success
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
        return if (authDetails.name.matches("^[a-zA-Z0-9]{3,}\$".toRegex())) {
            user = User(authDetails.name)
            repository.save(user)
            ResponseEntity.status(HttpStatus.OK).body(Success("Saved user successfully.", user.token))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Success("Error registering user."))
        }
    }
}