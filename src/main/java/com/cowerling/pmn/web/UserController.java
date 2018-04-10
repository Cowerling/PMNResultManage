package com.cowerling.pmn.web;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.DuplicateUserException;
import com.cowerling.pmn.exception.ExceptionMessage;
import com.cowerling.pmn.security.PasswordEncoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
@SessionAttributes({"loginUser"})
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoderService passwordEncoderService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            throw new SessionAuthenticationException(ExceptionMessage.REGISTER_SESSION_AUTHENTICATION);
        }

        model.addAttribute("registerUser", new User());
        return "user/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegister(RedirectAttributes redirectAttributes, @Valid @ModelAttribute("registerUser") User user, Errors errors) {
        if (errors.hasErrors()) {
            return "user/register";
        }

        try {
            user.setPassword(passwordEncoderService.encode(user.getPassword()));

            userRepository.saveUser(user);
        } catch (DuplicateUserException e) {
            errors.reject(e.getClass().getName(), e.getMessage());
            return "user/register";
        }

        redirectAttributes.addFlashAttribute("name", user.getName());

        return "redirect:/user/registerSuccess";
    }

    @RequestMapping("/registerSuccess")
    @PreAuthorize("#model.containsAttribute('name')")
    @ToResourceNotFound
    public String registerSuccess(Model model) {
        return "user/registerSuccess";
    }
}
