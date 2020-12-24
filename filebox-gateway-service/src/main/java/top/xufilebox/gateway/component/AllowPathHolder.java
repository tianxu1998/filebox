package top.xufilebox.gateway.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-23 16:04
 **/
@Component
public class AllowPathHolder {
    @Value("#{'${filebox.allowPaths}'.split(',')}")
    private List<String> allowPaths;

    public List<String> getAllowPaths() {
        return allowPaths;
    }
}
