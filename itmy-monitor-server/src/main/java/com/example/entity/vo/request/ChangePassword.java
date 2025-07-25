package com.example.entity.vo.request;

import lombok.Data;

@Data
public class ChangePassword {
    private String new_password;
    private String password;
}
