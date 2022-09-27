package com.lwx.service;

import com.lwx.domain.Demo;
import com.lwx.mapper.DemoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DemoService {
    @Resource
    private DemoMapper demoMapper;

    public List<Demo> selectDemo()  {
        return demoMapper.selectByExample(null);
    }
}
