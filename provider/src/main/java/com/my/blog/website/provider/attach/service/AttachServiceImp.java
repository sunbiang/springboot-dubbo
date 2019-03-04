package com.my.blog.website.provider.attach.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.AttachService;
import com.my.blog.website.entity.AttachVo;
import com.my.blog.website.provider.attach.dao.AttachVoMapper;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @Author: sun
 * @Date: 2018/12/18 10:44
 */
@Service
public class AttachServiceImp implements AttachService {
    @Autowired
    private AttachVoMapper attachDao;


    @Override public PageInfo<AttachVo> getAttachs(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        EntityWrapper ew = new EntityWrapper();
        ew.orderBy("id", false);
        List<AttachVo> attachVos = attachDao.selectList(ew);
        return new PageInfo<>(attachVos);

    }

    @Override public void save(String fname, String fkey, String ftype, Integer author) {
        AttachVo attach = new AttachVo();
        attach.setFname(fname);
        attach.setAuthorId(author);
        attach.setFkey(fkey);
        attach.setFtype(ftype);
        attach.setCreated((int) (new Date().getTime() / 1000L));
        attachDao.insert(attach);
    }

    @Override public AttachVo selectById(Integer id) {
        return attachDao.selectById(id);
    }

    @Override public void deleteById(Integer id) {
        attachDao.deleteById(id);
    }

    @Override public int selectCount(EntityWrapper entityWrapper) {
        return attachDao.selectCount(entityWrapper);
    }
}
