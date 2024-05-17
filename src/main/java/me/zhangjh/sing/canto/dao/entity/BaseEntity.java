package me.zhangjh.sing.canto.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author njhxzhangjihong@126.com
 * @date 17:58 2024/5/14
 * @Description
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -4252036495460735192L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    @TableLogic
    private Integer isDeleted;
}
