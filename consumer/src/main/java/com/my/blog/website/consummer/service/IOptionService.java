package com.my.blog.website.consummer.service;

import com.baomidou.mybatisplus.service.IService;
import com.my.blog.website.consummer.modal.Vo.OptionVo;

import java.util.List;
import java.util.Map;

/**
 * options的接口
 * Created by BlueT on 2017/3/7.
 */
public interface IOptionService extends IService<OptionVo> {

    void insertOption(OptionVo optionVo);

    void insertOption(String name, String value);

    List<OptionVo> getOptions();


    /**
     * 保存一组配置
     *
     * @param options
     */
    void saveOptions(Map<String, String> options);
}
