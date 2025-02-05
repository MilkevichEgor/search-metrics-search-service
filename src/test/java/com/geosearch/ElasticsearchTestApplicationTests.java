package com.geosearch;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class ElasticsearchTestApplicationTests {

	@Test
	void contextLoads() throws JOSEException {
	  OctetSequenceKeyGenerator octetSequenceKeyGenerator = new OctetSequenceKeyGenerator(256);
	  OctetSequenceKeyGenerator octetSequenceKeyGenerator2 = new OctetSequenceKeyGenerator(192);
	  System.out.println(UUID.randomUUID());
	  System.out.println(octetSequenceKeyGenerator.generate());
	  System.out.println(octetSequenceKeyGenerator2.generate());

	}

}
