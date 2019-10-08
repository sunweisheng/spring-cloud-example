package com.bluersw.s.c.b.a;

import com.bluersw.s.c.b.sl.PrivateChatRemoteApplicationEvent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.bluersw")
public class SpringCloudBusAApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudBusAApplication.class, args);
	}

}
