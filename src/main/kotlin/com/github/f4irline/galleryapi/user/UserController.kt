package com.github.f4irline.galleryapi.user

import com.github.f4irline.galleryapi.response.Error
import com.github.f4irline.galleryapi.response.Success
import com.github.f4irline.galleryapi.security.AuthDetails
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
            ResponseEntity(Success("Registered user successfully.", user.token), HttpStatus.OK)
        } else {
            ResponseEntity(Error("Error registering user."), HttpStatus.BAD_REQUEST)
        }
    }
}