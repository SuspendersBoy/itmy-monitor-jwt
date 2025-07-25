package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SshConnectionVO {
    @NotNull
    String id;
    @NotNull
    Integer prot;
    @NotNull
    @Length(min = 1, max = 20)
    String username;
    @Length(min = 1, max = 20)
    @NotNull
    String password;
}
