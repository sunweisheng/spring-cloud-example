package com.bluersw.s.c.s.b;

import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.support.GenericMessage;

@EnableBinding(ChatProcessor.class)
public class BClient {

	private static Logger logger = LoggerFactory.getLogger(BClient.class);

	//@ServiceActivator没有Json转对象的能力需要借助@Transformer注解
	@ServiceActivator(inputChannel=ChatProcessor.INPUT)
	public void PrintInput(ChatMessage message) {

		logger.info(message.ShowMessage());
	}

	@Transformer(inputChannel = ChatProcessor.INPUT,outputChannel = ChatProcessor.INPUT)
	public ChatMessage transform(String message) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(message,ChatMessage.class);
	}

	//没秒发出一个消息给A
	@Bean
	@InboundChannelAdapter(value = ChatProcessor.OUTPUT,poller = @Poller(fixedDelay="1000"))
	public GenericMessage<ChatMessage> SendChatMessage(){
		ChatMessage message = new ChatMessage("ClientB","B To A Message.", new Date());
		GenericMessage<ChatMessage> gm = new GenericMessage<>(message);

		return gm;
	}
}
