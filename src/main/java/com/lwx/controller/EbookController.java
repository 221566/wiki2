package com.lwx.controller;

import com.lwx.req.EbookQueryReq;
import com.lwx.req.EbookSaveReq;
import com.lwx.resp.CommonResp;
import com.lwx.resp.EbookQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Resource
    private EbookService ebookService;

    @GetMapping("/selectEbook")
    public CommonResp selectEbook(EbookQueryReq ebookReq){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.selectEbook(ebookReq);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    /**
     *1、 @RequestBody对应的就是json方式的（post）提交就像我们写的ebook
     * 这个是用content-ttype是appliction/json方式的提交，我们需要用到这个注解才能接收到
     *
     * 2、是用www-form方式传参的话就不需要任何注解，直接写变量就能接收到了
     *
     * 3、axios的post提交是用appliction/json方式提交的所以要使用@RequestBody注解才能接收到数据
     */
    public CommonResp save(@RequestBody EbookSaveReq ebookSaveReq){
        CommonResp resp = new CommonResp<>();
        ebookService.save(ebookSaveReq);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }
}
