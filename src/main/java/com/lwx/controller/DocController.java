package com.lwx.controller;

import com.lwx.req.DocQueryReq;
import com.lwx.req.DocSaveReq;
import com.lwx.resp.CommonResp;
import com.lwx.resp.DocQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {
    @Resource
    private DocService docService;

    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/selectDoc")
    public CommonResp selectDoc(@Valid DocQueryReq docReq){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.selectDoc(docReq);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    /**
     *1、 @RequestBody对应的就是json方式的（post）提交就像我们写的doc
     * 这个是用content-ttype是appliction/json方式的提交，我们需要用到这个注解才能接收到
     *
     * 2、是用www-form方式传参的话就不需要任何注解，直接写变量就能接收到了
     *
     * 3、axios的post提交是用appliction/json方式提交的所以要使用@RequestBody注解才能接收到数据
     */
    public CommonResp save(@Valid @RequestBody DocSaveReq docSaveReq){
        CommonResp resp = new CommonResp<>();
        docService.save(docSaveReq);
        return resp;
    }

    @DeleteMapping("/delete/{idStr}")
    public CommonResp delete(@PathVariable String idStr){
        CommonResp resp = new CommonResp<>();
        //把string转成一个集合，先转成数组在转集合,idStr.split(",")根据逗号转成数组
        List<String> list = Arrays.asList(idStr.split(","));
        docService.delete(list);
        return resp;
    }

    @GetMapping("/findContent/{id}")
    public CommonResp findContent(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }
}
