package com.recipebook;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class RecipebookServiceApplicationTests {

	@Test
	void contextLoads(ApplicationContext context) {
		// This ensures that we've got the context, and it's been set up successfully
		Assertions.assertThat(context).isNotNull();
	}

}
