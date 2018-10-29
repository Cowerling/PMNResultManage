package com.cowerling.pmn.web;

import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.data.DataRecordStatus;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.project.ProjectCategory;
import com.cowerling.pmn.domain.project.ProjectStatus;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.ProjectSqlProvider.*;

@Controller
@RequestMapping("/workbench")
@SessionAttributes({"loginUser"})
public class WorkbenchController {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DataRepository dataRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String workbench(@ModelAttribute("loginUser") final User loginUser,
                            Model model) {
        if (loginUser.getUserRole() == UserRole.ADMIN) {
            List<Project> projects = projectRepository.findProjectsByUser(loginUser, FindMode.CREATOR, null, null);

            model.addAttribute("notStartProjectCount", projects.stream().filter(project -> project.getStatus() == ProjectStatus.WAIT).count());
            model.addAttribute("noManagerProjectCount", projects.stream().filter(project -> project.getManager() == null).count());

            Map<String, Long> projectStatistics = new HashMap<>();

            for (ProjectCategory projectCategory : ProjectCategory.values()) {
                projectStatistics.put(projectCategory.toString(), projects.stream().filter(project -> project.getCategory() == projectCategory).count());
            }

            JSONObject jsonObject = new JSONObject(projectStatistics);
            model.addAttribute("projectStatistics", jsonObject);
        } else if (loginUser.getUserRole() == UserRole.ADVAN_USER) {
            List<Project> projects = projectRepository.findProjectsByUser(loginUser, FindMode.MANAGER, null, null);

            model.addAttribute("notStartProjectCount", projects.stream().filter(project -> project.getStatus() == ProjectStatus.WAIT).count());
            model.addAttribute("noPrincipalProjectCount", projects.stream().filter(project -> project.getPrincipal() == null).count());

            Map<String, Long> projectStatistics = new HashMap<>();

            for (ProjectCategory projectCategory : ProjectCategory.values()) {
                projectStatistics.put(projectCategory.toString(), projects.stream().filter(project -> project.getCategory() == projectCategory).count());
            }

            JSONObject jsonObject = new JSONObject(projectStatistics);
            model.addAttribute("projectStatistics", jsonObject);
        } else if (loginUser.getUserRole() == UserRole.USER) {
            List<Project> projects = projectRepository.findProjectsByUser(loginUser, FindMode.PRINCIPAL, null, null);

            model.addAttribute("notStartProjectCount", projects.stream().filter(project -> project.getStatus() == ProjectStatus.WAIT).count());
            model.addAttribute("noMemberProjectCount", projects.stream().filter(project -> project.getMembers().size() == 0).count());

            model.addAttribute("unauditedDataRecordCount", dataRepository
                    .findDataRecordsByUser(loginUser, null, null, 0 , Integer.MAX_VALUE)
                    .stream().filter(dataRecord -> dataRecord.getProject().getPrincipal().getId() == loginUser.getId() && dataRecord.getStatus() == DataRecordStatus.UNAUDITED
                    ).count());


            Map<String, Long> projectStatistics = new HashMap<>();

            for (ProjectCategory projectCategory : ProjectCategory.values()) {
                projectStatistics.put(projectCategory.toString(), projects.stream().filter(project -> project.getCategory() == projectCategory).count());
            }

            List<Project> participatorProjects = projectRepository.findProjectsByUser(loginUser, FindMode.PARTICIPATOR, null, null);

            for (ProjectCategory projectCategory : ProjectCategory.values()) {
                projectStatistics.put(projectCategory.toString(), projectStatistics.get(projectCategory.toString()) + participatorProjects.stream().filter(project -> project.getCategory() == projectCategory).count());
            }

            JSONObject jsonObject = new JSONObject(projectStatistics);
            model.addAttribute("projectStatistics", jsonObject);
        }

        return "workbench";
    }
}
