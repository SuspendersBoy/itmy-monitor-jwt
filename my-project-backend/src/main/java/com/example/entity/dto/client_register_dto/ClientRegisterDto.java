package com.example.entity.dto.client_register_dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("db_client_register")
@AllArgsConstructor
@NoArgsConstructor
public class ClientRegisterDto {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String token;
    private Date registerTime;
}
