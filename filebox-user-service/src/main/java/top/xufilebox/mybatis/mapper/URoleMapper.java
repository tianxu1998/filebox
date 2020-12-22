package top.xufilebox.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.xufilebox.mybatis.entity.URole;

import java.util.List;

@Mapper
public interface URoleMapper {
    int deleteByPrimaryKey(Integer roleId);

    int insert(URole record);

    URole selectByPrimaryKey(Integer roleId);

    List<URole> selectAll();

    int updateByPrimaryKey(URole record);
}