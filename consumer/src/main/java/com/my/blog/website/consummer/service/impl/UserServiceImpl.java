package com.my.blog.website.consummer.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.dao.UserVoMapper;
import com.my.blog.website.consummer.modal.Vo.UserVo;
import com.my.blog.website.consummer.service.IUserService;
import com.my.blog.website.consummer.utils.TaleUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by BlueT on 2017/3/3.
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserVoMapper, UserVo> implements IUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserVoMapper userDao;

    @Override
    public Integer insertUser(UserVo userVo) {
        Integer uid = null;
        if(StringUtils.isNotBlank(userVo.getUsername()) && StringUtils.isNotBlank(userVo.getEmail())) {
//            用户密码加密
            String encodePwd = TaleUtils.MD5encode(userVo.getUsername() + userVo.getPassword());
            userVo.setPassword(encodePwd);
            userDao.insert(userVo);
        }
        return userVo.getUid();
    }

    @Override
    public UserVo queryUserById(Integer uid) {
        UserVo userVo = null;
        if(uid != null) {
            userVo = userDao.selectById(uid);
        }
        return userVo;
    }

    @Override
    public UserVo login(String username, String password) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }
        long count = userDao.findCount(username);

        if(count < 1) {
            throw new TipException("不存在该用户");
        }
//        String pwd = TaleUtils.MD5encode(password);
        EntityWrapper ew = new EntityWrapper();
        ew.where("1=1");
        ew.andNew("username={0}", username);
        ew.and("password={0}", password);
        List<UserVo> userVos = userDao.selectList(ew);
        if(userVos.size() != 1) {
            throw new TipException("用户名或密码错误");
        }
        return userVos.get(0);
    }

    @Override
    public void updateByUid(UserVo userVo) {
        if(null == userVo || null == userVo.getUid()) {
            throw new TipException("userVo is null");
        }
        int i = userDao.updateById(userVo);
        if(i != 1) {
            throw new TipException("update user by uid and retrun is not one");
        }
    }
}
