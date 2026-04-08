package com.alibalci.isgmobil.isg.isgbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//Component scan başlatır
//
//Auto configuration yapar
//
//Spring context’i başlatır
public class IsgmobilApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsgmobilApplication.class, args);
	}

	}
