package com.cowerling.pmn.web.department;

import com.cowerling.pmn.data.DepartmentRepository;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.department.Department;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.web.ConstantValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/department")
@SessionAttributes({"loginUser"})
public class DepartmentController {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    private UserRole authenticateUserRole(UserRole loginUserRole) {
        UserRole userRole;

        switch (loginUserRole) {
            case ADMIN:
                userRole = UserRole.ADVAN_USER;
                break;
            case ADVAN_USER:
            case USER:
            default:
                userRole = UserRole.USER;
                break;
        }

        return userRole;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Department> list(@RequestParam(value = "userAlias", defaultValue = ConstantValue.EMPTY_PARAMETER) String userAlias, @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            List<Department> departments = new ArrayList<>();

            if (userAlias.equals(ConstantValue.EMPTY_PARAMETER)) {
                departments = departmentRepository.findDepartments();
            } else {
                Map<String, Department> departmentMap = new HashMap<>();

                List<User> users = userRepository.findUsersByAlias(userAlias);
                users.forEach(user -> {
                    if (loginUser.getUserRole().superior(user.getUserRole())) {
                        Department department = user.getDepartment();
                        if (!departmentMap.containsKey(department.getName())) {
                            departmentMap.put(department.getName(), department);
                        }
                    }
                });

                if (departmentMap.size() != 0) {
                    departments = departmentMap.values().stream().collect(Collectors.toList());
                }
            }

            departments.forEach(department -> {
                try {
                    if (loginUser.getUserRole() == UserRole.SUPER_ADMIN) {
                        department.setSpecificNumber(userRepository.findUserCountByDepartment(department));
                    } else {
                        department.setSpecificNumber(userRepository.findUserCountByDepartment(department, authenticateUserRole(loginUser.getUserRole())));
                    }
                    department.setTag(generalEncoderService.staticEncrypt(department.getId()));
                } catch (EncoderServiceException e) {
                    throw new RuntimeException();
                }
            });

            return departments;
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> users(@RequestParam(value = "departmentTag") String departmentTag, @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            Long departmentId = Long.parseLong(generalEncoderService.staticDecrypt(departmentTag));

            if (loginUser.getUserRole() == UserRole.SUPER_ADMIN) {
                return userRepository.findUsersByDepartmentId(departmentId);
            } else {
                return userRepository.findUsersByDepartmentId(departmentId, authenticateUserRole(loginUser.getUserRole()));
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
