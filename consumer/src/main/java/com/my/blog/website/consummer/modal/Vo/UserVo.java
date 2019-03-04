package com.my.blog.website.consummer.modal.Vo;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

import lombok.Data;

/**
 * @author
 */
@TableName("t_users")
@Data
public class UserVo implements Serializable {
    /**
     * user表主键
     */
    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户的邮箱
     */
    private String email;

    /**
     * 用户的主页
     */
    @TableField("home_url")
    private String homeUrl;

    /**
     * 用户显示的名称
     */
    @TableField("screen_name")
    private String screenName;

    /**
     * 用户注册时的GMT unix时间戳
     */
    private Integer created;

    /**
     * 最后活动时间
     */
    private Integer activated;

    /**
     * 上次登录最后活跃时间
     */
    private Integer logged;

    /**
     * 用户组
     */
    @TableField("group_name")
    private String groupName;

}