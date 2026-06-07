package com.HolgersDream.Deckforge.controller;

import com.HolgersDream.Deckforge.domain.AuthSessionUser;
import com.HolgersDream.Deckforge.service.AuthUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthUserController {

    private final AuthUserService service;
    private final SessionObjectRetriever sessionObjectRetriever;

    public AuthUserController(AuthUserService service, SessionObjectRetriever sessionObjectRetriever) {
        this.service = service;
        this.sessionObjectRetriever = sessionObjectRetriever;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/authentication/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("auth", new AuthRequest());
        return "authentication/register";
    }

    @PostMapping("/authentication/register")
    public String tryToRegister(@ModelAttribute AuthRequest authRequest, @RequestParam String repeated) {
        service.checkRegister(authRequest, repeated);
        return "redirect:/authentication/login";
    }

    @GetMapping("/authentication/login")
    public String showLoginForm(Model model) {
        model.addAttribute("auth", new AuthRequest());
        return "authentication/login";
    }

    @PostMapping("/authentication/login")
    public String tryToLogin(@ModelAttribute AuthRequest authRequest, HttpSession session) {
        AuthSessionUser loggedIn = service.checkLogin(authRequest);
        session.setAttribute("currentUser", loggedIn);
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String showWelcome(HttpSession session, Model model) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/authentication/login";
        }

        model.addAttribute("currentUser", currentUser);

        return "welcome";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/return")
    public String returnBack(HttpSession session) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        // Returnere enten til index siden eller velkomst-siden,
        // baseret på om brugeren er logget ind
        if (currentUser == null) {
            return "redirect:/";
        } else {
            return "redirect:/welcome";
        }
    }
}
