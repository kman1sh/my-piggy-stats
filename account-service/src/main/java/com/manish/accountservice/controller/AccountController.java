package com.manish.accountservice.controller;

import com.manish.accountservice.domain.Account;
import com.manish.accountservice.domain.User;
import com.manish.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PreAuthorize("#oauth2.hasScope('server') or #name.equals('demo')")
    @GetMapping(value = "/{name}")
    public Account getAccountByName(@PathVariable String name) {
        return accountService.findByName(name);
    }

    @GetMapping(value = "/current")
    public Account getCurrentAccount(Principal principal) {
        return accountService.findByName(principal.getName());
    }

    @PutMapping(value = "/current")
    public void saveCurrentAccount(Principal principal, @Valid @RequestBody Account account) {
        accountService.saveChanges(principal.getName(), account);
    }

    @PostMapping
    public Account createNewAccount(@Valid @RequestBody User user) {
        return accountService.create(user);
    }

    @GetMapping(path = "/temp")
    public String temp() {
        return "working";
    }
}
