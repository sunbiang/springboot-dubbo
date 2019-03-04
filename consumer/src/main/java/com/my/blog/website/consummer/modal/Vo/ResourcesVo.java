package com.my.blog.website.consummer.modal.Vo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

import lombok.Data;

@Data
@TableName("resources")
public class ResourcesVo implements Serializable {
    @TableId(value = "id" ,type = IdType.AUTO)
    private Integer id;

    private String name;

    private String resUrl;

    private Integer type;

    private Integer parentId;

    private Integer sort;
}
