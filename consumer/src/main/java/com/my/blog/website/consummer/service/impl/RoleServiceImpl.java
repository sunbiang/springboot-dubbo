package com.my.blog.website.consummer.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.my.blog.website.consummer.dao.RoleVoMapper;
import com.my.blog.website.consummer.modal.Vo.RoleVo;
import com.my.blog.website.consummer.service.IRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * @Author: sun
 * @Date: 2018/12/22 15:01
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleVoMapper, RoleVo> implements IRoleService {
    @Resource
    private RoleVoMapper roleVoMapper;
    @Override public List<RoleVo> findUserId(Integer id) {
        return roleVoMapper.findUserId(id);
    }
}
