package com.cowerling.pmn.web;

import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping("/")
@SessionAttributes({"loginUser"})
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model, HttpServletRequest request) throws UserNotFoundException {
        if (!model.containsAttribute("loginUser")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = ((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername();
            User loginUser = userRepository.findUserByName(name);
            if (loginUser == null) {
                throw new UserNotFoundException();
            }

            model.addAttribute("loginUser", loginUser);
        }

        return "home";
    }
}
