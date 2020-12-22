package top.xufilebox.auth.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xufilebox.auth.service.IDirectoryService;
import top.xufilebox.common.mybatis.entity.Directory;
import top.xufilebox.common.mybatis.mapper.DirectoryMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Service
@DS("slave_1")
public class DirectoryServiceImpl extends ServiceImpl<DirectoryMapper, Directory> implements IDirectoryService {

}
