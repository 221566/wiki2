package com.lwx.controller;

import com.lwx.req.EbookReq;
import com.lwx.resp.CommonResp;
import com.lwx.resp.EbookResp;
import com.lwx.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Resource
    private EbookService ebookService;

    @GetMapping("/selectEbook")
    public CommonResp selectEbook(EbookReq ebookReq){
        CommonResp<List<EbookResp>> resp = new CommonResp<>();
        List<EbookResp> list = ebookService.selectEbook(ebookReq);
        resp.setContent(list);
        return resp;
    }
}
