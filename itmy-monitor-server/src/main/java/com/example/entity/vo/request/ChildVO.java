package com.example.entity.vo.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data

public class ChildVO {
    @Length(min = 6, max = 10)
    private String username;
    @Length(min = 6, max = 20)
    private String password;
    @Size(min = 1)
    private String client;
}
