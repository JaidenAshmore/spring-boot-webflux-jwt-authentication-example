package com.jashmore.springbootwebfluxjwtauthenticationexample

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

	@LocalServerPort
	var serverPort: Int? = null

	@Test
	fun whenLoggedInUserCanGetOwnDetails() {
		// arrange
		val webClient = WebClient.builder()
				.baseUrl("http://localhost:${serverPort}")
				.build()

		// act
		val response = webClient.get()
				.uri("/user")
				.exchange()
				.block()

		// assert
		assertThat(response?.statusCode()).isEqualTo(HttpStatus.OK)
		assertThat(response?.bodyToFlux(User::class.java)?.blockFirst()).isEqualTo(User("email@example.com"))
	}
}
