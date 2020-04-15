package com.jashmore.springbootwebfluxjwtauthenticationexample

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

data class User(val email: String)

data class UserCredentials(val email: String, val password: String)

@RestController
@RequestMapping("/user")
class UserController {
    private val myself: User = User("email@example.com")

    @PutMapping
    fun signUp(@RequestBody user: UserCredentials): Mono<ResponseEntity<Void>> {
        return Mono.just(ResponseEntity.noContent().build())
    }

    @PostMapping
    fun login(@RequestBody user: UserCredentials): Mono<ResponseEntity<Void>> {
        return Mono.just(ResponseEntity.noContent().build())
    }

    @GetMapping
    fun getMyself(): Mono<ResponseEntity<User>> {
        return Mono.just(ResponseEntity.ok(myself))
    }
}