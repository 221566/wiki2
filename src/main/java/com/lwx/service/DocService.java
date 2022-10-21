package com.lwx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwx.domain.Content;
import com.lwx.domain.Doc;
import com.lwx.domain.DocExample;
import com.lwx.exception.BusinessException;
import com.lwx.exception.BusinessExceptionCode;
import com.lwx.mapper.ContentMapper;
import com.lwx.mapper.DocMapper;
import com.lwx.req.DocQueryReq;
import com.lwx.req.DocSaveReq;
import com.lwx.resp.DocQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.util.CopyUtil;
import com.lwx.util.RedisUtil;
import com.lwx.util.RequestContext;
import com.lwx.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);
    @Resource
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private SnowFlake snowFlake;

//    @Autowired
//    private WebSocketServer webSocketServer;

    @Resource
    public WsService wsService;

    public List<DocQueryResp> all(Long ebookId)  {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

//        列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> selectDoc(DocQueryReq docReq)  {
        DocExample docExample = new DocExample();
//        Criteria相当于where
        DocExample.Criteria criteria = docExample.createCriteria();
//        if(!ObjectUtils.isEmpty(docReq.getName())) {
//            criteria.andNameLike("%" + docReq.getName() + "%");
//        }
        PageHelper.startPage(docReq.getPage(),docReq.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());
//        持久层返回list<Doc>需要转成List<DocQueryResp>再返回控制层，需要用到循环
//        List<DocQueryResp> respList = new ArrayList<>();
//        for (Doc doc : docList) {
////            DocQueryResp docResp = new DocQueryResp();
////            BeanUtils.copyProperties(doc,docResp);
//             对象复制
//            DocQueryResp copy = CopyUtil.copy(doc, DocQueryResp.class);
//            respList.add(copy);
//        }
//        列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void save(DocSaveReq req){
        Doc doc = CopyUtil.copy(req,Doc.class);
        Content content = CopyUtil.copy(req,Content.class);
        if (ObjectUtils.isEmpty(req.getId())){
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        }else {
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0){
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids){
        DocExample docExample = new DocExample();
        //创建一个条件
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id){
        Content content = contentMapper.selectByPrimaryKey(id);
        //文档阅读数加一
        docMapper.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }
    }

    /**
     * 点赞
     */
    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 5000)) {
            docMapper.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        //推送消息
        Doc doc = docMapper.selectByPrimaryKey(id);
        wsService.sendInfo("【"+doc.getName()+"】被点赞");
    }

    public void updateEbookInfo(){
        docMapper.updateEbookInfo();
    }
}
