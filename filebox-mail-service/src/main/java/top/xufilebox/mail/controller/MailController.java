package top.xufilebox.mail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mail")
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
     * @param to 邮件接收者
     * @param title 邮件标题
     * @param template 邮件模板
     * @param keyWords 替换关键字
     * @return
     */
    @RequestMapping("/sendText")
    public Result sendTextMail(String to, String title, String template, List<String> keyWords) throws Exception {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(title);
        msg.setText(placeholderReplace(template, keyWords));
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
