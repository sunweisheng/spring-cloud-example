package com.bluersw.s.c.s.a;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;

//ChatInput.class的输入通道不在这里绑定，监听到数据会找不到AClient类的引用。
//Input和Output通道定义的名字不能一样，否则程序启动会抛异常。
@EnableBinding({ChatOutput.class,ChatInput.class})
public class AClient {

	private static Logger logger = LoggerFactory.getLogger(AClient.class);

	@Autowired
	private ChatOutput chatOutput;

	//StreamListener自带了Json转对象的能力，收到B的消息打印并回复B一个新的消息。
	@StreamListener(ChatInput.INPUT)
	public void PrintInput(ChatMessage message) {

		logger.info(message.ShowMessage());

		ChatMessage replyMessage = new ChatMessage("ClientA","A To B Message.", new Date());

		chatOutput.output().send(MessageBuilder.withPayload(replyMessage).build());
	}
}
