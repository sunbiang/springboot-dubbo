package com.my.blog.website.consummer.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.my.blog.website.consummer.modal.Vo.RelationshipVoKey;
import com.my.blog.website.consummer.dao.RelationshipVoMapper;
import com.my.blog.website.consummer.service.IRelationshipService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by BlueT on 2017/3/18.
 */
@Service
public class RelationshipServiceImpl extends ServiceImpl<RelationshipVoMapper, RelationshipVoKey> implements IRelationshipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipServiceImpl.class);

    @Resource
    private RelationshipVoMapper relationshipVoMapper;

    @Override
    public void deleteById(Integer cid, Integer mid) {
        EntityWrapper<RelationshipVoKey> ew = new EntityWrapper();
        if(cid != null) {
            ew.and("cid={0}", cid);
        }
        if(mid != null) {
            ew.and("mid={0}", mid);
        }
        relationshipVoMapper.delete(ew);
    }

    @Override
    public List<RelationshipVoKey> getRelationshipById(Integer cid, Integer mid) {
        EntityWrapper<RelationshipVoKey> ew = new EntityWrapper();
        if(cid != null) {
            ew.and("cid={0}", cid);
        }
        if(mid != null) {
            ew.and("mid={0}", mid);
        }
        return relationshipVoMapper.selectList(ew);
    }

    @Override
    public void insertVo(RelationshipVoKey relationshipVoKey) {
        relationshipVoMapper.insert(relationshipVoKey);
    }

    @Override
    public Long countById(Integer cid, Integer mid) {
        LOGGER.debug("Enter countById method:cid={},mid={}", cid, mid);
        EntityWrapper<RelationshipVoKey> ew = new EntityWrapper();
        if(cid != null) {
            ew.and("cid={0}", cid);
        }
        if(mid != null) {
            ew.and("mid={0}", mid);
        }
        long num = relationshipVoMapper.selectCount(ew);
        LOGGER.debug("Exit countById method return num={}", num);
        return num;
    }
}
