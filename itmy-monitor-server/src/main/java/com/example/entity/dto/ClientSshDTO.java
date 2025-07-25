package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;
@TableName("db_client_ssh")
@Data
public class ClientSshDTO implements BaseData   {
    @TableId
    Integer id;
    Integer prot;
    String username;
    String  password;
}
