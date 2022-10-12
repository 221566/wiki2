package com.lwx.controller;

import com.lwx.req.CategoryQueryReq;
import com.lwx.req.CategorySaveReq;
import com.lwx.resp.CommonResp;
import com.lwx.resp.CategoryQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @GetMapping("/selectCategory")
    public CommonResp selectCategory(@Valid CategoryQueryReq categoryReq){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.selectCategory(categoryReq);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    /**
     *1、 @RequestBody对应的就是json方式的（post）提交就像我们写的category
     * 这个是用content-ttype是appliction/json方式的提交，我们需要用到这个注解才能接收到
     *
     * 2、是用www-form方式传参的话就不需要任何注解，直接写变量就能接收到了
     *
     * 3、axios的post提交是用appliction/json方式提交的所以要使用@RequestBody注解才能接收到数据
     */
    public CommonResp save(@Valid @RequestBody CategorySaveReq categorySaveReq){
        CommonResp resp = new CommonResp<>();
        categoryService.save(categorySaveReq);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
