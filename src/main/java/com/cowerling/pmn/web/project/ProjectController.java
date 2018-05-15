package com.cowerling.pmn.web.project;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/project")
@SessionAttributes({"loginUser"})
public class ProjectController {
    private static final String DEFAULT_MANAGED_USERS_OFFSET = "0";
    private static final String DEFAULT_MANAGED_USERS_LIMIT = "25";

    @Autowired
    private ProjectRepository projectRepository;

    private void authenticateUser(User user, ProjectRepository.FindMode findMode) {
        if ((findMode == ProjectRepository.FindMode.CREATOR && user.getUserRole() != UserRole.ADMIN) ||
                (findMode == ProjectRepository.FindMode.MANAGER && user.getUserRole() != UserRole.ADVAN_USER) ||
                (findMode == ProjectRepository.FindMode.PRINCIPAL && user.getUserRole() != UserRole.USER) ||
                (findMode == ProjectRepository.FindMode.PARTICIPATOR && user.getUserRole() != UserRole.USER)) {
            throw new RuntimeException();
        }
    }

    @RequestMapping(value = "/list/{mode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ToResourceNotFound
    public @ResponseBody List<Project> list(@PathVariable("mode") String mode, @ModelAttribute("loginUser") final User loginUser, @RequestParam(value = "offset", defaultValue = DEFAULT_MANAGED_USERS_OFFSET) int offset, @RequestParam(value = "limit", defaultValue = DEFAULT_MANAGED_USERS_LIMIT) int limit) throws ResourceNotFoundException {
        try {
            ProjectRepository.FindMode findMode = ProjectRepository.FindMode.valueOf(mode.toUpperCase());

            authenticateUser(loginUser, findMode);

            return projectRepository.findProjectsByUser(loginUser, findMode, offset, limit);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/count/{mode}", method = RequestMethod.GET)
    @ToResourceNotFound
    public @ResponseBody Long count(@PathVariable("mode") String mode, @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            ProjectRepository.FindMode findMode = ProjectRepository.FindMode.valueOf(mode.toUpperCase());

            authenticateUser(loginUser, findMode);

            return projectRepository.findProjectCountByUser(loginUser, findMode);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
