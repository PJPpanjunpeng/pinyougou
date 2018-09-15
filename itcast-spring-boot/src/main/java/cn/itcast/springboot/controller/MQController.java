package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/mq")
@RestController
public class MQController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("/send")
    public String sendMapMsg() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 123L);
        map.put("name", "传智播客");
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue", map);

        return "发送消息完成";
    }


    /**
     * 发送一个手机短信消息到 MQ 的队列
     * @return
     */
    @GetMapping("/sendSms")
    public String sendSmsMsg() {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", "13827530087");
        map.put("signName", "品优购");
        map.put("templateCode", "SMS_144942597");
        map.put("templateParam", "{\"code\":\"123456\"}");
        jmsMessagingTemplate.convertAndSend("itcast_sms_queue", map);

        return "发送 sms 消息完成。 ";
    }
}
