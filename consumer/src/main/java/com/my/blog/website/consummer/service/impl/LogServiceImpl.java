package com.my.blog.website.consummer.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.my.blog.website.consummer.constant.WebConst;
import com.my.blog.website.consummer.modal.Vo.LogVo;
import com.my.blog.website.consummer.service.ILogService;
import com.my.blog.website.consummer.dao.LogVoMapper;
import com.my.blog.website.consummer.utils.DateKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by BlueT on 2017/3/4.
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogVoMapper, LogVo> implements ILogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

    @Resource
    private LogVoMapper logDao;

    @Override
    public void insertLog(LogVo logVo) {
        logDao.insert(logVo);
    }

    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {
        LogVo logs = new LogVo();
        logs.setAction(action);
        logs.setData(data);
        logs.setIp(ip);
        logs.setAuthorId(authorId);
        logs.setCreated(DateKit.getCurrentUnixTime());
        logDao.insert(logs);
    }

    @Override
    public List<LogVo> getLogs(int page, int limit) {
        LOGGER.debug("Enter getLogs method:page={},linit={}", page, limit);
        if(page <= 0) {
            page = 1;
        }
        if(limit < 1 || limit > WebConst.MAX_POSTS) {
            limit = 10;
        }
        PageHelper.startPage((page - 1) * limit, limit);
        List<LogVo> logVos = logDao.selectList(new EntityWrapper<LogVo>().orderBy("id", false));
        LOGGER.debug("Exit getLogs method");
        return logVos;
    }
}
