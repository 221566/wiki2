package com.lwx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwx.domain.Doc;
import com.lwx.domain.DocExample;
import com.lwx.mapper.DocMapper;
import com.lwx.req.DocQueryReq;
import com.lwx.req.DocSaveReq;
import com.lwx.resp.DocQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.util.CopyUtil;
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

    @Autowired
    private SnowFlake snowFlake;

    public List<DocQueryResp> all()  {
        DocExample docExample = new DocExample();
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
        if (ObjectUtils.isEmpty(req.getId())){
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        }else {
            docMapper.updateByPrimaryKey(doc);
        }
    }

    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }
}
