package com.manish.authservice.controller;

import com.manish.authservice.domain.User;
import com.manish.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2SecurityExpressionMethods;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;


	// use not found yet.
	@GetMapping(path = "/current")
	public Principal getUser(Principal principal) {
		return principal;
	}

	@PreAuthorize("#oauth2.hasScope('server')")
	@PostMapping
	public void createUser(@Valid @RequestBody User user) {
		userService.create(user);
	}
}
