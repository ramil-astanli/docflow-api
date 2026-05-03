package com.ramilastanli.docflow.security.dto.response;

public record LoginResponseDto(
        String message,
        UserDto user,
        String jwtToken
) {
}