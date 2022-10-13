package com.lwx.req;

import lombok.Data;

@Data
public class EbookQueryReq extends PageReq{
    private Long id;

    private String name;

    private Long categoryIdd2;
}