package com.my.blog.website.consummer.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.my.blog.website.consummer.constant.WebConst;
import com.my.blog.website.consummer.dao.MetaVoMapper;
import com.my.blog.website.consummer.dto.MetaDto;
import com.my.blog.website.consummer.dto.Types;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.modal.Vo.ContentVo;
import com.my.blog.website.consummer.modal.Vo.MetaVo;
import com.my.blog.website.consummer.modal.Vo.RelationshipVoKey;
import com.my.blog.website.consummer.service.IContentService;
import com.my.blog.website.consummer.service.IMetaService;
import com.my.blog.website.consummer.service.IRelationshipService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Created by BlueT on 2017/3/17.
 */
@Service
public class MetaServiceImpl extends ServiceImpl<MetaVoMapper, MetaVo> implements IMetaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaServiceImpl.class);

    @Resource
    private MetaVoMapper metaDao;

    @Resource
    private IRelationshipService relationshipService;

    @Resource
    private IContentService contentService;

    /**
     * 根据姓名和类型查找标签/分类
     *
     * @param type
     * @param name
     * @return
     */
    @Override
    public MetaDto getMeta(String type, String name) {
        if(StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            return metaDao.selectDtoByNameAndType(name, type);
        }
        return null;
    }

    @Override
    public Integer countMeta(Integer mid) {
        return metaDao.countWithSql(mid);
    }

    @Override
    public List<MetaVo> getMetas(String types) {
        if(StringUtils.isNotBlank(types)) {
            EntityWrapper ew = new EntityWrapper();
            ew.andNew("type={0}", types);
            List<String> list = new ArrayList<>();
            list.add("sort");
            list.add("mid");
            ew.orderDesc(list);
            return metaDao.selectList(ew);
        }
        return null;
    }

    @Override
    public List<MetaDto> getMetaList(String type, String orderby, int limit) {
        if(StringUtils.isNotBlank(type)) {
            if(StringUtils.isBlank(orderby)) {
                orderby = "count desc, a.mid desc";
            }
            if(limit < 1 || limit > WebConst.MAX_POSTS) {
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderby);
            paraMap.put("limit", limit);
            return metaDao.selectFromSql(paraMap);
        }
        return null;
    }

    /**
     * 删除分类或者标签的操作
     *
     * @param mid
     */
    @Override
    public void delete(int mid) {
        MetaVo metas = metaDao.selectById(mid);
        if(null != metas) {
            String type = metas.getType();
            String name = metas.getName();
            metaDao.deleteById(mid);
            List<RelationshipVoKey> rlist = relationshipService.getRelationshipById(null, mid);
            if(null != rlist) {
                for(RelationshipVoKey r : rlist) {
                    ContentVo contents = contentService.getContents(String.valueOf(r.getCid()));
                    if(null != contents) {
                        if(type.equals(Types.CATEGORY.getType())) {
                            contents.setCategories(reMeta(name, contents.getCategories()));
                        }
                        if(type.equals(Types.TAG.getType())) {
                            contents.setTags(reMeta(name, contents.getTags()));
                        }
                        contentService.updateById(contents);
                    }
                }
            }
            relationshipService.deleteById(null, mid);
        }
    }

    /**
     * 保存标签和分类
     *
     * @param type
     * @param name
     * @param mid
     */
    @Override
    public void saveMeta(String type, String name, Integer mid) {
        if(StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            EntityWrapper ew = new EntityWrapper();
            ew.where("1=1");
            ew.and("type={0}", type);
            ew.and("name={0}", name);
            List<MetaVo> metaVos = metaDao.selectList(ew);
            MetaVo metas;
            if(metaVos.size() != 0) {
                throw new TipException("已经存在该项");
            } else {
                metas = new MetaVo();
                metas.setName(name);
                if(null != mid) {
                    MetaVo original = metaDao.selectById(mid);
                    metas.setMid(mid);
                    metaDao.updateById(metas);
//                    更新原有文章的categories
                    ContentVo contentVo = new ContentVo();
                    contentVo.setCategories(name);
                    contentService.update(contentVo, new EntityWrapper<ContentVo>().andNew("categories={0}",
                            original.getName()));

                } else {
                    metas.setType(type);
                    metaDao.insert(metas);
                }
            }
        }
    }

    /**
     * 博客点击修改，修改标签
     *
     * @param cid
     * @param names
     * @param type
     */
    @Override
    public void saveMetas(Integer cid, String names, String type) {
        if(null == cid) {
            throw new TipException("项目关联id不能为空");
        }
        if(StringUtils.isNotBlank(names) && StringUtils.isNotBlank(type)) {
            String[] nameArr = StringUtils.split(names, ",");
            for(String name : nameArr) {
                this.saveOrUpdate(cid, name, type);
            }
        }
    }

    private void saveOrUpdate(Integer cid, String name, String type) {
        EntityWrapper<MetaVo> par = new EntityWrapper<>();
        par.where("1=1");
        if(name != null) {
            par.andNew("name={0}", name);
        }
        if(type != null) {
            par.and("type={0}", type);
        }
        List<MetaVo> metaVos = metaDao.selectList(par);

        int mid;
        MetaVo metas;
        if(metaVos.size() == 1) {
            metas = metaVos.get(0);
            mid = metas.getMid();
        } else if(metaVos.size() > 1) {
            throw new TipException("查询到多条数据");
        } else {
            metas = new MetaVo();
            metas.setSlug(name);
            metas.setName(name);
            metas.setType(type);
            metaDao.insert(metas);
            mid = metas.getMid();
        }
        if(mid != 0) {
            Long count = relationshipService.countById(cid, mid);
            if(count == 0) {
                RelationshipVoKey relationships = new RelationshipVoKey();
                relationships.setCid(cid);
                relationships.setMid(mid);
                relationshipService.insertVo(relationships);
            }
        }
    }

    /**
     * 删除标签或分类时字符串的改变
     *
     * @param name
     * @param metas
     * @return
     */
    private String reMeta(String name, String metas) {
        String[] ms = StringUtils.split(metas, ",");
        StringBuilder sbuf = new StringBuilder();
        for(String m : ms) {
            if(!name.equals(m)) {
                sbuf.append(",").append(m);
            }
        }
        if(sbuf.length() > 0) {
            return sbuf.substring(1);
        }
        return "";
    }

    @Override
    public void saveMeta(MetaVo metas) {
        if(null != metas) {
            metaDao.insert(metas);
        }
    }

    @Override
    public void update(MetaVo metas) {
        if(null != metas && null != metas.getMid()) {
            metaDao.updateById(metas);
        }
    }
}
