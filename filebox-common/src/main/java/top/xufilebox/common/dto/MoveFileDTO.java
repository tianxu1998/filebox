package top.xufilebox.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveFileDTO {
    // 转存到哪个文件夹
    private Integer toDir;

    // 要转存的文件Id
    private List<Integer> fileIds;
}
