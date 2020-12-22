package top.xufilebox.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xufilebox.auth.service.IFileService;
import top.xufilebox.common.mybatis.entity.File;
import top.xufilebox.common.mybatis.mapper.FileMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

}
