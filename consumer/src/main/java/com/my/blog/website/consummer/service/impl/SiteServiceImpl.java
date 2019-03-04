package com.my.blog.website.consummer.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageHelper;
import com.my.blog.website.AttachService;
import com.my.blog.website.entity.AttachVo;
import com.my.blog.website.consummer.constant.WebConst;
import com.my.blog.website.consummer.controller.admin.AttachController;
import com.my.blog.website.consummer.dao.CommentVoMapper;
import com.my.blog.website.consummer.dao.MetaVoMapper;
import com.my.blog.website.consummer.dto.Types;
import com.my.blog.website.consummer.exception.TipException;
import com.my.blog.website.consummer.modal.Bo.BackResponseBo;
import com.my.blog.website.consummer.modal.Bo.StatisticsBo;
import com.my.blog.website.consummer.modal.Vo.ContentVo;
import com.my.blog.website.consummer.modal.Vo.MetaVo;
import com.my.blog.website.consummer.utils.TaleUtils;
import com.my.blog.website.consummer.utils.ZipUtils;
import com.my.blog.website.consummer.utils.backup.Backup;
import com.my.blog.website.consummer.dao.ContentVoMapper;
import com.my.blog.website.consummer.dto.MetaDto;
import com.my.blog.website.consummer.modal.Bo.ArchiveBo;
import com.my.blog.website.consummer.modal.Vo.CommentVo;
import com.my.blog.website.consummer.service.ISiteService;
import com.my.blog.website.consummer.utils.DateKit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;


/**
 * Created by BlueT on 2017/3/7.
 */
@Service
public class SiteServiceImpl implements ISiteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    @Resource
    private CommentVoMapper commentDao;

    @Resource
    private ContentVoMapper contentDao;

    @Reference(check = false)
    private AttachService attachDao;

    @Resource
    private MetaVoMapper metaDao;

    @Override
    public List<CommentVo> recentComments(int limit) {
        LOGGER.debug("Enter recentComments method:limit={}", limit);
        if(limit < 0 || limit > 10) {
            limit = 10;
        }
        EntityWrapper ew = new EntityWrapper();
        ew.orderBy("created", false);
        PageHelper.startPage(1, limit);
        List<CommentVo> byPage = commentDao.selectList(ew);
        LOGGER.debug("Exit recentComments method");
        return byPage;
    }

    @Override
    public List<ContentVo> recentContents(int limit) {
        LOGGER.debug("Enter recentContents method");
        if(limit < 0 || limit > 10) {
            limit = 10;
        }
        EntityWrapper ew = new EntityWrapper();
        ew.where("1=1");
        ew.andNew("status={0}", Types.PUBLISH.getType());
        ew.and("type={0}", Types.ARTICLE.getType());
        ew.orderBy("created", false);
        PageHelper.startPage(1, limit);
        List<ContentVo> list = contentDao.selectList(ew);
        LOGGER.debug("Exit recentContents method");
        return list;
    }

    @Override
    public BackResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception {
        BackResponseBo backResponse = new BackResponseBo();
        if(bk_type.equals("attach")) {
            if(StringUtils.isBlank(bk_path)) {
                throw new TipException("请输入备份文件存储路径");
            }
            if(!(new File(bk_path)).isDirectory()) {
                throw new TipException("请输入一个存在的目录");
            }
            String bkAttachDir = AttachController.CLASSPATH + "upload";
            String bkThemesDir = AttachController.CLASSPATH + "templates/themes";

            String fname = DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".zip";

            String attachPath = bk_path + "/" + "attachs_" + fname;
            String themesPath = bk_path + "/" + "themes_" + fname;

            ZipUtils.zipFolder(bkAttachDir, attachPath);
            ZipUtils.zipFolder(bkThemesDir, themesPath);

            backResponse.setAttachPath(attachPath);
            backResponse.setThemePath(themesPath);
        }
        if(bk_type.equals("db")) {

            String bkAttachDir = AttachController.CLASSPATH + "upload/";
            if(!(new File(bkAttachDir)).isDirectory()) {
                File file = new File(bkAttachDir);
                if(!file.exists()) {
                    file.mkdirs();
                }
            }
            String sqlFileName =
                    "tale_" + DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".sql";
            String zipFile = sqlFileName.replace(".sql", ".zip");

            Backup backup = new Backup(TaleUtils.getNewDataSource().getConnection());
            String sqlContent = backup.execute();

            File sqlFile = new File(bkAttachDir + sqlFileName);
            write(sqlContent, sqlFile, Charset.forName("UTF-8"));

            String zip = bkAttachDir + zipFile;
            ZipUtils.zipFile(sqlFile.getPath(), zip);

            if(!sqlFile.exists()) {
                throw new TipException("数据库备份失败");
            }
            sqlFile.delete();

            backResponse.setSqlPath(zipFile);

            // 10秒后删除备份文件
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    new File(zip).delete();
                }
            }, 10 * 1000);
        }
        return backResponse;
    }

    @Override
    public CommentVo getComment(Integer coid) {
        if(null != coid) {
            return commentDao.selectById(coid);
        }
        return null;
    }

    /**
     * 仪表盘中的数量
     *
     * @return
     */
    @Override
    public StatisticsBo getStatistics() {
        LOGGER.debug("Enter getStatistics method");
        StatisticsBo statistics = new StatisticsBo();
        EntityWrapper ew = new EntityWrapper();
        ew.where("1=1");
        ew.andNew("type", Types.ARTICLE.getType());
        ew.and("status", Types.PUBLISH.getType());
        Long articles = contentDao.selectCount(ew).longValue();
        Long comments = commentDao.selectCount(new EntityWrapper<CommentVo>()).longValue();
        Long attachs = new Integer(attachDao.selectCount(new EntityWrapper<AttachVo>())).longValue();
        Long links = metaDao.selectCount(new EntityWrapper<MetaVo>()
                .and("type", Types.LINK.getType())).longValue();
        statistics.setArticles(articles);
        statistics.setComments(comments);
        statistics.setAttachs(attachs);
        statistics.setLinks(links);
        LOGGER.debug("Exit getStatistics method");
        return statistics;
    }

    /**
     * 归档的列表(按月分组)
     *
     * @return
     */
    @Override
    public List<ArchiveBo> getArchives() {
        LOGGER.debug("Enter getArchives method");
        List<ArchiveBo> archives = contentDao.findReturnArchiveBo();
        if(null != archives) {
            archives.forEach(archive -> {
                String date = archive.getDate();
                Date sd = DateKit.dateFormat(date, "yyyy年MM月");
                int start = DateKit.getUnixTimeByDate(sd);
                int end = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd, 1)) - 1;
                EntityWrapper<ContentVo> ew = new EntityWrapper();
                ew.eq("type", Types.ARTICLE.getType())
                        .and("status={0}", Types.PUBLISH.getType())
                        .between("created", start, end)
                        .orderBy("created", false);
                List<ContentVo> contentss = contentDao.selectList(ew);
                archive.setArticles(contentss);
            });
        }
        LOGGER.debug("Exit getArchives method");
        return archives;
    }

    @Override
    public List<MetaDto> metas(String type, String orderBy, int limit) {
        LOGGER.debug("Enter metas method:type={},order={},limit={}", type, orderBy, limit);
        List<MetaDto> retList = null;
        if(StringUtils.isNotBlank(type)) {
            if(StringUtils.isBlank(orderBy)) {
                orderBy = "count desc, a.mid desc";
            }
            if(limit < 1 || limit > WebConst.MAX_POSTS) {
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderBy);
            paraMap.put("limit", limit);
            retList = metaDao.selectFromSql(paraMap);
        }
        LOGGER.debug("Exit metas method");
        return retList;
    }


    private void write(String data, File file, Charset charset) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes(charset));
        } catch(IOException var8) {
            throw new IllegalStateException(var8);
        } finally {
            if(null != os) {
                try {
                    os.close();
                } catch(IOException var2) {
                    var2.printStackTrace();
                }
            }
        }

    }

}
