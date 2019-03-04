package com.my.blog.website.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

import lombok.Data;

/**
 * @author
 */
@TableName("t_attach")
@Data
public class AttachVo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String fname;

    private String ftype;

    private String fkey;
    @TableField("author_id")
    private Integer authorId;

    private Integer created;


}