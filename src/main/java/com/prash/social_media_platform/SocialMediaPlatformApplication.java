package com.prash.social_media_platform;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SocialMediaPlatformApplication {

	public static void main(String[] args) {

		SpringApplication.run(SocialMediaPlatformApplication.class, args);
	}
}
