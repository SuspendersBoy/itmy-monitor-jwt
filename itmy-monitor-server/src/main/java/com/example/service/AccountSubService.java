package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.ChildDto;
import com.example.entity.vo.request.ChildVO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AccountSubService extends IService<ChildDto> {

    Boolean addSubAccount(ChildVO childVO, String id);

    List<ChildDto> selectSubAccount();
}
