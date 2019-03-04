package com.my.blog.website.consummer.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.my.blog.website.consummer.modal.Vo.UserVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserVoMapper extends BaseMapper<UserVo> {


    @Select("select count(1) from t_users where username=#{username}")
    Long findCount(@Param("username") String username);


}