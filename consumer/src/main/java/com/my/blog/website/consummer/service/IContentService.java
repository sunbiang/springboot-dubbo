package com.my.blog.website.consummer.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.consummer.modal.Vo.ContentVo;

/**
 * Created by Administrator on 2017/3/13 013.
 */
public interface IContentService extends IService<ContentVo> {


    /**
     * 发布文章
     *
     * @param contents
     */
    void publish(ContentVo contents);

    /**
     * 查询文章返回多条数据
     *
     * @param p     当前页
     * @param limit 每页条数
     * @return ContentVo
     */
    PageInfo<ContentVo> getContents(Integer p, Integer limit);


    /**
     * 根据id或slug获取文章
     *
     * @param id id
     * @return ContentVo
     */
    ContentVo getContents(String id);

    /**
     * 根据主键更新
     *
     * @param contentVo contentVo
     */
    void updateContentByCid(ContentVo contentVo);


    /**
     * 查询分类/标签下的文章归档
     *
     * @param mid   mid
     * @param page  page
     * @param limit limit
     * @return ContentVo
     */
    PageInfo<ContentVo> getArticles(Integer mid, int page, int limit);

    /**
     * 搜索、分页
     *
     * @param keyword keyword
     * @param page    page
     * @param limit   limit
     * @return ContentVo
     */
    PageInfo<ContentVo> getArticles(String keyword, Integer page, Integer limit);


    /**
     * @param ew
     * @param page
     * @param limit
     * @return
     */
    PageInfo<ContentVo> getArticlesWithpage(EntityWrapper ew, Integer page, Integer limit);


    /**
     * 编辑文章
     *
     * @param contents
     */
    void updateArticle(ContentVo contents);


}
