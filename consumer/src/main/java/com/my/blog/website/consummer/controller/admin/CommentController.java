package com.my.blog.website.consummer.controller.admin;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.service.ICommentService;
import com.my.blog.website.consummer.utils.TaleUtils;
import com.my.blog.website.consummer.controller.BaseController;
import com.my.blog.website.consummer.modal.Bo.RestResponseBo;
import com.my.blog.website.consummer.modal.Vo.CommentVo;
import com.my.blog.website.consummer.modal.Vo.UserVo;
import com.vdurmont.emoji.EmojiParser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 13 on 2017/2/26.
 */
@Controller
@RequestMapping("admin/comments")
public class CommentController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private ICommentService commentsService;

    /**
     * 评论列表
     *
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        UserVo users = this.user(request);
        EntityWrapper ew = new EntityWrapper();
        ew.ne("author_id", users.getUid());
        ew.orderBy("coid", false);
        PageInfo<CommentVo> commentsPaginator = commentsService.getCommentsWithPage(ew, page, limit);
        request.setAttribute("comments", commentsPaginator);
        return "admin/comment_list";
    }

    /**
     * 删除一条评论
     *
     * @param coid
     * @return
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam Integer coid) {
        try {
            CommentVo comments = commentsService.selectById(coid);
            if(null == comments) {
                return RestResponseBo.fail("不存在该评论");
            }
            commentsService.delete(coid, comments.getCid());
        } catch(Exception e) {
            String msg = "评论删除失败";
            if(e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    @PostMapping(value = "/status")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam Integer coid, @RequestParam String status) {
        try {
            CommentVo commentVo = commentsService.selectById(coid);
            commentVo.setStatus(status);
            commentsService.updateById(commentVo);
        } catch(Exception e) {
            String msg = "操作失败";
            if(e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    /**
     * 回复评论 暂时没有此功能
     *
     * @param coid
     * @param content
     * @param request
     * @return
     */
    @PostMapping(value = "")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo reply(@RequestParam Integer coid, @RequestParam String content, HttpServletRequest request) {
        if(null == coid || StringUtils.isBlank(content)) {
            return RestResponseBo.fail("请输入完整后评论");
        }

        if(content.length() > 2000) {
            return RestResponseBo.fail("请输入2000个字符以内的回复");
        }
        CommentVo c = commentsService.getCommentById(coid);
        if(null == c) {
            return RestResponseBo.fail("不存在该评论");
        }
        UserVo users = this.user(request);
        content = TaleUtils.cleanXSS(content);
        content = EmojiParser.parseToAliases(content);

        CommentVo comments = new CommentVo();
        comments.setAuthor(users.getUsername());
        comments.setAuthorId(users.getUid());
        comments.setCid(c.getCid());
        comments.setIp(request.getRemoteAddr());
        comments.setUrl(users.getHomeUrl());
        comments.setContent(content);
        comments.setMail(users.getEmail());
        comments.setParent(coid);
        try {
            commentsService.insert(comments);
            return RestResponseBo.ok();
        } catch(Exception e) {
            String msg = "回复失败";
            if(e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
    }

}
