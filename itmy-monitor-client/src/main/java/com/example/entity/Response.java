package com.example.entity;

public record Response(int code, String message, String token,Object data) {
}
