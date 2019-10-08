package com.bluersw.s.c.b.sl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationListener;

/**
 * 总线私聊事件监听
 */
public class PrivateChatListener implements ApplicationListener<PrivateChatRemoteApplicationEvent> {

	private static Log log = LogFactory.getLog(PrivateChatListener.class);

	public PrivateChatListener(){}

	@Override
	public void onApplicationEvent(PrivateChatRemoteApplicationEvent event){
		log.info(String.format("应用%s对应用%s悄悄的说：\"%s\"",
				event.getOriginService(),
				event.getDestinationService(),
				event.getMessage()));
	}
}
