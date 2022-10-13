package com.lwx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwx.domain.Ebook;
import com.lwx.domain.EbookExample;
import com.lwx.mapper.EbookMapper;
import com.lwx.req.EbookQueryReq;
import com.lwx.req.EbookSaveReq;
import com.lwx.resp.EbookQueryResp;
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
public class EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);
    @Resource
    private EbookMapper ebookMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> selectEbook(EbookQueryReq ebookReq)  {
        EbookExample ebookExample = new EbookExample();
//        Criteria相当于where
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(ebookReq.getName())) {
            criteria.andNameLike("%" + ebookReq.getName() + "%");
        }

        if(!ObjectUtils.isEmpty(ebookReq.getCategoryIdd2())) {
            criteria.andCategory2IdEqualTo( ebookReq.getCategoryIdd2());
        }
        PageHelper.startPage(ebookReq.getPage(),ebookReq.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());
//        持久层返回list<Ebook>需要转成List<EbookQueryResp>再返回控制层，需要用到循环
//        List<EbookQueryResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
////            EbookQueryResp ebookResp = new EbookQueryResp();
////            BeanUtils.copyProperties(ebook,ebookResp);
//             对象复制
//            EbookQueryResp copy = CopyUtil.copy(ebook, EbookQueryResp.class);
//            respList.add(copy);
//        }
//        列表复制
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        PageResp<EbookQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void save(EbookSaveReq req){
        Ebook ebook = CopyUtil.copy(req,Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())){
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        }else {
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(Long id){
        ebookMapper.deleteByPrimaryKey(id);
    }
}
