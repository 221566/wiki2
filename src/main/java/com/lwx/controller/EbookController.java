package com.lwx.controller;

import com.lwx.domain.Ebook;
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
    public List<Ebook> selectEbook(){
        return ebookService.selectEbook();
    }
}
