package com.my.blog.website.consummer.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.my.blog.website.consummer.modal.Vo.RoleVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleVoMapper extends BaseMapper<RoleVo> {
    @Select("select r.* from role r LEFT JOIN user_role ur ON r.id=ur.roleId where ur.userId=#{id}")
    List<RoleVo> findUserId(@Param("id") Integer id);
}
