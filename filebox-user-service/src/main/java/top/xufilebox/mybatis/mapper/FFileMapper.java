package top.xufilebox.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.xufilebox.mybatis.entity.FFile;

import java.util.List;

@Mapper
public interface FFileMapper {
    int deleteByPrimaryKey(Integer fileId);

    int insert(FFile record);

    FFile selectByPrimaryKey(Integer fileId);

    List<FFile> selectAll();

    int updateByPrimaryKey(FFile record);
}