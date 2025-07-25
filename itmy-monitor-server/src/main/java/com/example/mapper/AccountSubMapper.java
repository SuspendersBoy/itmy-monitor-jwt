package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.ChildDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountSubMapper extends BaseMapper<ChildDto> {
}
