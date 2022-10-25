package com.lwx.resp;

import lombok.Data;

import java.util.Date;

@Data
public class StatisticResp {
    private Date data;

    private int viewCount;

    private int voteCount;

    private int viewIncrease;

    private int voteIncrease;


}
