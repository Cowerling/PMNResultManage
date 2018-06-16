package com.cowerling.pmn.web.project;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.data.provider.ProjectSqlProvider;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.project.ProjectCategory;
import com.cowerling.pmn.domain.project.form.ProjectAddForm;
import com.cowerling.pmn.domain.project.form.ProjectSettingsForm;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.ProjectSqlProvider.*;
import static com.cowerling.pmn.data.provider.ProjectSqlProvider.Field.*;
import static com.cowerling.pmn.data.provider.ProjectSqlProvider.Order.*;

@Controller
@RequestMapping("/project")
@SessionAttributes({"loginUser"})
public class ProjectController {
    private static final String LIST_REQUEST_DRAW = "draw";
    private static final String LIST_REQUEST_START = "start";
    private static final String LIST_REQUEST_LENGTH = "length";
    private static final String LIST_REQUEST_COLUMNS = "columns";
    private static final String LIST_REQUEST_COLUMNS_NAME = "name";
    private static final String LIST_REQUEST_COLUMN_NAME = "name";
    private static final String LIST_REQUEST_COLUMN_CATEGORY = "category";
    private static final String LIST_REQUEST_COLUMN_CREATE_TIME = "create_time";
    private static final String LIST_REQUEST_COLUMN_STATUS = "status";
    private static final String LIST_REQUEST_ORDER = "order";
    private static final String LIST_REQUEST_ORDER_COLUMN = "column";
    private static final String LIST_REQUEST_ORDER_DIR = "dir";
    private static final String LIST_REQUEST_ORDER_ASC = "asc";

    private static final String SETTING_MANAGER = "manager";
    private static final String SETTING_PRINCIPAL = "principal";
    private static final String SETTING_REMARK = "remark";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    private void authenticateUser(User user, ProjectSqlProvider.FindMode findMode) {
        if ((findMode == ProjectSqlProvider.FindMode.CREATOR && user.getUserRole() != UserRole.ADMIN) ||
                (findMode == ProjectSqlProvider.FindMode.MANAGER && user.getUserRole() != UserRole.ADVAN_USER) ||
                (findMode == ProjectSqlProvider.FindMode.PRINCIPAL && user.getUserRole() != UserRole.USER) ||
                (findMode == ProjectSqlProvider.FindMode.PARTICIPATOR && user.getUserRole() != UserRole.USER)) {
            throw new RuntimeException();
        }
    }

    @RequestMapping(value = "/list/{mode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ToResourceNotFound
    public @ResponseBody Map<String, Object> listDetail(@PathVariable("mode") String mode,
                   @ModelAttribute("loginUser") final User loginUser,
                   @RequestParam(value = "request") String request) throws ResourceNotFoundException {
        try {
            ProjectSqlProvider.FindMode findMode = ProjectSqlProvider.FindMode.valueOf(mode.toUpperCase());

            authenticateUser(loginUser, findMode);

            JSONObject jsonObject = new JSONObject(request);
            int draw = jsonObject.getInt(LIST_REQUEST_DRAW), start = jsonObject.getInt(LIST_REQUEST_START), length = jsonObject.getInt(LIST_REQUEST_LENGTH);
            JSONArray tableColumns = jsonObject.getJSONArray(LIST_REQUEST_COLUMNS);
            JSONArray tableOrders = jsonObject.getJSONArray(LIST_REQUEST_ORDER);

            List<Pair<Field, Order>> orders = new ArrayList<>();
            tableOrders.forEach(item -> {
                JSONObject tableOrder = (JSONObject) item;
                String columnName = tableColumns.getJSONObject(tableOrder.getInt(LIST_REQUEST_ORDER_COLUMN)).getString(LIST_REQUEST_COLUMNS_NAME);
                Order order = tableOrder.getString(LIST_REQUEST_ORDER_DIR).equals(LIST_REQUEST_ORDER_ASC) ? ASCENDING : DESCENDING;

                switch (columnName) {
                    case LIST_REQUEST_COLUMN_NAME:
                        orders.add(Pair.of(NAME, order));
                        break;
                    case LIST_REQUEST_COLUMN_CATEGORY:
                        orders.add(Pair.of(CATEGORY, order));
                        break;
                    case LIST_REQUEST_COLUMN_CREATE_TIME:
                        orders.add(Pair.of(CREATE_TIME, order));
                        break;
                    case LIST_REQUEST_COLUMN_STATUS:
                        orders.add(Pair.of(STATUS, order));
                        break;
                    default:
                        break;
                }
            });

            List<Project> projects = projectRepository.findProjectsByUser(loginUser, findMode, null, orders, start, length);
            projects.forEach(project -> {
                try {
                    project.setTag(generalEncoderService.staticEncrypt(project.getId()));
                } catch (EncoderServiceException e) {
                    throw new RuntimeException();
                }
            });

            Long count = projectRepository.findProjectCountByUser(loginUser, findMode);

            Map<String, Object> listDetail = new HashMap<>();
            listDetail.put("draw", draw);
            listDetail.put("count", count);
            listDetail.put("projects", projects);

            return listDetail;
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/count/{mode}", method = RequestMethod.GET)
    @ToResourceNotFound
    public @ResponseBody Long count(@PathVariable("mode") String mode, @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            ProjectSqlProvider.FindMode findMode = ProjectSqlProvider.FindMode.valueOf(mode.toUpperCase());

            authenticateUser(loginUser, findMode);

            return projectRepository.findProjectCountByUser(loginUser, findMode);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping("/list")
    public String list() {
        return "/project/list";
    }

    @RequestMapping("/{projectTag}")
    public String detail(@PathVariable("projectTag") String projectTag, Model model) throws ResourceNotFoundException {
        try {
            Project project = projectRepository.findProjectById(Long.parseLong(generalEncoderService.staticDecrypt(projectTag)));
            project.setTag(projectTag);
            model.addAttribute(project);
            return "project/detail";
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String add(ProjectAddForm projectAddForm, @ModelAttribute("loginUser") final User loginUser) {
        Project project = new Project(projectAddForm.getName(), loginUser, ProjectCategory.valueOf(projectAddForm.getCategory()), projectAddForm.getRemark());
        projectRepository.saveProject(project);

        return "redirect:/project/list";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String remove(String projectTag) throws EncoderServiceException {
        projectRepository.removeProjectById(Long.parseLong(generalEncoderService.staticDecrypt(projectTag)));

        return "redirect:/project/list";
    }

    @RequestMapping(value = "/settings/{category}", method = RequestMethod.POST)
    @PreAuthorize("(#category == '" + SETTING_MANAGER + "' and hasRole('ROLE_ADMIN')) or " +
            "(#category == '" + SETTING_REMARK + "' and hasRole('ROLE_ADMIN')) or " +
            "(#category == '" + SETTING_PRINCIPAL + "' and hasRole('ROLE_ADVAN_USER'))")
    public String projectSettings(@PathVariable("category") String category, ProjectSettingsForm projectSettingsForm, String projectTag) throws EncoderServiceException {
        Project project = projectRepository.findProjectById(Long.parseLong(generalEncoderService.staticDecrypt(projectTag)));

        switch (category) {
            case SETTING_MANAGER:
                project.setManager(userRepository.findUserByName(projectSettingsForm.getManager()));
                projectRepository.updateProject(project);
                break;
            case SETTING_PRINCIPAL:
                project.setPrincipal(userRepository.findUserByName(projectSettingsForm.getPrincipal()));
                projectRepository.updateProject(project);
                break;
            case SETTING_REMARK:
                project.setRemark(projectSettingsForm.getRemark());
                projectRepository.updateProject(project);
                break;
            default:
                break;
        }

        return "redirect:/project/" + projectTag;
    }
}
