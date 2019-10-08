package com.bluersw.s.c.b.a;

import com.bluersw.s.c.b.sl.PrivateChatListener;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(PrivateChatListener.class)
public class ListenerConfiguration  {
	@Bean
	public PrivateChatListener privateChatListener(){
		return new PrivateChatListener();
	}
}
