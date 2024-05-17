package me.zhangjh.sing.canto.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>
 * 跟练记录
 * </p>
 *
 * @author baomidou
 * @since 2024-05-16
 */
@Data
@TableName("tbl_practiced")
public class TblPracticed extends BaseEntity {

    private Long songId;

    private String user;

}
