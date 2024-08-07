package com.example.funkogram.app.user.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class JwtResponse {

    private String token;

    private String type = "Bearer";

    private Long id;

    private String username;

    private List<String> roles;
}
