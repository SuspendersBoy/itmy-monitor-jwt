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
import java.util.function.Predicate;

@RestController
@RequestMapping("api/monitor")
public class MonitorController {
    @Autowired
    ClientDetailService clientDetailService;

    /**
     * 列出用户所有权限的服务器
     * @param id
     * @return
     */
    @GetMapping("list")
    public RestBean<List<ClientPreviewVO>> listAllClient(@RequestAttribute(Const.ATTR_USER_ID)String id){
        // 获取当前认证的用户对象的权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities =authentication.getAuthorities();
        List<? extends GrantedAuthority> s=authorities.stream().filter((Predicate<GrantedAuthority>) grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_admin") ? true :false).toList();
        if (!s.isEmpty()){
            return RestBean.success(clientDetailService.listAllClient());
        }
        return RestBean.success(clientDetailService.listAllUserClient(id));
    }
    //服务器连接信息
    @PostMapping("ssh-save")
    public RestBean<Void> sshSave(@RequestBody @Valid SshConnectionVO sshConnectionVO){
        clientDetailService.sshSave(sshConnectionVO);
        return RestBean.success();
    }
    @GetMapping("/ssh")
    public RestBean<SshSettingsVO> sshSettings(@RequestParam String clientId) {
      return  clientDetailService.sshSettings(clientId) ? RestBean.success():RestBean.forbidden("服务器未注册");
    }
}
