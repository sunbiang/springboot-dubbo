package com.my.blog.website.consummer.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.consummer.modal.Bo.CommentBo;
import com.my.blog.website.consummer.modal.Vo.CommentVo;

/**
 * Created by BlueT on 2017/3/16.
 */
public interface ICommentService extends IService<CommentVo> {

    /**
     * 保存对象
     *
     * @param commentVo
     */
    void insertComment(CommentVo commentVo);

    /**
     * 获取文章下的评论
     *
     * @param cid
     * @param page
     * @param limit
     * @return CommentBo
     */
    PageInfo<CommentBo> getComments(Integer cid, int page, int limit);

    /**
     * 获取文章下的评论
     *
     * @param ew
     * @param page
     * @param limit
     * @return CommentVo
     */
    PageInfo<CommentVo> getCommentsWithPage(EntityWrapper ew, int page, int limit);


    /**
     * 根据主键查询评论
     *
     * @param coid
     * @return
     */
    CommentVo getCommentById(Integer coid);


    /**
     * 删除评论，暂时没用
     *
     * @param coid
     * @param cid
     * @throws Exception
     */
    void delete(Integer coid, Integer cid);

    /**
     * 更新评论状态
     *
     * @param comments
     */
    void update(CommentVo comments);

}
