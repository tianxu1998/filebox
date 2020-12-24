package top.xufilebox.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xufilebox.common.mybatis.service.IShareService;
import top.xufilebox.common.mybatis.entity.Share;
import top.xufilebox.common.mybatis.mapper.ShareMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements IShareService {

}
