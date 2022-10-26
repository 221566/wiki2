package com.lwx.controller;

import com.lwx.aspect.LogAspect;
import com.lwx.req.EbookQueryReq;
import com.lwx.req.EbookSaveReq;
import com.lwx.resp.CommonResp;
import com.lwx.resp.EbookQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.service.EbookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);


    @Resource
    private EbookService ebookService;

    @GetMapping("/selectEbook")
    public CommonResp selectEbook(@Valid EbookQueryReq ebookReq){
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
    public CommonResp save(@Valid @RequestBody EbookSaveReq ebookSaveReq){
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

    @RequestMapping("/upload/avatar")
    public CommonResp upload(@RequestParam MultipartFile avatar) throws IOException {
        LOG.info("上传文件开始：{}", avatar);
        LOG.info("文件名：{}", avatar.getOriginalFilename());
        LOG.info("文件大小：{}", avatar.getSize());

        // 保存文件到本地
        String fileName = avatar.getOriginalFilename();
        String fullPath = "D:/file/wiki/" + fileName;
        File dest = new File(fullPath);
        avatar.transferTo(dest);
        LOG.info(dest.getAbsolutePath());

        return new CommonResp();
    }
}
