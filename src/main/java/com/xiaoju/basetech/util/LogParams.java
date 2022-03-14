package com.xiaoju.basetech.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author sparkle
 * @date 2022/3/8
 */
@Data
@AllArgsConstructor
public class LogParams {
    private String revisionRangeSince;
    private String revisionRangeUntil;
}
