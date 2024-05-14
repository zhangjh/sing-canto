package me.zhangjh.sing.canto.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author njhxzhangjihong@126.com
 * @date 17:58 2024/5/14
 * @Description
 */
@Data
public class BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    @TableLogic
    private Integer isDeleted;
}
