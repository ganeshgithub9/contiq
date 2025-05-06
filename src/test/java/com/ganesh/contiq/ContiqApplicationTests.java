package com.ganesh.contiq;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContiqApplicationTests {

		@Test
		void shouldTriggerRunMethod_whenCalledApplicationMainMethod() {
			try (MockedStatic<SpringApplication> mockSpringApp = Mockito.mockStatic(SpringApplication.class)) {
				mockSpringApp.when(() -> SpringApplication.run(ContiqApplication.class, new String[]{})).thenReturn(null);

				ContiqApplication.main(new String[]{});

				mockSpringApp.verify(() -> SpringApplication.run(ContiqApplication.class, new String[]{}), Mockito.times(1));
			}
		}
}
