package com.xiaoju.basetech.entity;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: gaoweiwei_v
 * @time: 2019/11/27 1:57 PM
 */
@Data
public class DeployInfoEntity {
    private String uuid;
    private String address;
    private int port;
    private String codePath;
    private String childModules;
}