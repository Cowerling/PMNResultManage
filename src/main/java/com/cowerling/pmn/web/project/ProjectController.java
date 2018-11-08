package com.cowerling.pmn.web.project;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.data.provider.DataSqlProvider;
import com.cowerling.pmn.data.provider.ProjectSqlProvider;
import com.cowerling.pmn.domain.data.DataRecordStatus;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.project.ProjectCategory;
import com.cowerling.pmn.domain.project.ProjectStatus;
import com.cowerling.pmn.domain.project.ProjectVerification;
import com.cowerling.pmn.domain.project.form.ProjectAddForm;
import com.cowerling.pmn.domain.project.form.ProjectSettingsForm;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import com.cowerling.pmn.exception.DuplicateMemberException;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.utils.DateUtils;
import com.cowerling.pmn.web.ConstantValue;
import com.cowerling.pmn.web.message.ErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.cowerling.pmn.data.provider.ProjectSqlProvider.*;
import static com.cowerling.pmn.data.provider.ProjectSqlProvider.Field.*;
import static com.cowerling.pmn.data.provider.ProjectSqlProvider.Order.*;

@Controller
@RequestMapping("/project")
@SessionAttributes({"loginUser"})
public class ProjectController {
    private static final String LIST_REQUEST_COLUMN_NAME = "name";
    private static final String LIST_REQUEST_COLUMN_CATEGORY = "category";
    private static final String LIST_REQUEST_COLUMN_CREATE_TIME = "create_time";
    private static final String LIST_REQUEST_COLUMN_STATUS = "status";
    private static final String LIST_SEARCH_NAME = "name";
    private static final String LIST_SEARCH_CATEGORY = "category";
    private static final String LIST_SEARCH_CREATE_TIME = "createTime";
    private static final String LIST_SEARCH_FINISH_TIME = "finishTime";
    private static final String LIST_SEARCH_CREATOR = "creator";
    private static final String LIST_SEARCH_MANAGER = "manager";
    private static final String LIST_SEARCH_PRINCIPAL = "principal";
    private static final String LIST_SEARCH_REMARK = "remark";
    private static final String LIST_SEARCH_STATUS = "status";

    private static final String SETTING_MANAGER = "manager";
    private static final String SETTING_PRINCIPAL = "principal";
    private static final String SETTING_REMARK = "remark";
    private static final String SETTING_STATUS = "status";
    private static final String SETTING_MEMBER_ADD = "memberAdd";
    private static final String SETTING_MEMBER_REMOVE = "memberRemove";
    private static final String SETTING_VERIFICATION_PRINCIPAL = "verificationPrincipal";
    private static final String SETTING_VERIFICATION_MANAGER = "verificationManager";
    private static final String SETTING_VERIFICATION_CREATOR = "verificationCreator";

    private static final String MESSAGE_MEMBER_ERROR = "memberError";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @Value("${file.data.location}")
    private String dataFileLocation;

    private void authenticateUser(User user, ProjectSqlProvider.FindMode findMode) {
        if ((findMode == ProjectSqlProvider.FindMode.CREATOR && user.getUserRole() != UserRole.ADMIN) ||
                (findMode == ProjectSqlProvider.FindMode.MANAGER && user.getUserRole() != UserRole.ADVAN_USER) ||
                (findMode == ProjectSqlProvider.FindMode.PRINCIPAL && user.getUserRole() != UserRole.USER) ||
                (findMode == ProjectSqlProvider.FindMode.PARTICIPATOR && user.getUserRole() != UserRole.USER)) {
            throw new RuntimeException();
        }
    }

    private List<Pair<Field, Order>> orders(JSONArray tableColumns, JSONArray tableOrders) {
        List<Pair<Field, Order>> orders = new ArrayList<>();

        tableOrders.forEach(item -> {
            JSONObject tableOrder = (JSONObject) item;
            String columnName = tableColumns.getJSONObject(tableOrder.getInt(ConstantValue.LIST_REQUEST_ORDER_COLUMN)).getString(ConstantValue.LIST_REQUEST_COLUMNS_NAME);
            Order order = tableOrder.getString(ConstantValue.LIST_REQUEST_ORDER_DIR).equals(ConstantValue.LIST_REQUEST_ORDER_ASC) ? ASCENDING : DESCENDING;

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

        return orders;
    }

    private Map<Field, Object> filters(String search) throws ParseException {
        Map<Field, Object> filters = null;

        if (StringUtils.isNotEmpty(search)) {
            filters = new HashMap<>();

            JSONObject searchJsonObject = new JSONObject(search);

            if (searchJsonObject.has(LIST_SEARCH_NAME)) {
                JSONArray searchNames = searchJsonObject.getJSONArray(LIST_SEARCH_NAME);
                List<String> names = searchNames.toList().stream().map(item -> item.toString()).collect(Collectors.toList());
                filters.put(Field.NAME, names);
            }

            if (searchJsonObject.has(LIST_SEARCH_CATEGORY)) {
                JSONArray searchCategories = searchJsonObject.getJSONArray(LIST_SEARCH_CATEGORY);
                List<String> categories = searchCategories.toList().stream().map(item -> item.toString()).collect(Collectors.toList());
                filters.put(Field.CATEGORY, categories);
            }

            if (searchJsonObject.has(LIST_SEARCH_CREATE_TIME)) {
                JSONArray searchCreateTimes = searchJsonObject.getJSONArray(LIST_SEARCH_CREATE_TIME);
                Date startCreateTime = DateUtils.parse(searchCreateTimes.getString(0)), endCreateTime = DateUtils.parse(searchCreateTimes.getString(1));

                if (startCreateTime.before(endCreateTime)) {
                    filters.put(Field.START_CREATE_TIME, startCreateTime);
                    filters.put(Field.END_CREATE_TIME, endCreateTime);
                }
            }

            if (searchJsonObject.has(LIST_SEARCH_FINISH_TIME)) {
                JSONArray searchFinishTimes = searchJsonObject.getJSONArray(LIST_SEARCH_FINISH_TIME);
                Date startFinishTime = DateUtils.parse(searchFinishTimes.getString(0)), endFinishTime = DateUtils.parse(searchFinishTimes.getString(1));

                if (startFinishTime.before(endFinishTime)) {
                    filters.put(Field.START_FINISH_TIME, startFinishTime);
                    filters.put(Field.END_FINISH_TIME, endFinishTime);
                }
            }

            if (searchJsonObject.has(LIST_SEARCH_STATUS)) {
                JSONArray searchStatus = searchJsonObject.getJSONArray(LIST_SEARCH_STATUS);
                List<String> status = searchStatus.toList().stream().map(item -> item.toString()).collect(Collectors.toList());
                filters.put(Field.STATUS, status);
            }

            if (searchJsonObject.has(LIST_SEARCH_REMARK)) {
                String remark = searchJsonObject.getString(LIST_SEARCH_REMARK);
                if (StringUtils.isNotEmpty(remark)) {
                    filters.put(Field.REMARK, remark);
                }
            }

            if (searchJsonObject.has(LIST_SEARCH_CREATOR)) {
                List<Long> creators = searchJsonObject.getJSONArray(LIST_SEARCH_CREATOR).toList().stream().map(item -> {
                    User user = userRepository.findUserByName(item.toString());

                    if (user == null || user.getUserRole() != UserRole.ADMIN) {
                        throw new RuntimeException();
                    }

                    return user.getId();
                }).collect(Collectors.toList());

                filters.put(Field.CREATOR, creators);
            }

            if (searchJsonObject.has(LIST_SEARCH_MANAGER)) {
                List<Long> managers = searchJsonObject.getJSONArray(LIST_SEARCH_MANAGER).toList().stream().map(item -> {
                    User user = userRepository.findUserByName(item.toString());

                    if (user == null || user.getUserRole() != UserRole.ADVAN_USER) {
                        throw new RuntimeException();
                    }

                    return user.getId();
                }).collect(Collectors.toList());

                filters.put(Field.MANAGER, managers);
            }

            if (searchJsonObject.has(LIST_SEARCH_PRINCIPAL)) {
                List<Long> principals = searchJsonObject.getJSONArray(LIST_SEARCH_PRINCIPAL).toList().stream().map(item -> {
                    User user = userRepository.findUserByName(item.toString());

                    if (user == null || user.getUserRole() != UserRole.USER) {
                        throw new RuntimeException();
                    }

                    return user.getId();
                }).collect(Collectors.toList());

                filters.put(Field.PRINCIPAL, principals);
            }
        }

        return filters;
    }

    @RequestMapping(value = "/list/{mode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ToResourceNotFound
    public @ResponseBody Map<String, Object> listDetail(
            @PathVariable("mode") String mode,
            @ModelAttribute("loginUser") final User loginUser,
            @RequestParam(value = "request") String request,
            @RequestParam(value = "search") String search) throws ResourceNotFoundException {
        try {
            ProjectSqlProvider.FindMode findMode = ProjectSqlProvider.FindMode.valueOf(mode.toUpperCase());

            authenticateUser(loginUser, findMode);

            JSONObject jsonObject = new JSONObject(request);
            int draw = jsonObject.getInt(ConstantValue.LIST_REQUEST_DRAW), start = jsonObject.getInt(ConstantValue.LIST_REQUEST_START), length = jsonObject.getInt(ConstantValue.LIST_REQUEST_LENGTH);
            JSONArray tableColumns = jsonObject.getJSONArray(ConstantValue.LIST_REQUEST_COLUMNS);
            JSONArray tableOrders = jsonObject.getJSONArray(ConstantValue.LIST_REQUEST_ORDER);

            List<Pair<Field, Order>> orders = orders(tableColumns, tableOrders);
            Map<Field, Object> filters = filters(search);

            List<Project> projects = projectRepository.findProjectsByUser(loginUser, findMode, filters, orders, start, length);
            projects.forEach(project -> {
                try {
                    project.setTag(generalEncoderService.staticEncrypt(project.getId()));
                } catch (EncoderServiceException e) {
                    throw new RuntimeException();
                }
            });

            Long count = projectRepository.findProjectCountByUser(loginUser, findMode, filters);

            Map<String, Object> listDetail = new HashMap<>();
            listDetail.put("draw", draw);
            listDetail.put("count", count);
            listDetail.put("projects", projects);

            return listDetail;
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/list/{mode}/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ToResourceNotFound
    public @ResponseBody List<Project> listDetail(
            @PathVariable("mode") String mode,
            @ModelAttribute("loginUser") final User loginUser,
            @RequestParam(value = "search") String search) throws ResourceNotFoundException {
        try {
            ProjectSqlProvider.FindMode findMode = ProjectSqlProvider.FindMode.valueOf(mode.toUpperCase());

            authenticateUser(loginUser, findMode);

            List<Project> projects = projectRepository.findProjectsByUser(loginUser, findMode, filters(search), null);
            projects.forEach(project -> {
                try {
                    project.setTag(generalEncoderService.staticEncrypt(project.getId()));
                } catch (EncoderServiceException e) {
                    throw new RuntimeException();
                }
            });

            return projects;
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

            return projectRepository.findProjectCountByUser(loginUser, findMode, null);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping("/list")
    public String list() {
        return "/project/list";
    }

    @RequestMapping(value = "/{projectTag}", method = RequestMethod.GET)
    public String detail(@PathVariable("projectTag") String projectTag, @RequestParam(value = "message", defaultValue = ConstantValue.EMPTY_PARAMETER) String message,  Model model) throws ResourceNotFoundException {
        try {
            Project project = projectRepository.findProjectById(Long.parseLong(generalEncoderService.staticDecrypt(projectTag)));
            project.setTag(projectTag);
            model.addAttribute(project);

            if (StringUtils.isNotEmpty(message)) {
                JSONObject jsonObject = new JSONObject(generalEncoderService.staticDecrypt(message));

                if (jsonObject.has(MESSAGE_MEMBER_ERROR)) {
                    model.addAttribute(MESSAGE_MEMBER_ERROR, jsonObject.getInt(MESSAGE_MEMBER_ERROR));
                }
            }

            return "project/detail";
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String add(ProjectAddForm projectAddForm, @ModelAttribute("loginUser") final User loginUser) {
        try {
            Project project = new Project(projectAddForm.getName(), loginUser, ProjectCategory.valueOf(projectAddForm.getCategory()), projectAddForm.getRemark());

            projectRepository.saveProject(project);
            projectRepository.saveProjectVerification(project, new ProjectVerification());

            return "redirect:/project/list";
        } catch (Exception e) {
            throw new AccessDeniedException(ErrorMessage.ADD_PROJECT_FAILED);
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String remove(String projectTag,
                         @ModelAttribute("loginUser") final User loginUser) throws AccessDeniedException {
        try {
            Project project = projectRepository.findProjectById(Long.parseLong(generalEncoderService.staticDecrypt(projectTag)));

            if (project.getCreator().getId() != loginUser.getId()) {
                throw new RuntimeException();
            }

            project.getMembers().forEach(member -> {
                dataRepository.findDataRecordsByUser(member, new HashMap<>() {
                    {
                        put(DataSqlProvider.RecordField.PROJECT, new ArrayList<Long>() {
                            {
                                add(project.getId());
                            }
                        });
                    }
                }, null, 0, Integer.MAX_VALUE).forEach(dataRecord -> {
                    if (dataRecord.getStatus() != DataRecordStatus.QUALIFIED) {
                        File deleteFile = new File(dataFileLocation + dataRecord.getFile());
                        if (deleteFile.exists() && deleteFile.isFile()) {
                            deleteFile.delete();
                        }
                    }

                    dataRepository.removeDataRecord(dataRecord);
                });

                userRepository.removeMemberByProject(member, project);
            });

            projectRepository.removeProject(project);

            return "redirect:/project/list";
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    @RequestMapping(value = "/settings/{category}", method = RequestMethod.POST)
    @PreAuthorize("(#category == '" + SETTING_MANAGER + "' and hasRole('ROLE_ADMIN')) or " +
            "(#category == '" + SETTING_REMARK + "' and hasRole('ROLE_ADMIN')) or " +
            "(#category == '" + SETTING_PRINCIPAL + "' and hasRole('ROLE_ADVAN_USER')) or " +
            "(#category == '" + SETTING_STATUS + "' and hasRole('ROLE_ADMIN') and #projectSettingsForm.status.toUpperCase() == '" + ConstantValue.PROJECT_STATUS_FINISH + "') or " +
            "(#category == '" + SETTING_STATUS + "' and hasRole('ROLE_USER') and #projectSettingsForm.status.toUpperCase() == '" + ConstantValue.PROJECT_STATUS_PROGRESS + "') or " +
            "(#category == '" + SETTING_MEMBER_ADD + "' and hasRole('ROLE_USER')) or " +
            "(#category == '" + SETTING_MEMBER_REMOVE + "' and hasRole('ROLE_USER')) or " +
            "(#category == '" + SETTING_VERIFICATION_PRINCIPAL + "' and hasRole('ROLE_USER')) or " +
            "(#category == '" + SETTING_VERIFICATION_MANAGER + "' and hasRole('ROLE_ADVAN_USER')) or " +
            "(#category == '" + SETTING_VERIFICATION_CREATOR + "' and hasRole('ROLE_ADMIN'))")
    public String projectSettings(@PathVariable("category") String category,
                                  ProjectSettingsForm projectSettingsForm,
                                  String projectTag,
                                  @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException, EncoderServiceException {
        Project project = projectRepository.findProjectById(Long.parseLong(generalEncoderService.staticDecrypt(projectTag)));

        if (project == null) {
            throw new ResourceNotFoundException();
        }

        if (category.equals(SETTING_STATUS) && projectSettingsForm.getStatus().equals(ConstantValue.PROJECT_STATUS_FINISH) && project.getCreator().getId() != loginUser.getId()) {
            throw new ResourceNotFoundException();
        }

        if (category.equals(SETTING_STATUS) && projectSettingsForm.getStatus().equals(ConstantValue.PROJECT_STATUS_PROGRESS) && (project.getPrincipal() == null || project.getPrincipal().getId() != loginUser.getId())) {
            throw new ResourceNotFoundException();
        }

        if ((category.equals(SETTING_MEMBER_ADD) || category.equals(SETTING_MEMBER_REMOVE)) && (project.getPrincipal() == null || project.getPrincipal().getId() != loginUser.getId() || project.getStatus() != ProjectStatus.PROGRESS)) {
            throw new ResourceNotFoundException();
        }

        if (category.equals(SETTING_VERIFICATION_PRINCIPAL) && !(project.getStatus() == ProjectStatus.PROGRESS && project.getVerification().getManagerAdopt() == false && project.getVerification().getCreatorAdopt() == false)) {
            throw new ResourceNotFoundException();
        }

        if (category.equals(SETTING_VERIFICATION_MANAGER) && !(project.getStatus() == ProjectStatus.PROGRESS && project.getVerification().getPrincipalAdopt() == true && project.getVerification().getCreatorAdopt() == false)) {
            throw new ResourceNotFoundException();
        }

        if (category.equals(SETTING_VERIFICATION_CREATOR) && !(project.getStatus() == ProjectStatus.PROGRESS && project.getVerification().getPrincipalAdopt() == true && project.getVerification().getManagerAdopt() == true)) {
            throw new ResourceNotFoundException();
        }

        int duplicateMemberCount = 0;

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
            case SETTING_STATUS:
                project.setStatus(ProjectStatus.valueOf(projectSettingsForm.getStatus().toUpperCase()));
                if (project.getStatus() == ProjectStatus.FINISH) {
                    project.setFinishTime(new Date());
                }
                projectRepository.updateProject(project);
                break;
            case SETTING_MEMBER_ADD:
                if (StringUtils.isNotEmpty(projectSettingsForm.getMember())) {
                    JSONArray jsonArray = new JSONArray(projectSettingsForm.getMember());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        User user = userRepository.findUserByName(jsonArray.getString(i));
                        if (user != null && user.getId() != loginUser.getId()) {
                            try {
                                userRepository.saveMemberByProject(user, project);
                            } catch (DuplicateMemberException e) {
                                duplicateMemberCount++;
                            }
                        }
                    }
                }
                break;
            case SETTING_MEMBER_REMOVE:
                if (StringUtils.isNotEmpty(projectSettingsForm.getMember())) {
                    (new JSONArray(projectSettingsForm.getMember())).forEach(member -> {
                        User user = userRepository.findUserByName((String) member);
                        if (user != null) {
                            userRepository.removeMemberByProject(user, project);
                        }
                    });
                }
                break;
            case SETTING_VERIFICATION_PRINCIPAL:
                project.getVerification().setPrincipalAdopt(projectSettingsForm.getPrincipalAdopt());
                project.getVerification().setPrincipalRemark(projectSettingsForm.getPrincipalRemark());
                projectRepository.updateProjectVerification(project, project.getVerification());
                break;
            case SETTING_VERIFICATION_MANAGER:
                if (projectSettingsForm.getManagerAdopt()) {
                    project.getVerification().setManagerAdopt(projectSettingsForm.getManagerAdopt());
                } else {
                    project.getVerification().setPrincipalAdopt(projectSettingsForm.getManagerAdopt());
                }

                project.getVerification().setManagerRemark(projectSettingsForm.getManagerRemark());
                projectRepository.updateProjectVerification(project, project.getVerification());
                break;
            case SETTING_VERIFICATION_CREATOR:
                if (projectSettingsForm.getCreatorAdopt()) {
                    project.getVerification().setCreatorAdopt(projectSettingsForm.getCreatorAdopt());
                } else {
                    project.getVerification().setManagerAdopt(projectSettingsForm.getCreatorAdopt());
                }

                project.getVerification().setCreatorRemark(projectSettingsForm.getCreatorRemark());
                projectRepository.updateProjectVerification(project, project.getVerification());
                break;
            default:
                break;
        }

        JSONObject message = new JSONObject();
        if (duplicateMemberCount != 0) {
            message.put(MESSAGE_MEMBER_ERROR, duplicateMemberCount);
        }

        return "redirect:/project/" + projectTag + (message.length() > 0 ? "?message=" + generalEncoderService.staticEncrypt(message.toString()) : "");
    }
}
