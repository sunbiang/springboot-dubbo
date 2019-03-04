package com.my.blog.website.consummer.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.my.blog.website.consummer.modal.Bo.ArchiveBo;
import com.my.blog.website.consummer.modal.Vo.ContentVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//@Component
@Mapper
public interface ContentVoMapper extends BaseMapper<ContentVo> {
    @Select("select FROM_UNIXTIME(created, '%Y年%m月') as date, count(*) as count from t_contents where type = 'post' and status = 'publish' group by date order by date desc")
    List<ArchiveBo> findReturnArchiveBo();

    //保留
    @Select("select a.*" +
            "from t_contents a left join t_relationships b on a.cid = b.cid" +
            "where b.mid = #{value} and a.status = 'publish' and a.type = 'post'" +
            "order by a.created desc")
    List<ContentVo> findByCatalog(Integer mid);
}