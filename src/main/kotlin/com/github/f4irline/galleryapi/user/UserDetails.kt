package com.github.f4irline.galleryapi.user

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
public class UserDetails(
        val userRepository: UserRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(name: String): UserDetails {
        val user = userRepository.findByName(name) ?: throw UsernameNotFoundException("No such user")
        return with(user) {
            User.withUsername(name)
                    .password(password)
                    .authorities("USER")
                    .build()
        }
    }
}