package com.xiaoju.basetech.entity;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: charlynegaoweiwei
 * @time: 2020/4/26 7:52 PM
 */
@Data
public class EnvCoverRequest extends CoverBaseRequest{

    private List<String> address;
    private int port;
    //    需要merge的uuid
    private List<String> toMergeId;

}