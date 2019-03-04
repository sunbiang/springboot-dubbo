package com.my.blog.website.consummer.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.consummer.dao.CommentVoMapper;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.modal.Vo.ContentVo;
import com.my.blog.website.consummer.service.ICommentService;
import com.my.blog.website.consummer.service.IContentService;
import com.my.blog.website.consummer.utils.TaleUtils;
import com.my.blog.website.consummer.modal.Bo.CommentBo;
import com.my.blog.website.consummer.modal.Vo.CommentVo;
import com.my.blog.website.consummer.utils.DateKit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * Created by BlueT on 2017/3/16.
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentVoMapper, CommentVo> implements ICommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Resource
    private CommentVoMapper commentDao;

    @Resource
    private IContentService contentService;

    /**
     * 添加评论
     *
     * @param comments
     */
    @Override
    public void insertComment(CommentVo comments) {
        if(null == comments) {
            throw new TipException("评论对象为空");
        }
        if(StringUtils.isBlank(comments.getAuthor())) {
            comments.setAuthor("热心网友");
        }
        if(StringUtils.isNotBlank(comments.getMail()) && !TaleUtils.isEmail(comments.getMail())) {
            throw new TipException("请输入正确的邮箱格式");
        }
        if(StringUtils.isBlank(comments.getContent())) {
            throw new TipException("评论内容不能为空");
        }
        if(comments.getContent().length() < 5 || comments.getContent().length() > 2000) {
            throw new TipException("评论字数在5-2000个字符");
        }
        if(null == comments.getCid()) {
            throw new TipException("评论文章不能为空");
        }
        ContentVo contents = contentService.getContents(String.valueOf(comments.getCid()));
        if(null == contents) {
            throw new TipException("不存在的文章");
        }
        comments.setOwnerId(contents.getAuthorId());
        comments.setCreated(DateKit.getCurrentUnixTime());
        commentDao.insert(comments);
        contents.setCommentsNum(contents.getCommentsNum() + 1);
        contentService.updateById(contents);
    }

    /**
     * 文章下面的评论分页
     *
     * @param cid
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageInfo<CommentBo> getComments(Integer cid, int page, int limit) {

        if(null != cid) {
            PageHelper.startPage(page, limit);
            EntityWrapper ew = new EntityWrapper();
            ew.eq("cid", cid);
            ew.and("parent={0}", 0);
            ew.orderBy("coid", false);
            List<CommentVo> parents = commentDao.selectList(ew);
            PageInfo<CommentVo> commentPaginator = new PageInfo<>(parents);
            PageInfo<CommentBo> returnBo = copyPageInfo(commentPaginator);
            if(parents.size() != 0) {
                List<CommentBo> comments = new ArrayList<>(parents.size());
                for(CommentVo commentVo : parents) {
                    CommentBo commentBo = new CommentBo(commentVo);
                    comments.add(commentBo);
                }
                returnBo.setList(comments);
            }
            return returnBo;
        }
        return null;
    }

    /**
     * 后台评论管理的分页
     *
     * @param ew
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageInfo<CommentVo> getCommentsWithPage(EntityWrapper ew, int page, int limit) {
        PageHelper.startPage(page, limit);
        List<CommentVo> commentVos = commentDao.selectList(ew);
        PageInfo<CommentVo> pageInfo = new PageInfo<>(commentVos);
        return pageInfo;
    }

    /**
     * 更改评论的状态
     *
     * @param comments
     */
    @Override
    public void update(CommentVo comments) {
        if(null != comments && null != comments.getCoid()) {
            commentDao.updateById(comments);
        }
    }

    /**
     * 删除评论
     *
     * @param coid
     * @param cid
     */
    @Override
    public void delete(Integer coid, Integer cid) {
        if(null == coid) {
            throw new TipException("主键为空");
        }
        commentDao.deleteById(coid);
        ContentVo contents = contentService.selectById(cid);
        if(null != contents && contents.getCommentsNum() > 0) {
            contents.setCommentsNum(contents.getCommentsNum() - 1);
            contentService.updateById(contents);
        }
    }

    @Override
    public CommentVo getCommentById(Integer coid) {
        if(null != coid) {
            return commentDao.selectById(coid);
        }
        return null;
    }

    /**
     * copy原有的分页信息，除数据
     *
     * @param ordinal
     * @param <T>
     * @return
     */
    private <T> PageInfo<T> copyPageInfo(PageInfo ordinal) {
        PageInfo<T> returnBo = new PageInfo<T>();
        returnBo.setPageSize(ordinal.getPageSize());
        returnBo.setPageNum(ordinal.getPageNum());
        returnBo.setEndRow(ordinal.getEndRow());
        returnBo.setTotal(ordinal.getTotal());
        returnBo.setHasNextPage(ordinal.isHasNextPage());
        returnBo.setHasPreviousPage(ordinal.isHasPreviousPage());
        returnBo.setIsFirstPage(ordinal.isIsFirstPage());
        returnBo.setIsLastPage(ordinal.isIsLastPage());
        returnBo.setNavigateFirstPage(ordinal.getNavigateFirstPage());
        returnBo.setNavigateLastPage(ordinal.getNavigateLastPage());
        returnBo.setNavigatepageNums(ordinal.getNavigatepageNums());
        returnBo.setSize(ordinal.getSize());
        returnBo.setPrePage(ordinal.getPrePage());
        returnBo.setNextPage(ordinal.getNextPage());
        return returnBo;
    }
}
