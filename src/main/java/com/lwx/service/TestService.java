package com.lwx.service;

import com.lwx.domain.Test;
import com.lwx.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestService {
    @Resource
    private TestMapper testMapper;

    public List<Test> selectTest()  {
        return testMapper.selectTest();
    }
}
