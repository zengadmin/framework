package com.fyqz.frameowrk.core;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author :zengchao
 * @Description: 公共基础model
 * @date : 2019/5/20 15:57
 */

@Data
@SuppressWarnings("serial")
public abstract class BaseModel extends Model implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

    }
}
