package com.my.blog.website;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.entity.AttachVo;


/**
 * @Author: sun
 * @Date: 2018/12/18 10:35
 */
public interface AttachService {
    /**
     * 分页查询附件
     *
     * @param page
     * @param limit
     * @return
     */
    PageInfo<AttachVo> getAttachs(Integer page, Integer limit);


    /**
     * 保存附件
     *
     * @param fname
     * @param fkey
     * @param ftype
     * @param author
     */
    void save(String fname, String fkey, String ftype, Integer author);


    /**
     * 根据id查找
     */
    AttachVo selectById(Integer id);


    /**
     * 根据id删除
     */
    void deleteById(Integer id);

    /**
     * 查询数量
     */
    int selectCount(EntityWrapper entityWrapper);

}
