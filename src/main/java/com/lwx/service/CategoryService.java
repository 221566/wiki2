package com.lwx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwx.domain.Category;
import com.lwx.domain.CategoryExample;
import com.lwx.mapper.CategoryMapper;
import com.lwx.req.CategoryQueryReq;
import com.lwx.req.CategorySaveReq;
import com.lwx.resp.CategoryQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.util.CopyUtil;
import com.lwx.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);
    @Resource
    private CategoryMapper categoryMapper;

    @Autowired
    private SnowFlake snowFlake;

    public List<CategoryQueryResp> all()  {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

//        列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;
    }

    public PageResp<CategoryQueryResp> selectCategory(CategoryQueryReq categoryReq)  {
        CategoryExample categoryExample = new CategoryExample();
//        Criteria相当于where
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
//        if(!ObjectUtils.isEmpty(categoryReq.getName())) {
//            criteria.andNameLike("%" + categoryReq.getName() + "%");
//        }
        PageHelper.startPage(categoryReq.getPage(),categoryReq.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());
//        持久层返回list<Category>需要转成List<CategoryQueryResp>再返回控制层，需要用到循环
//        List<CategoryQueryResp> respList = new ArrayList<>();
//        for (Category category : categoryList) {
////            CategoryQueryResp categoryResp = new CategoryQueryResp();
////            BeanUtils.copyProperties(category,categoryResp);
//             对象复制
//            CategoryQueryResp copy = CopyUtil.copy(category, CategoryQueryResp.class);
//            respList.add(copy);
//        }
//        列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void save(CategorySaveReq req){
        Category category = CopyUtil.copy(req,Category.class);
        if (ObjectUtils.isEmpty(req.getId())){
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else {
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id){
        categoryMapper.deleteByPrimaryKey(id);
    }
}
