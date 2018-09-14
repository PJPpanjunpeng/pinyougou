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
}
