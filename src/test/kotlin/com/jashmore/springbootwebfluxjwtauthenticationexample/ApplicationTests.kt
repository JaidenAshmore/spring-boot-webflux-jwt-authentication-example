package com.jashmore.springbootwebfluxjwtauthenticationexample

import io.jsonwebtoken.Jwt
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

	@LocalServerPort
	var serverPort: Int? = null

	@Autowired
	lateinit var jwtSigner: JwtSigner

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

	@Test
	fun loginToUnknownAccountReturnsUnauthorised() {
		// arrange
		val webClient = WebClient.builder()
				.baseUrl("http://localhost:${serverPort}")
				.build()

		// act
		val response = webClient.post()
				.uri("/user/login")
				.bodyValue(UserCredentials("unknown@example.com", "pw"))
				.exchange()
				.block()

		// assert
		assertThat(response?.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
	}

	@Test
	fun loginToKnownAccountButIncorrectPasswordReturnsUnauthorised() {
		// arrange
		val webClient = WebClient.builder()
				.baseUrl("http://localhost:${serverPort}")
				.build()

		// act
		val response = webClient.post()
				.uri("/user/login")
				.bodyValue(UserCredentials("email@example.com", "incorrect password"))
				.exchange()
				.block()

		// assert
		assertThat(response?.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
	}

	@Test
	fun successfulLoginToKnownAccountReturnsNoContent() {
		// arrange
		val webClient = WebClient.builder()
				.baseUrl("http://localhost:${serverPort}")
				.build()

		// act
		val response = webClient.post()
				.uri("/user/login")
				.bodyValue(UserCredentials("email@example.com", "pw"))
				.exchange()
				.block()

		// assert
		assertThat(response?.statusCode()).isEqualTo(HttpStatus.NO_CONTENT)
	}

	@Test
	fun `successful login to known account should return Jwt cookie`() {
		// arrange
		val webClient = WebClient.builder()
				.baseUrl("http://localhost:${serverPort}")
				.build()

		// act
		val response = webClient.post()
				.uri("/user/login")
				.bodyValue(UserCredentials("email@example.com", "pw"))
				.exchange()
				.block()
		val jwtToken = response?.cookies()?.get("X-Auth")?.get(0)?.value ?: throw RuntimeException("No JWT Token in response")

		// assert
		assertThat(jwtSigner.validateJwt(jwtToken)).isNotNull
	}

	@Test
	fun signingUpToAccountAllowsForSubsequentLogin() {
		// arrange
		val webClient = WebClient.builder()
				.baseUrl("http://localhost:${serverPort}")
				.build()

		// act
		val signupResponse = webClient.put()
				.uri("/user/signup")
				.bodyValue(UserCredentials("new@example.com", "pw"))
				.exchange()
				.block()
		assertThat(signupResponse?.statusCode()).isEqualTo(HttpStatus.NO_CONTENT)
		val loginResponse = webClient.post()
				.uri("/user/login")
				.bodyValue(UserCredentials("new@example.com", "pw"))
				.exchange()
				.block()

		// assert
		assertThat(loginResponse?.statusCode()).isEqualTo(HttpStatus.NO_CONTENT)
	}
}
