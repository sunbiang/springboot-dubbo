package com.my.blog.website.consummer.controller.admin;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.consummer.constant.WebConst;
import com.my.blog.website.consummer.dto.LogActions;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.modal.Vo.MetaVo;
import com.my.blog.website.consummer.modal.Vo.ResourcesVo;
import com.my.blog.website.consummer.modal.Vo.RoleVo;
import com.my.blog.website.consummer.service.IContentService;
import com.my.blog.website.consummer.service.IMetaService;
import com.my.blog.website.consummer.service.IRelationshipService;
import com.my.blog.website.consummer.controller.BaseController;
import com.my.blog.website.consummer.dto.Types;
import com.my.blog.website.consummer.modal.Bo.RestResponseBo;
import com.my.blog.website.consummer.modal.Vo.ContentVo;
import com.my.blog.website.consummer.modal.Vo.UserVo;
import com.my.blog.website.consummer.service.ILogService;
import com.my.blog.website.consummer.service.IResourcesService;
import com.my.blog.website.consummer.service.IRoleService;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by 13 on 2017/2/21.
 */
@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = TipException.class)
public class ArticleController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private IContentService contentsService;

    @Autowired
    private IMetaService metasService;
    @Autowired
    private IRelationshipService iRelationshipService;

    @Autowired
    private ILogService logService;

    @Resource
    private IRoleService roleService;

    @Resource
    private IResourcesService resourcesService;

    /**
     * 文章列表
     *
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        EntityWrapper ew = new EntityWrapper();
        ew.where("1=1");
        ew.andNew("type={0}", Types.ARTICLE.getType());
        ew.orderBy("created", false);
        PageInfo<ContentVo> contentsPaginator = contentsService.getArticlesWithpage(ew, page, limit);
        request.setAttribute("articles", contentsPaginator);
        return "admin/article_list";
    }

    /**
     * 文章发表
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/publish")
    public String newArticle(HttpServletRequest request) {
        List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        return "admin/article_edit";
    }

    /**
     * 文章编辑
     *
     * @param cid
     * @param request
     * @return
     */
    @GetMapping(value = "/{cid}")
    public String editArticle(@PathVariable String cid, HttpServletRequest request) {
        ContentVo contents = contentsService.getContents(cid);
        request.setAttribute("contents", contents);
        List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        return "admin/article_edit";
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
     * 文章发表
     *
     * @param contents
     * @param request
     * @return
     */
    @PostMapping(value = "/publish")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo publishArticle(ContentVo contents, HttpServletRequest request) {
        UserVo users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        if(StringUtils.isBlank(contents.getCategories())) {
            contents.setCategories("默认分类");
        }
        try {
            contentsService.publish(contents);
        } catch(Exception e) {
            String msg = "文章发布失败";
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
     * 文章更新
     *
     * @param contents
     * @param request
     * @return
     */
    @PostMapping(value = "/modify")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo modifyArticle(ContentVo contents, HttpServletRequest request) {
        UserVo users = this.user(request);
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        try {
            contentsService.updateArticle(contents);
        } catch(Exception e) {
            String msg = "文章编辑失败";
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
     * 删除文章
     *
     * @param cid
     * @param request
     * @return
     */
    @RequiresPermissions(value = ("/admin/article/delete"))
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam Integer cid, HttpServletRequest request) {
        try {
            if(cid != null) {
                contentsService.deleteById(cid);
                iRelationshipService.deleteById(cid);
            }
            logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid + "", request.getRemoteAddr(),
                    this.getUid(request));
        } catch(Exception e) {
            String msg = "文章删除失败";
            if(e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }
}
