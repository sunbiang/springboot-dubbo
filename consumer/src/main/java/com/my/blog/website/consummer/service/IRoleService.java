package com.my.blog.website.consummer.service;

import com.baomidou.mybatisplus.service.IService;
import com.my.blog.website.consummer.modal.Vo.RoleVo;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: sun
 * @Date: 2018/12/22 14:43
 */
public interface IRoleService extends IService<RoleVo> {
    List<RoleVo> findUserId( Integer id);
}
