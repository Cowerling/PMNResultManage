package com.cowerling.pmn.web.manage;

import com.cowerling.pmn.data.DepartmentRepository;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import com.cowerling.pmn.geodata.GeoAuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/manage")
@SessionAttributes({"loginUser"})
public class ManageController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private GeoAuthenticationRepository geoAuthenticationRepository;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public String list() {
        return "manage/user";
    }

    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public String userEdit(String userName, String role, String department) throws RuntimeException {
        try {
            User user = userRepository.findUserByName(userName);

            user.setUserRole(UserRole.valueOf(role));
            user.setDepartment(departmentRepository.findDepartmentByName(department));

            userRepository.updateUser(user);

            return "redirect:/manage/user";
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @RequestMapping(value = "/user/delete", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public String userDelete(String userName) throws RuntimeException {
        try {
            User user = userRepository.findUserByName(userName);

            geoAuthenticationRepository.removeUser(user.getName());
            userRepository.removeUser(user);

            return "redirect:/manage/user";
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
