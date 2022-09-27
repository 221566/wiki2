package com.lwx.service;

import com.lwx.domain.Ebook;
import com.lwx.mapper.EbookMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<Ebook> selectEbook()  {
        return ebookMapper.selectByExample(null);
    }
}
