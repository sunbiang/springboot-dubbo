package com.my.blog.website.consummer.modal.Vo;


import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

import lombok.Data;

/**
 * @author
 */
@TableName("t_metas")
@Data
public class MetaVo implements Serializable {
    /**
     * 项目主键
     */
    @TableId(value = "mid", type = IdType.AUTO)
    private Integer mid;

    /**
     * 名称
     */
    private String name;

    /**
     * 项目缩略名
     */
    private String slug;

    /**
     * 项目类型
     */
    private String type;

    /**
     * 选项描述
     */
    private String description;

    /**
     * 项目排序
     */
    private Integer sort;

    private Integer parent;

}