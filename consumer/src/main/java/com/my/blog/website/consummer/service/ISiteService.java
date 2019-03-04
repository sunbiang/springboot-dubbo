package com.my.blog.website.consummer.service;

import com.my.blog.website.consummer.dto.MetaDto;
import com.my.blog.website.consummer.modal.Bo.ArchiveBo;
import com.my.blog.website.consummer.modal.Bo.BackResponseBo;
import com.my.blog.website.consummer.modal.Bo.StatisticsBo;
import com.my.blog.website.consummer.modal.Vo.CommentVo;
import com.my.blog.website.consummer.modal.Vo.ContentVo;

import java.util.List;

/**
 * 站点服务
 * <p>
 * Created by 13 on 2017/2/23.
 */
public interface ISiteService {


    /**
     * 最新收到的评论
     *
     * @param limit
     * @return
     */
    List<CommentVo> recentComments(int limit);

    /**
     * 最新发表的文章
     *
     * @param limit
     * @return
     */
    List<ContentVo> recentContents(int limit);

    /**
     * 查询一条评论
     *
     * @param coid
     * @return
     */
    CommentVo getComment(Integer coid);

    /**
     * 系统备份
     *
     * @param bk_type
     * @param bk_path
     * @param fmt
     * @return
     */
    BackResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception;


    /**
     * 获取后台统计数据
     *
     * @return
     */
    StatisticsBo getStatistics();

    /**
     * 查询文章归档
     *
     * @return
     */
    List<ArchiveBo> getArchives();

    /**
     * 获取分类/标签列表
     *
     * @return
     */
    List<MetaDto> metas(String type, String orderBy, int limit);

}
