package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.client_register_dto.ClientRegisterDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientRegisterMapper extends BaseMapper<ClientRegisterDto> {
}
