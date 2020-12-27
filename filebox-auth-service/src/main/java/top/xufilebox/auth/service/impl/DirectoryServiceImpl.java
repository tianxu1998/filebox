package top.xufilebox.auth.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xufilebox.common.mybatis.entity.Directory;
import top.xufilebox.common.mybatis.mapper.DirectoryMapper;
import top.xufilebox.common.mybatis.service.IDirectoryService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Service
public class DirectoryServiceImpl extends ServiceImpl<DirectoryMapper, Directory> implements IDirectoryService {

}
