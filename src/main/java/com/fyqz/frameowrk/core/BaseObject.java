package com.fyqz.frameowrk.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @Title: BaseObject
 * @ProjectName: fyqz-platform
 * @Description: 重写toString
 * @author: zengchao
 * @date: 2019/5/21 10:21
 */
public abstract class BaseObject implements Serializable {
    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

    }
}
