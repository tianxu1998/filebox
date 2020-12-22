package top.xufilebox.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.xufilebox.mybatis.entity.FDirectory;

import java.util.List;

@Mapper
public interface FDirectoryMapper {
    int deleteByPrimaryKey(Integer dirId);

    int insert(FDirectory record);

    FDirectory selectByPrimaryKey(Integer dirId);

    List<FDirectory> selectAll();

    int updateByPrimaryKey(FDirectory record);
}