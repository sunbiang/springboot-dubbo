package com.my.blog.website.consummer.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.my.blog.website.consummer.dao.ResourcesVoMapper;
import com.my.blog.website.consummer.modal.Vo.ResourcesVo;
import com.my.blog.website.consummer.service.IResourcesService;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service("resourcesService")
public class ResourcesServiceImpl extends ServiceImpl<ResourcesVoMapper, ResourcesVo> implements IResourcesService {

    @Resource
    private ResourcesVoMapper resourcesVoMapper;

    @Override public List<ResourcesVo> findRoleId(Integer id) {
        return resourcesVoMapper.findRoleId(id);
    }
}
