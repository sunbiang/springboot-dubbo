package com.my.blog.website.consummer.controller.admin;

import com.my.blog.website.consummer.constant.WebConst;
import com.my.blog.website.consummer.dto.LogActions;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.modal.Bo.StatisticsBo;
import com.my.blog.website.consummer.modal.Vo.CommentVo;
import com.my.blog.website.consummer.modal.Vo.ContentVo;
import com.my.blog.website.consummer.modal.Vo.LogVo;
import com.my.blog.website.consummer.modal.Vo.ResourcesVo;
import com.my.blog.website.consummer.modal.Vo.RoleVo;
import com.my.blog.website.consummer.service.IResourcesService;
import com.my.blog.website.consummer.service.IRoleService;
import com.my.blog.website.consummer.service.ISiteService;
import com.my.blog.website.consummer.service.IUserService;
import com.my.blog.website.consummer.utils.TaleUtils;
import com.my.blog.website.consummer.controller.BaseController;
import com.my.blog.website.consummer.modal.Bo.RestResponseBo;
import com.my.blog.website.consummer.modal.Vo.UserVo;
import com.my.blog.website.consummer.service.ILogService;
import com.my.blog.website.consummer.utils.Commons;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户后台登录/登出
 * Created by BlueT on 2017/3/11.
 */
@Controller
@RequestMapping("/admin")
//@Transactional(rollbackFor = TipException.class)
public class AuthController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private IUserService usersService;

    @Resource
    private ILogService logService;

    @Resource
    private ISiteService siteService;

    @Resource
    private DefaultWebSessionManager sessionDAO;


    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login() {
        return "admin/login";
    }


    /**
     * 管理后台登录
     *
     * @param username
     * @param password
     * @param remeber_me
     * @param request
//     * @param response
     * @return
     */
//    @PostMapping(value = "login")
//    @ResponseBody
//    public RestResponseBo doLogin(@RequestParam String username,
//                                  @RequestParam String password,
//                                  @RequestParam(required = false) Boolean remeber_me,
//                                  HttpServletRequest request,
//                                  HttpServletResponse response) {
//
//        Integer error_count = cache.get("login_error_count");
//        try {
//            UserVo user = usersService.login(username, password);
//            request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);
//            if(remeber_me) {
//                TaleUtils.setCookie(response, user.getUid());
//            }
//            logService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), user.getUid());
//        } catch(Exception e) {
//            error_count = null == error_count ? 1 : error_count + 1;
//            if(error_count > 3) {
//                return RestResponseBo.fail("您输入密码已经错误超过3次，请10分钟后尝试");
//            }
//            cache.set("login_error_count", error_count, 10 * 60);
//            String msg = "登录失败";
//            if(e instanceof TipException) {
//                msg = e.getMessage();
//            } else {
//                LOGGER.error(msg, e);
//            }
//            return RestResponseBo.fail(msg);
//        }
//        return RestResponseBo.ok();
//    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public RestResponseBo doLogin(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam(required = false) Boolean remeber_me,
                                  HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        try {
            String msg = "";
            Collection<Session> sessions = sessionDAO.getSessionDAO().getActiveSessions();
            for (Session session : sessions) {
          if(username.equals(String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)))){
              msg="此用户已登录";
          }
            }
            if(msg.equals("")){
                subject.login(usernamePasswordToken);
                return RestResponseBo.ok();
            }else {
                return RestResponseBo.fail(msg);
            }
        } catch(AuthenticationException e) {
            usernamePasswordToken.clear();
            String msg = "用户或密码不正确！";
            request.setAttribute("msg", msg);
            return RestResponseBo.fail(msg);
        }

    }


    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("/admin/logout")
    public void logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        try {
            logService.insertLog(LogActions.LOGOUT.getAction(), null, request.getRemoteAddr(), this.getUid(request));
            response.sendRedirect(Commons.site_login());//退出的时候重定向到登录界面
        } catch(IOException e) {
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }
    }
}
