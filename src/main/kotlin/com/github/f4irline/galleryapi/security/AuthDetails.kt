package com.github.f4irline.galleryapi.security

import com.fasterxml.jackson.annotation.JsonProperty

public class AuthDetails (
        @JsonProperty("name")
        val name: String
)