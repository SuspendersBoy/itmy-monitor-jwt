package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.ClientSshDTO;
import com.example.entity.vo.request.SshConnectionVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SshMapper extends BaseMapper<ClientSshDTO> {
}
