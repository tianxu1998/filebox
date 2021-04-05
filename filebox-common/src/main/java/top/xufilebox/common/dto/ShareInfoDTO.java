
package top.xufilebox.common.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShareInfoDTO {

    private List<ShareInfoItem> items;

    @Data
    public static class ShareInfoItem {
        /**
         * 分享url
         */
        private String shareUrl;

        /**
         * 此分享url文件个数
         */
        private Integer fileNum;

        /**
         * 分享日期
         */
        private LocalDateTime shareDate;

        /**
         * 分享有效期(单位：天
         */
        private Integer term;

        /**
         * 是否过期(1表示过期， 0表示未过期
         */
        private Integer expired;

        /**
         * 是否禁用（1表示禁用， 0表示未禁用
         */
        private Integer disable;

    }
}
