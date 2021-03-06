package com.cowerling.pmn.web.department;

import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.data.DepartmentRepository;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.department.Department;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.web.ConstantValue;
import org.apache.commons.lang3.StringUtils;
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
    private DataRepository dataRepository;

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
    public Map<String, List<User>> users(@RequestParam(value = "userGrade", defaultValue = ConstantValue.EMPTY_PARAMETER) String userGrade,
                                         @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            Map<String, List<User>> users = new HashMap<>();

            for (Department department : departmentRepository.findDepartments()) {
                List<User> subUsers = StringUtils.isNotEmpty(userGrade) ?
                        userRepository.findUsersByDepartmentId(department.getId(), suitableUserRole(userGrade)) :
                        userRepository.findUsersByDepartmentId(department.getId());
                users.put(department.getName(), subUsers);
            }

            return users;
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/projectUsers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<User>> projectUsers(@RequestParam(value = "dataRecordTag") String dataRecordTag,
                                                @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord.getProject().getPrincipal().getId() != loginUser.getId()) {
                throw new RuntimeException();
            }

            Map<String, List<User>> projectUsers = new HashMap<>();

            for (User user: dataRecord.getProject().getMembers()) {
                if (!projectUsers.containsKey(user.getDepartment().getName())) {
                    projectUsers.put(user.getDepartment().getName(), new ArrayList<>());
                }

                projectUsers.get(user.getDepartment().getName()).add(user);
            }

            return projectUsers;
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
