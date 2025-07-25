package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ChangePassword;
import com.example.entity.dto.ChildDto;
import com.example.entity.vo.request.ChildVO;
import com.example.service.AccountService;
import com.example.service.AccountSubService;
import com.example.utils.Const;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    AccountService accountService;
    @Autowired
    AccountSubService accountSubService;
    @PostMapping("change-password")
    public RestBean<Void> post(@RequestBody ChangePassword changePassword, @RequestAttribute(Const.ATTR_USER_ID)int id) {
        Boolean b=accountService.changePassword(changePassword,id);
        return b ? RestBean.success() : RestBean.failure(401,"原密码错误");
    }
    @PostMapping("add-sub-account")
    public RestBean<Void>  addSubAccount(@RequestBody ChildVO childVO , @RequestAttribute(Const.ATTR_USER_ID)String id) {
        // 获取当前认证的用户对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities =authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
           if (authority.getAuthority().equals("ROLE_user")){
               return RestBean.forbidden("权限不足");
           }
        }
        accountSubService.addSubAccount(childVO,id);
        return RestBean.success();
    }
    @GetMapping("select-sub-account")
    public RestBean<List<ChildDto>> selectSubAccount( ) {
        // 获取当前认证的用户对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities =authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_user")){
                return RestBean.forbidden("权限不足");
            }
        }
         List<ChildDto> list= accountSubService.selectSubAccount();
        return RestBean.success(list);
    }
}
