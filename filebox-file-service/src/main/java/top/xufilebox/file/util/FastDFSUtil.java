package top.xufilebox.file.util;

import com.baomidou.mybatisplus.extension.api.R;
import com.github.tobato.fastdfs.domain.fdfs.GroupState;
import com.github.tobato.fastdfs.service.TrackerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-07 09:07
 **/
@Component
public class FastDFSUtil {
    @Autowired
    TrackerClient trackerClient;
    Random rand = new Random();
    public String randomGroupName() {
        List<GroupState> groupStates = trackerClient.listGroups();
        int index = rand.nextInt(groupStates.size());
        return groupStates.get(index).getGroupName();
    }
}
