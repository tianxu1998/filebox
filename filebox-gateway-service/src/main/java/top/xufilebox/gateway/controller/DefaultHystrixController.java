package top.xufilebox.gateway.controller;

/**
 * @description:
 * @author: alextian
 * @create: 2021-05-24 13:02
 **/

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认降级处理
 */
@RestController
public class DefaultHystrixController {

	/**
	 * 降级操作
	 * @return
	 */
	@RequestMapping("/defaultfallback")
	public Result defaultfallback(){
		return Result.failed(ResultCode.FAILED);
	}
}
