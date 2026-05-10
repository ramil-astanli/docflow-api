package com.ramilastanli.docflow.core.dto.response;

public record LoginResponseDto(
        String message,
        UserDto user,
        String jwtToken
) {
}