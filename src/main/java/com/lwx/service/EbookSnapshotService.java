package com.lwx.service;


import com.lwx.mapper.EbookSnapshotMapper;
import com.lwx.resp.StatisticResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotService {

    @Resource
    private EbookSnapshotMapper EbookSnapshotMapper;

    public void genSnapshot() {
        EbookSnapshotMapper.genSnapshot();
    }

    /**
     * 获取首页数值数据：总阅读数、总点赞数、今日阅读数、今日点赞数、今日预计阅读数、今日预计阅读增长
     */
    public List<StatisticResp> getStatistic() {
        return EbookSnapshotMapper.getStatistic();
    }

    /**
     * 30天数值统计
     */
//    public List<StatisticResp> get30Statistic() {
//        return ebookSnapshotMapperCust.get30Statistic();
//    }

}
