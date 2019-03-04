package com.my.blog.website.consummer.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.my.blog.website.consummer.modal.Vo.ResourcesVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ResourcesVoMapper extends BaseMapper<ResourcesVo> {
    @Select("SELECT res.* from resources res LEFT JOIN role_resources rres on res.id=rres.resourcesId where rres.roleId=#{id}")
    public List<ResourcesVo> findRoleId(@Param("id") Integer id);
}
