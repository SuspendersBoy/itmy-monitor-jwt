package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.SshConnectionVO;
import com.example.entity.vo.request.SshSettingsVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.service.ClientDetailService;
import com.example.utils.Const;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@RestController
@RequestMapping("api/monitor")
public class MonitorController {
    @Autowired
    ClientDetailService clientDetailService;

    @GetMapping("list")
    public RestBean<List<ClientPreviewVO>> listAllClient(@RequestAttribute(Const.ATTR_USER_ID)String id){
        // 获取当前认证的用户对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities =authentication.getAuthorities();
        List<String > s=authorities.stream().map((Function<GrantedAuthority, String>) grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_admin") ? "admin" :null).toList();
        if (!s.isEmpty()&& s.stream().anyMatch(Objects::nonNull)){
            return RestBean.success(clientDetailService.listAllClient());
        }
        return RestBean.success(clientDetailService.listAllUserClient(id));
    }
    @PostMapping("ssh-save")
    public RestBean<Void> sshSave(@RequestBody @Valid SshConnectionVO sshConnectionVO){
        return RestBean.success();
    }
    @GetMapping("/ssh")
    public RestBean<SshSettingsVO> sshSettings() {
      return  RestBean.success();
    }
}
