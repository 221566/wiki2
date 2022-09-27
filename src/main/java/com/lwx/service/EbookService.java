package com.lwx.service;

import com.lwx.domain.Ebook;
import com.lwx.domain.EbookExample;
import com.lwx.mapper.EbookMapper;
import com.lwx.req.EbookReq;
import com.lwx.resp.EbookResp;
import com.lwx.util.CopyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> selectEbook(EbookReq ebookReq)  {
        EbookExample ebookExample = new EbookExample();
//        Criteria相当于where
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%"+ebookReq.getName()+"%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
//        持久层返回list<Ebook>需要转成List<EbookResp>再返回控制层，需要用到循环
//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
////            EbookResp ebookResp = new EbookResp();
////            BeanUtils.copyProperties(ebook,ebookResp);
//             对象复制
//            EbookResp copy = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(copy);
//        }
//        列表复制
        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);
        return list;
    }
}
