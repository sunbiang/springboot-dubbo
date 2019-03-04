package com.my.blog.website.consummer.modal.Vo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * @author
 */
@TableName("t_options")
@Data
public class OptionVo implements Serializable {
    /**
     * 配置名称
     */
    @TableId(value = "name")
    private String name;

    /**
     * 配置值
     */
    private String value;

    private String description;

}