package com.cowerling.pmn.web.department;

import com.cowerling.pmn.data.DepartmentRepository;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.department.Department;
import com.cowerling.pmn.domain.project.Project;
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

    private UserRole suitableUserRole(String userGrade) {
        UserRole userRole = UserRole.SUPER_ADMIN;
        switch (userGrade.toLowerCase()) {
            case ConstantValue.USER_GRADE_CREATOR:
                userRole = UserRole.ADMIN;
                break;
            case ConstantValue.USER_GRADE_MANAGER:
                userRole = UserRole.ADVAN_USER;
                break;
            case ConstantValue.USER_GRADE_PRINCIPAL:
            case ConstantValue.USER_GRADE_PARTICIPATOR:
                userRole = UserRole.USER;
                break;
            default:
                break;
        }

        return userRole;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Department> list(@RequestParam(value = "userGrade", defaultValue = ConstantValue.EMPTY_PARAMETER) String userGrade,
                                 @RequestParam(value = "userAlias", defaultValue = ConstantValue.EMPTY_PARAMETER) String userAlias,
                                 @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            List<Department> departments = new ArrayList<>();

            if (userAlias.equals(ConstantValue.EMPTY_PARAMETER)) {
                departments = departmentRepository.findDepartments();
            } else {
                Map<String, Department> departmentMap = new HashMap<>();

                List<User> users = userRepository.findUsersByAlias(userAlias);
                users.forEach(user -> {
                    if (user.getUserRole() == suitableUserRole(userGrade)) {
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
                UserRole userRole = suitableUserRole(userGrade);

                try {
                    if (userRole == UserRole.SUPER_ADMIN) {
                        department.setSpecificNumber(userRepository.findUserCountByDepartment(department));
                    } else {
                        department.setSpecificNumber(userRepository.findUserCountByDepartment(department, userRole));

                        if (userGrade.equals(ConstantValue.USER_GRADE_PARTICIPATOR) && loginUser.getDepartment().getId() == department.getId()) {
                            department.setSpecificNumber(department.getSpecificNumber() - 1);
                        }
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
    public List<User> users(@RequestParam(value = "departmentTag") String departmentTag,
                            @RequestParam(value = "userGrade", defaultValue = ConstantValue.EMPTY_PARAMETER) String userGrade,
                            @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            Long departmentId = Long.parseLong(generalEncoderService.staticDecrypt(departmentTag));

            UserRole userRole = suitableUserRole(userGrade);

            if (userRole == UserRole.SUPER_ADMIN) {
                return userRepository.findUsersByDepartmentId(departmentId);
            } else {
                List<User> users = userRepository.findUsersByDepartmentId(departmentId, userRole);
                users.removeIf(user -> user.getId() == loginUser.getId());
                return users;
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
