package com.example.entity;

public record Response(int code, Object message, String token,Object data) {
}
