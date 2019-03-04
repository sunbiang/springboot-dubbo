package com.my.blog.website.consummer.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.consummer.constant.WebConst;
import com.my.blog.website.consummer.dao.MetaVoMapper;
import com.my.blog.website.consummer.dto.Types;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.service.IContentService;
import com.my.blog.website.consummer.service.IMetaService;
import com.my.blog.website.consummer.service.IRelationshipService;
import com.my.blog.website.consummer.utils.TaleUtils;
import com.my.blog.website.consummer.dao.ContentVoMapper;
import com.my.blog.website.consummer.modal.Vo.ContentVo;
import com.my.blog.website.consummer.utils.DateKit;
import com.my.blog.website.consummer.utils.Tools;
import com.vdurmont.emoji.EmojiParser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/3/13 013.
 */
@Service
public class ContentServiceImpl extends ServiceImpl<ContentVoMapper, ContentVo> implements IContentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);

    @Autowired
    private ContentVoMapper contentDao;

    @Resource
    private MetaVoMapper metaDao;

    @Resource
    private IRelationshipService relationshipService;

    @Resource
    private IMetaService metasService;

    /**
     * 新增博客
     *
     * @param contents
     */
    @Override
    public void publish(ContentVo contents) {
        if(null == contents) {
            throw new TipException("文章对象为空");
        }
        if(StringUtils.isBlank(contents.getTitle())) {
            throw new TipException("文章标题不能为空");
        }
        if(StringUtils.isBlank(contents.getContent())) {
            throw new TipException("文章内容不能为空");
        }
        int titleLength = contents.getTitle().length();
        if(titleLength > WebConst.MAX_TITLE_COUNT) {
            throw new TipException("文章标题过长");
        }
        int contentLength = contents.getContent().length();
        if(contentLength > WebConst.MAX_TEXT_COUNT) {
            throw new TipException("文章内容过长");
        }
        if(null == contents.getAuthorId()) {
            throw new TipException("请登录后发布文章");
        }
        if(StringUtils.isNotBlank(contents.getSlug())) {
            if(contents.getSlug().length() < 5) {
                throw new TipException("路径太短了");
            }
            if(!TaleUtils.isPath(contents.getSlug())) throw new TipException("您输入的路径不合法");
            EntityWrapper ew = new EntityWrapper();
            ew.where("1=1");
            ew.andNew("type={0}", contents.getType())
                    .andNew("status={0}", contents.getSlug());
            long count = contentDao.selectCount(ew);
            if(count > 0) throw new TipException("该路径已经存在，请重新输入");
        } else {
            contents.setSlug(null);
        }

        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

        int time = DateKit.getCurrentUnixTime();
        contents.setCreated(time);
        contents.setModified(time);
        contents.setHits(0);
        contents.setCommentsNum(0);

        String tags = contents.getTags();
        String categories = contents.getCategories();
        contentDao.insert(contents);
        Integer cid = contents.getCid();

        metasService.saveMetas(cid, tags, Types.TAG.getType());
        metasService.saveMetas(cid, categories, Types.CATEGORY.getType());
    }

    /**
     * 首页的分页
     *
     * @param p     当前页
     * @param limit 每页条数
     * @return
     */
    @Override
    public PageInfo<ContentVo> getContents(Integer p, Integer limit) {
        LOGGER.debug("Enter getContents method");
        EntityWrapper ew = new EntityWrapper();
        ew.where("1=1");
        ew.andNew("type={0}", Types.ARTICLE.getType());
        ew.and("status={0}", Types.PUBLISH.getType());
        ew.orderBy("created", false);
        PageHelper.startPage(p, limit);
        List<ContentVo> data = contentDao.selectList(ew);
        PageInfo<ContentVo> pageInfo = new PageInfo<>(data);
        LOGGER.debug("Exit getContents method");
        return pageInfo;
    }

    /**
     * 点击编辑时进行的操作
     *
     * @param id id
     * @return
     */
    @Override
    public ContentVo getContents(String id) {
        if(StringUtils.isNotBlank(id)) {
            if(Tools.isNumber(id)) {
                ContentVo contentVo = contentDao.selectById(Integer.valueOf(id));
                if(contentVo != null) {
                    contentVo.setHits(contentVo.getHits() + 1);
                    contentDao.updateById(contentVo);
                }
                return contentVo;
            } else {
                EntityWrapper<ContentVo> ew = new EntityWrapper();
                ew.where("1=1");
                ew.andNew("slug={0}", id);
                List<ContentVo> contentVos = contentDao.selectList(ew);
                if(contentVos.size() != 1) {
                    throw new TipException("query content by id and return is not one");
                }
                return contentVos.get(0);
            }
        }
        return null;
    }

    @Override
    public void updateContentByCid(ContentVo contentVo) {
        if(null != contentVo && null != contentVo.getCid()) {
            contentDao.updateById(contentVo);
        }
    }

    @Override
    public PageInfo<ContentVo> getArticles(Integer mid, int page, int limit) {
        int total = metaDao.countWithSql(mid);
        PageHelper.startPage(page, limit);
        List<ContentVo> list = contentDao.findByCatalog(mid);
        PageInfo<ContentVo> paginator = new PageInfo<>(list);
        paginator.setTotal(total);
        return paginator;
    }

    /**
     * 搜索
     *
     * @param keyword keyword
     * @param page    page
     * @param limit   limit
     * @return
     */
    @Override
    public PageInfo<ContentVo> getArticles(String keyword, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        EntityWrapper<ContentVo> ew = new EntityWrapper();
        ew.eq("type", Types.ARTICLE.getType());
        ew.and("status={0}", Types.PUBLISH.getType());
        ew.like("title", "%" + keyword + "%");
        ew.orderBy("created", false);
        List<ContentVo> contentVos = contentDao.selectList(ew);
        return new PageInfo<>(contentVos);
    }

    /**
     * 文章列表
     *
     * @param ew    条件构造器
     * @param page  当前页
     * @param limit 每页的数据
     * @return
     */
    @Override
    public PageInfo<ContentVo> getArticlesWithpage(EntityWrapper ew, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<ContentVo> contentVos = contentDao.selectList(ew);
        return new PageInfo<>(contentVos);
    }

    /**
     * 文章更新
     *
     * @param contents
     */
    @Override
    public void updateArticle(ContentVo contents) {

        if(null == contents || null == contents.getCid()) {
            throw new TipException("文章对象不能为空");
        }
        if(StringUtils.isBlank(contents.getTitle())) {
            throw new TipException("文章标题不能为空");
        }
        if(StringUtils.isBlank(contents.getContent())) {
            throw new TipException("文章内容不能为空");
        }
        if(contents.getTitle().length() > 200) {
            throw new TipException("文章标题过长");
        }
        if(contents.getContent().length() > 65000) {
            throw new TipException("文章内容过长");
        }
        if(null == contents.getAuthorId()) {
            throw new TipException("请登录后发布文章");
        }
        if(StringUtils.isBlank(contents.getSlug())) {
            contents.setSlug(null);
        }
        int time = DateKit.getCurrentUnixTime();
        contents.setModified(time);
        contents.setCreated(DateKit.getCurrentUnixTime());
        Integer cid = contents.getCid();
        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

        contentDao.updateById(contents);
        relationshipService.deleteById(cid);
        metasService.saveMetas(cid, contents.getTags(), Types.TAG.getType());
        metasService.saveMetas(cid, contents.getCategories(), Types.CATEGORY.getType());
    }
}
