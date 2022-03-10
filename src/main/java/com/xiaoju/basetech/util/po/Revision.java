package com.xiaoju.basetech.util.po;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author sparkle
 * @date 2022/3/8
 */
@Data
@AllArgsConstructor
public class Revision {
    private String commitId;
    private String userName;
    private String authorIdent;
    private Date commitTime;
    private String fullMessage;
}
