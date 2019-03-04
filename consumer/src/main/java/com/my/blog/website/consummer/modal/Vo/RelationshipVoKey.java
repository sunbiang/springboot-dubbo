package com.my.blog.website.consummer.modal.Vo;


import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * @author
 */
@TableName("t_relationships")
@Data
public class RelationshipVoKey implements Serializable {
    /**
     * 内容主键
     */
    @TableId(value = "cid")
    private Integer cid;

    /**
     * 项目主键
     */
    @TableId(value = "id")
    private Integer mid;

}