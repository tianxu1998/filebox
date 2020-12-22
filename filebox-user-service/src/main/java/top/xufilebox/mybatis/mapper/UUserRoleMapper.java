package top.xufilebox.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.xufilebox.mybatis.entity.UUserRole;

import java.util.List;

@Mapper
public interface UUserRoleMapper {
    int insert(UUserRole record);

    List<UUserRole> selectAll();
}