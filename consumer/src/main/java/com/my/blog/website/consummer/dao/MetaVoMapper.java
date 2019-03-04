package com.my.blog.website.consummer.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.my.blog.website.consummer.modal.Vo.MetaVo;
import com.my.blog.website.consummer.dto.MetaDto;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MetaVoMapper extends BaseMapper<MetaVo> {
    @Select(" select a.*, count(b.cid) as count from t_metas a left join `t_relationships` b on a.mid = b.mid where a.type = #{type} group by a.mid order by #{order} limit #{limit}")
    List<MetaDto> selectFromSql(Map<String, Object> paraMap);

    //根据姓名和类型查找
    @Select("select a.*, count(b.cid) as count from t_metas a left join `t_relationships` b on a.mid = b.mid where a.type = #{type} and a.name = #{name} group by a.mid")
    MetaDto selectDtoByNameAndType(@Param("name") String name, @Param("type") String type);
    @Select("SELECT count(0)" +
            "FROM t_contents a LEFT JOIN t_relationships b ON a.cid = b.cid" +
            "WHERE b.mid = #{mid} AND a.status = 'publish' AND a.type = 'post';")
    Integer countWithSql(@Param("mid") Integer mid);
}