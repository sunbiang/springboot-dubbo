package com.my.blog.website.consummer.utils;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.my.blog.website.consummer.constant.WebConst;
import com.my.blog.website.consummer.modal.Vo.ResourcesVo;
import com.my.blog.website.consummer.modal.Vo.RoleVo;
import com.my.blog.website.consummer.modal.Vo.UserVo;
import com.my.blog.website.consummer.service.IResourcesService;
import com.my.blog.website.consummer.service.IRoleService;
import com.my.blog.website.consummer.service.IUserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import javax.annotation.Resource;

public class ShiroRealm extends AuthorizingRealm {
    @Autowired
    private IResourcesService resourcesService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IUserService userService;

    public ShiroRealm(){
        super(new AllowAllCredentialsMatcher());
        setAuthenticationTokenClass(UsernamePasswordToken.class);
        setCachingEnabled(false);
    }
    //授权
    @Override protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
       String username = SecurityUtils.getSubject().getPrincipal().toString();
        UserVo userVo = userService.selectOne(new EntityWrapper<UserVo>()
                .eq("username", username));
        for(RoleVo roleVo :roleService.findUserId(userVo.getUid())){
            simpleAuthorizationInfo.addRole(roleVo.getRoleDesc());
            for(ResourcesVo resourcesVo:resourcesService.findRoleId(roleVo.getId())){
                simpleAuthorizationInfo.addStringPermission(resourcesVo.getResUrl());
            }
        }
        return simpleAuthorizationInfo;
    }
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        SimpleAuthenticationInfo info=null;
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String loginName = ((UsernamePasswordToken) authenticationToken).getUsername();


        UserVo userVo = userService.selectOne(new EntityWrapper<UserVo>()
                .eq("username", usernamePasswordToken.getUsername()));
        if(userVo == null){
            return null;
        }
        info = new SimpleAuthenticationInfo(
                userVo.getUsername(),
          userVo.getPassword(),
          getName()
        );
        Session session =  SecurityUtils.getSubject().getSession();
        session.setAttribute(WebConst.LOGIN_SESSION_KEY, userVo);;
        return info;
    }
}
