package com.cowerling.pmn.web;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserGender;
import com.cowerling.pmn.domain.user.form.UserProfileForm;
import com.cowerling.pmn.domain.user.form.UserSecurityForm;
import com.cowerling.pmn.exception.DuplicateUserException;
import com.cowerling.pmn.exception.ExceptionMessage;
import com.cowerling.pmn.security.PasswordEncoderService;
import com.cowerling.pmn.utils.ImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

@Controller
@RequestMapping("/user")
@SessionAttributes({"loginUser"})
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoderService passwordEncoderService;

    @Value("${user.photo.location}")
    private String photo_location;

    @Value("${user.photo.width}")
    private int photo_width;

    @Value("${user.photo.height}")
    private int photo_height;

    @Value("${user.photo.format}")
    private String photo_format;

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

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String showProfile(Model model) {
        return "user/profile";
    }

    @RequestMapping(value = "/profile/settings", method = RequestMethod.POST)
    public String profileSettings(UserProfileForm userProfileForm,  @RequestPart("photo") MultipartFile photo, HttpSession session) throws ParseException, IOException {
        User loginUser = (User)session.getAttribute("loginUser");

        loginUser.setUserGender(Arrays.stream(UserGender.values()).anyMatch(x -> x.name().equals(userProfileForm.getGender())) ? UserGender.valueOf(userProfileForm.getGender()) : null);
        loginUser.setEmail(userProfileForm.getEmail());
        loginUser.setBirthday(StringUtils.isNotEmpty(userProfileForm.getBirthday()) ? new SimpleDateFormat("yyyy-MM-dd").parse(userProfileForm.getBirthday()) : null);
        loginUser.setPhone(StringUtils.isNotEmpty(userProfileForm.getPhone()) ? userProfileForm.getPhone().replace("-", "") : null);

        if (!photo.isEmpty()) {
            loginUser.setPhoto(ImageUtils.compress(photo.getInputStream(), photo_width, photo_height, photo_format, photo_location));
        }

        userRepository.updateUser(loginUser);

        return "redirect:/user/profile";
    }

    @RequestMapping(value = "/profile/security", method = RequestMethod.POST)
    public String profileSecurity(UserSecurityForm userSecurityForm, HttpSession session) {
        if (StringUtils.isNotEmpty(userSecurityForm.getPassword())) {
            User loginUser = (User)session.getAttribute("loginUser");
            loginUser.setPassword(passwordEncoderService.encode(userSecurityForm.getPassword()));
            userRepository.updateUser(loginUser);
        }

        return "redirect:/user/profile";
    }
}
