package com.example.funkogram.app.user.domain.dto;

import lombok.Data;

@Data
public class SignInRequest {

    private String username;

    private String password;
}
