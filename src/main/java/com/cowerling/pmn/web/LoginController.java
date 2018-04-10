package com.cowerling.pmn.web;

import com.cowerling.pmn.web.message.ErrorMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if(error != null) {
            model.addAttribute("error", ErrorMessage.LOGIN_FAILED);
        }
        return "login";
    }
}
