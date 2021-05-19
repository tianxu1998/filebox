package top.xufilebox.mail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.xufilebox.common.dto.SendMailDTO;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;

import java.util.List;

@RestController
@RequestMapping("/mail")
@RefreshScope
public class MailController {
    @Autowired
    JavaMailSender mailSender;
    // 邮件发送者
    @Value("${spring.mail.username}")
    private String from;
    // 占位符
    @Value("${filebox.mail.placeholder}")
    private String placeholder;

    /**
     * 发送文本文件
     * @return
     */
    @RequestMapping(value = "/sendText", method = RequestMethod.POST)
    public Result sendTextMail(@RequestBody SendMailDTO dto) throws Exception {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(dto.getTo());
        msg.setSubject(dto.getTitle());
        msg.setText(placeholderReplace(dto.getTemplate(), dto.getKeyWords()));
        mailSender.send(msg);
        return Result.success(ResultCode.SUCCESS, "发送成功");
    }


    /**
     * 把模板替换成要发送的内容
     * @param template
     * @param keyWords
     * @return
     */
    private String placeholderReplace(String template, List<String> keyWords) throws Exception {
        if (keyWords.contains(placeholder)) {
            throw new Exception("关键字不得是占位符");
        }
        int cnt = 0;
        int idx;
        StringBuilder sb = new StringBuilder(template);
        while ((idx = sb.indexOf(placeholder)) != -1) {
            sb.replace(idx, idx+1, keyWords.get(cnt++));
        }
        return sb.toString();
    }
}
