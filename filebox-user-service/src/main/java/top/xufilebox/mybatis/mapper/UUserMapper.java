package top.xufilebox.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.xufilebox.mybatis.entity.UUser;

import java.util.List;

@Mapper
public interface UUserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UUser record);

    UUser selectByPrimaryKey(Integer userId);

    List<UUser> selectAll();

    int updateByPrimaryKey(UUser record);
}