package com.my.blog.website.consummer.service;

import com.baomidou.mybatisplus.service.IService;
import com.my.blog.website.consummer.modal.Vo.ResourcesVo;

import java.util.List;


public interface IResourcesService extends IService<ResourcesVo> {
    public List<ResourcesVo> findRoleId(Integer id);
}
