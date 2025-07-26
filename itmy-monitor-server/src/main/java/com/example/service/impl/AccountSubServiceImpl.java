package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountDto;
import com.example.entity.dto.ChildDto;
import com.example.entity.vo.request.ChildVO;
import com.example.mapper.AccountMapper;
import com.example.mapper.AccountSubMapper;
import com.example.service.AccountSubService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;


@Service
public class AccountSubServiceImpl extends ServiceImpl<AccountSubMapper, ChildDto> implements AccountSubService {
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    /**
     * @childvo 子账户注册信息
     * @id 注册子账户的id
     * @return 是否成功
     */
    public Boolean addSubAccount(ChildVO childVO, String id) {
        ChildDto childDto=new ChildDto();
        BeanUtils.copyProperties(childVO,childDto);
        String password=passwordEncoder.encode(childVO.getPassword());
        childDto.setPassword(password);
        childDto.setParent_id(id);
        childDto.setRole("user");
        childDto.setRegisterTime(new Date());
        this.save(childDto);
        AccountDto accountDto=new AccountDto();
        accountDto.setEmail("");
        BeanUtils.copyProperties(childDto,accountDto);
        accountMapper.insert(accountDto);
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public List<ChildDto> selectSubAccount() {
        return this.list();

    }

}
