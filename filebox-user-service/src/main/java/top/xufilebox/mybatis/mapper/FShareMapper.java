package top.xufilebox.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.xufilebox.mybatis.entity.FShare;

import java.util.List;

@Mapper
public interface FShareMapper {
    int deleteByPrimaryKey(Integer shareId);

    int insert(FShare record);

    FShare selectByPrimaryKey(Integer shareId);

    List<FShare> selectAll();

    int updateByPrimaryKey(FShare record);
}