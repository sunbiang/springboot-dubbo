package com.my.blog.website.consummer.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.my.blog.website.consummer.dao.OptionVoMapper;
import com.my.blog.website.consummer.modal.Vo.OptionVo;
import com.my.blog.website.consummer.service.IOptionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * options表的service
 * Created by BlueT on 2017/3/7.
 */
@Service
public class OptionServiceImpl extends ServiceImpl<OptionVoMapper, OptionVo> implements IOptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionServiceImpl.class);

    @Resource
    private OptionVoMapper optionDao;

    @Override
    public void insertOption(OptionVo optionVo) {
        LOGGER.debug("Enter insertOption method:optionVo={}", optionVo);
        optionDao.insert(optionVo);
        LOGGER.debug("Exit insertOption method.");
    }

    @Override
    public void insertOption(String name, String value) {
        LOGGER.debug("Enter insertOption method:name={},value={}", name, value);
        OptionVo optionVo = new OptionVo();
        optionVo.setName(name);
        optionVo.setValue(value);
        if(optionDao.selectList(new EntityWrapper()).size() == 0) {
            optionDao.insert(optionVo);
        } else {
            optionDao.updateById(optionVo);
        }
        LOGGER.debug("Exit insertOption method.");
    }

    @Override
    public void saveOptions(Map<String, String> options) {
        if(null != options && !options.isEmpty()) {
            for(Map.Entry<String, String> entry : options.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                insertOption(key, value);
            }
        }
    }

    @Override
    public List<OptionVo> getOptions() {
        return optionDao.selectList(new EntityWrapper<OptionVo>());
    }
}
