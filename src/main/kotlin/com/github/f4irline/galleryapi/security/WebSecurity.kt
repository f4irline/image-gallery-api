package com.github.f4irline.galleryapi.security

import com.github.f4irline.galleryapi.response.AuthSuccessHandler
import com.github.f4irline.galleryapi.user.UserDetails
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
public class WebSecurity(
        val userDetails: UserDetails,
        val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter(true) {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/find").permitAll()
            .and().formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/api/user-login")
                .usernameParameter("user_name")
                .passwordParameter("user_password")
                .successHandler(AuthSuccessHandler())
                .failureUrl("/login?error").permitAll()
            .and().logout().permitAll()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder)
    }
}