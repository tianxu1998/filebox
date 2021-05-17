package top.xufilebox.auth.call;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.xufilebox.common.dto.SendMailDTO;
import top.xufilebox.common.result.Result;

@FeignClient(value = "mail-service")
public interface MailServiceFeign {
    /**
     * 发送文本文件
     * @return
     */
    @RequestMapping(value = "/mail/sendText", method = RequestMethod.POST)
    Result sendTextMail(@RequestBody SendMailDTO sendMailDTO);
}
