package com.lwx.mapper;

import com.lwx.domain.EbookSnapshot;
import com.lwx.domain.EbookSnapshotExample;
import com.lwx.resp.StatisticResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EbookSnapshotMapper {
    long countByExample(EbookSnapshotExample example);

    int deleteByExample(EbookSnapshotExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EbookSnapshot record);

    int insertSelective(EbookSnapshot record);

    List<EbookSnapshot> selectByExample(EbookSnapshotExample example);

    EbookSnapshot selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EbookSnapshot record, @Param("example") EbookSnapshotExample example);

    int updateByExample(@Param("record") EbookSnapshot record, @Param("example") EbookSnapshotExample example);

    int updateByPrimaryKeySelective(EbookSnapshot record);

    int updateByPrimaryKey(EbookSnapshot record);

    public void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}