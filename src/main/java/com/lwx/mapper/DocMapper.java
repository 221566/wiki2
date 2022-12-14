package com.lwx.mapper;

import com.lwx.domain.Doc;
import com.lwx.domain.DocExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocMapper {
    long countByExample(DocExample example);

    int deleteByExample(DocExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Doc record);

    int insertSelective(Doc record);

    List<Doc> selectByExample(DocExample example);

    Doc selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Doc record, @Param("example") DocExample example);

    int updateByExample(@Param("record") Doc record, @Param("example") DocExample example);

    int updateByPrimaryKeySelective(Doc record);

    int updateByPrimaryKey(Doc record);

    void increaseViewCount(@Param("id") Long id);

    void increaseVoteCount(@Param("id") Long id);

    void updateEbookInfo();
}