package com.alibalci.isgmobil;

import com.alibalci.isgmobil.isg.isgbackend.IsgmobilApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = IsgmobilApplication.class)
@ActiveProfiles("test")
class IsgmobilApplicationTests {

	@Test
	void contextLoads() {
	}

}
