package top.xufilebox.mybatis.utils;

import java.util.Random;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-07 09:23
 **/
public class RandomUtil {
    /**
     * 返回一个在start和end之间的随机整数，包含start和end
     * @param start
     * @param end
     * @return
     */
    private static final Random rand = new Random();
    public static int random(int start, int end) {
        return rand.nextInt(end - start + 1) + start;
    }
}
