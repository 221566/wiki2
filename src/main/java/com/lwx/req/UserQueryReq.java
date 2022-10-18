package com.lwx.req;

import lombok.Data;

@Data
public class UserQueryReq extends PageReq{
    private String loginName;
}
