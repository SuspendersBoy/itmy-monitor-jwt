package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@TableName("db_client_ssh")
@Data
public class ClientSshDTO implements BaseData   {
    @TableId(type = IdType.AUTO)
    Integer id;
    @NotNull
    String ip;
    @NotNull
    Integer port;
    @NotNull
    @Length(min = 1, max = 20)
    String username;
    @Length(min = 1, max = 20)
    @NotNull
    String password;
    @NotNull
    String clientId;
}
