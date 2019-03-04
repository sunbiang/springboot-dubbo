package com.my.blog.website.consummer.controller.admin;

import com.my.blog.website.consummer.constant.WebConst;
import com.my.blog.website.consummer.dto.LogActions;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.modal.Bo.StatisticsBo;
import com.my.blog.website.consummer.modal.Vo.LogVo;
import com.my.blog.website.consummer.modal.Vo.ResourcesVo;
import com.my.blog.website.consummer.modal.Vo.RoleVo;
import com.my.blog.website.consummer.service.IResourcesService;
import com.my.blog.website.consummer.service.IRoleService;
import com.my.blog.website.consummer.service.IUserService;
import com.my.blog.website.consummer.controller.BaseController;
import com.my.blog.website.consummer.modal.Bo.RestResponseBo;
import com.my.blog.website.consummer.modal.Vo.CommentVo;
import com.my.blog.website.consummer.modal.Vo.ContentVo;
import com.my.blog.website.consummer.modal.Vo.UserVo;
import com.my.blog.website.consummer.service.ILogService;
import com.my.blog.website.consummer.service.ISiteService;
import com.my.blog.website.consummer.utils.GsonUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//import com.sun.deploy.net.HttpResponse;

/**
 * 后台管理首页
 * Created by Administrator on 2017/3/9 009.
 */
@Controller("adminIndexController")
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class IndexController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Resource
    private ISiteService siteService;

    @Resource
    private ILogService logService;

    @Resource
    private IUserService userService;

    @Resource
    private IRoleService roleService;

    @Resource
    private IResourcesService resourcesService;

    /**
     * 页面跳转
     *
     * @return
     */
    @GetMapping(value = { "", "/index" })
    public String index(HttpServletRequest request) {
        LOGGER.info("Enter admin index method");
        List<CommentVo> comments = siteService.recentComments(5);
        List<ContentVo> contents = siteService.recentContents(5);
        StatisticsBo statistics = siteService.getStatistics();
        // 取最新的20条日志
        List<LogVo> logs = logService.getLogs(1, 5);

        request.setAttribute("comments", comments);
        request.setAttribute("articles", contents);
        request.setAttribute("statistics", statistics);
        request.setAttribute("logs", logs);
        LOGGER.info("Exit admin index method");
        return "admin/index";
    }

    /**
     * 加载本地菜单
     * @return
     */
    @RequestMapping(value = "/localMenu",method = RequestMethod.POST)
    @ResponseBody
    public List<ResourcesVo> localMenu(){
        UserVo userVo = (UserVo) SecurityUtils.getSubject().getSession().getAttribute(WebConst.LOGIN_SESSION_KEY);
        Integer id = userVo.getUid();
        List<RoleVo> list = roleService.findUserId(id);
        List<ResourcesVo> resourcesVoList = null;
        for(RoleVo roleVo : list){
            resourcesVoList  = resourcesService.findRoleId(roleVo.getId());
        }
        return resourcesVoList;
    }

    /**
     * 个人设置页面
     */
    @GetMapping(value = "profile")
    public String profile() {
        return "admin/profile";
    }

    /**
     * admin 退出登录
     *
     * @return
     */
    @GetMapping(value = "logout")
    public String logout() {
        System.out.println("index-----------logout");
        return "admin/login";
    }




    /**
     * 保存个人信息
     */
    @PostMapping(value = "/profile")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo saveProfile(UserVo userVo, HttpServletRequest request, HttpSession session) {
        UserVo users = this.user(request);
        if(userVo != null) {
            users.setScreenName(userVo.getScreenName());
            users.setEmail(userVo.getEmail());
            userService.updateById(users);
            logService.insertLog(LogActions.UP_INFO.getAction(), GsonUtils.toJsonString(userVo),
                    request.getRemoteAddr(), this.getUid(request));

            //更新session中的数据
            UserVo original = (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            original.setScreenName(userVo.getScreenName());
            original.setEmail(userVo.getEmail());
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, original);
        }
        return RestResponseBo.ok();
    }

    /**
     * 修改密码
     */
    @PostMapping(value = "/password")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo upPwd(@RequestParam String oldPassword, @RequestParam String password,
                                HttpServletRequest request, HttpSession session) {
        UserVo users = this.user(request);
        if(StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)) {
            return RestResponseBo.fail("请确认信息输入完整");
        }

        if(!users.getPassword().equals(oldPassword)) {
            return RestResponseBo.fail("旧密码错误");
        }
        if(password.length() < 5 || password.length() > 33) {
            return RestResponseBo.fail("请输入6-14位密码");
        }

        try {
            users.setPassword(password);
            userService.updateById(users);
            logService.insertLog(LogActions.UP_PWD.getAction(), null, request.getRemoteAddr(), this.getUid(request));

            //更新session中的数据
            UserVo original = (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            original.setPassword(password);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, original);
            return RestResponseBo.ok();
        } catch(Exception e) {
            String msg = "密码修改失败";
            if(e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
    }
}
