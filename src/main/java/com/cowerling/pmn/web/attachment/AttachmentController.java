package com.cowerling.pmn.web.attachment;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.data.AttachmentRepository;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.attachment.Attachment;
import com.cowerling.pmn.domain.attachment.AttachmentAuthority;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.project.ProjectStatus;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.AttachmentUploadException;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ExceptionMessage;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.utils.DateUtils;
import com.cowerling.pmn.web.ConstantValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.cowerling.pmn.data.provider.AttachmentProvider.*;
import static com.cowerling.pmn.data.provider.AttachmentProvider.Order.ASCENDING;
import static com.cowerling.pmn.data.provider.AttachmentProvider.Order.DESCENDING;
import static com.cowerling.pmn.data.provider.AttachmentProvider.Field.*;

@Controller
@RequestMapping("/attachment")
@SessionAttributes({"loginUser"})
public class AttachmentController {
    private static final String LIST_REQUEST_COLUMN_NAME = "name";
    private static final String LIST_REQUEST_COLUMN_PROJECT_NAME = "projectName";
    private static final String LIST_REQUEST_COLUMN_UPLOADER_NAME = "uploaderName";
    private static final String LIST_REQUEST_COLUMN_UPLOAD_TIME = "uploadTime";
    private static final String LIST_REQUEST_COLUMN_REMARK = "remark";
    private static final String LIST_SEARCH_NAME = "name";
    private static final String LIST_SEARCH_PROJECT_NAME = "projectName";
    private static final String LIST_SEARCH_UPLOADER_NAME = "uploaderName";
    private static final String LIST_SEARCH_UPLOAD_TIME = "uploadTime";
    private static final String LIST_SEARCH_REMARK = "remark";

    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String CHINESE_SUITABLE_CHARSET = "iso-8859-1";

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @Value("${file.attachment.location}")
    private String attachmentFileLocation;

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
                case LIST_REQUEST_COLUMN_PROJECT_NAME:
                    orders.add(Pair.of(PROJECT_NAME, order));
                    break;
                case LIST_REQUEST_COLUMN_UPLOADER_NAME:
                    orders.add(Pair.of(UPLOADER_NAME, order));
                    break;
                case LIST_REQUEST_COLUMN_UPLOAD_TIME:
                    orders.add(Pair.of(UPLOAD_TIME, order));
                    break;
                case LIST_REQUEST_COLUMN_REMARK:
                    orders.add(Pair.of(REMARK, order));
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

            if (searchJsonObject.has(LIST_SEARCH_UPLOADER_NAME)) {
                List<String> uploaders = searchJsonObject.getJSONArray(LIST_SEARCH_UPLOADER_NAME).toList().stream().map(Object::toString).collect(Collectors.toList());
                filters.put(Field.UPLOADER_NAME, uploaders);
            }

            if (searchJsonObject.has(LIST_SEARCH_UPLOAD_TIME)) {
                JSONArray searchUploadTimes = searchJsonObject.getJSONArray(LIST_SEARCH_UPLOAD_TIME);
                Date startUploadTime = DateUtils.parse(searchUploadTimes.getString(0)), endUploadTime = DateUtils.parse(searchUploadTimes.getString(1));

                if (startUploadTime.before(endUploadTime)) {
                    filters.put(Field.START_UPLOAD_TIME, startUploadTime);
                    filters.put(Field.END_UPLOAD_TIME, endUploadTime);
                }
            }

            if (searchJsonObject.has(LIST_SEARCH_REMARK)) {
                String remark = searchJsonObject.getString(LIST_SEARCH_REMARK);
                if (StringUtils.isNotEmpty(remark)) {
                    filters.put(Field.REMARK, remark);
                }
            }

            if (searchJsonObject.has(LIST_SEARCH_PROJECT_NAME)) {
                List<String> projects = searchJsonObject.getJSONArray(LIST_SEARCH_PROJECT_NAME).toList().stream().map(item -> item.toString()).collect(Collectors.toList());
                filters.put(Field.PROJECT_NAME, projects);
            }

            if (searchJsonObject.has(LIST_SEARCH_UPLOADER_NAME)) {
                List<String> uploaders = searchJsonObject.getJSONArray(LIST_SEARCH_UPLOADER_NAME).toList().stream().map(Object::toString).collect(Collectors.toList());
                filters.put(Field.UPLOADER_NAME, uploaders);
            }
        }

        return filters;
    }

    @RequestMapping("/list")
    public String list() {
        return "/attachment/list";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload() {
        return "/attachment/upload";
    }

    @RequestMapping(value = "/record/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ToResourceNotFound
    public @ResponseBody
    Map<String, Object> list (
            @RequestParam(value = "request") String request,
            @RequestParam(value = "search") String search,
            @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            JSONObject requestJsonObject = new JSONObject(request);
            int draw = requestJsonObject.getInt(ConstantValue.LIST_REQUEST_DRAW), start = requestJsonObject.getInt(ConstantValue.LIST_REQUEST_START), length = requestJsonObject.getInt(ConstantValue.LIST_REQUEST_LENGTH);
            JSONArray tableColumns = requestJsonObject.getJSONArray(ConstantValue.LIST_REQUEST_COLUMNS);
            JSONArray tableOrders = requestJsonObject.getJSONArray(ConstantValue.LIST_REQUEST_ORDER);

            List<Pair<Field, Order>> orders = orders(tableColumns, tableOrders);
            Map<Field, Object> filters = filters(search);

            List<Attachment> attachments = attachmentRepository.findAttachmentsByUser(loginUser, filters, orders, start, length);
            attachments.forEach(attachment -> {
                try {
                    attachment.setAuthorities(attachmentRepository.findAttachmentAuthorities(attachment, loginUser));
                    attachment.setTag(generalEncoderService.staticEncrypt(attachment.getId()));
                } catch (EncoderServiceException e) {
                    throw new RuntimeException();
                }
            });

            return new HashMap<>() {
                {
                    put("count", attachmentRepository.findAttachmentCountByUser(loginUser, filters));
                    put("draw", draw);
                    put("attachments", attachments);
                }
            };
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> processUpload(@RequestParam("projectTag") String projectTag,
                                                           @RequestParam("remark") String remark,
                                                           @RequestPart("attachmentFile") MultipartFile attachmentFile,
                                                           @ModelAttribute("loginUser") final User loginUser) throws AttachmentUploadException {
        try {
            Project project = projectRepository.findProjectById(Long.parseLong(generalEncoderService.staticDecrypt(projectTag)));
            String dataFileExtension = FilenameUtils.getExtension(attachmentFile.getOriginalFilename());

            if (project.getStatus() != ProjectStatus.PROGRESS) {
                throw new RuntimeException(ExceptionMessage.ATTACHMENT_UPLOAD_PROJECT);
            }

            String file = com.cowerling.pmn.utils.StringUtils.random() + "." + dataFileExtension;
            attachmentFile.transferTo(new File(attachmentFileLocation + file));

            Attachment attachment = new Attachment();
            attachment.setName(FilenameUtils.getBaseName(attachmentFile.getOriginalFilename()));
            attachment.setFile(file);
            attachment.setProject(project);
            attachment.setUploader(loginUser);
            attachment.setRemark(remark);

            attachmentRepository.saveAttachment(attachment);

            attachmentRepository.saveAttachmentAuthorities(attachment, loginUser, new AttachmentAuthority[] {
                    AttachmentAuthority.BASIS,
                    AttachmentAuthority.DOWNLOAD,
                    AttachmentAuthority.DELETE
            });

            List<User> others = project.getMembers();
            others.addAll(Arrays.asList(new User[]{ project.getCreator(), project.getManager(), project.getPrincipal() }));
            others.stream().filter(member -> member.getId() != loginUser.getId()).forEach(other -> attachmentRepository.saveAttachmentAuthorities(attachment, other, new AttachmentAuthority[] {
                    AttachmentAuthority.BASIS,
                    AttachmentAuthority.DOWNLOAD
            }));

            return new HashMap<>() {
                {
                    put("append", true);
                }
            };
        } catch (Exception e) {
            throw new AttachmentUploadException(e.getMessage());
        }
    }

    @RequestMapping(value = "/download/{attachmentTag}")
    public ResponseEntity<byte[]> download(@PathVariable("attachmentTag") String attachmentTag,
                                           @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            Attachment attachment = attachmentRepository.findAttachmentById(Long.parseLong(generalEncoderService.staticDecrypt(attachmentTag)));

            List<AttachmentAuthority> authorities = attachmentRepository.findAttachmentAuthorities(attachment, loginUser);

            if (!authorities.contains(AttachmentAuthority.DOWNLOAD)) {
                throw new RuntimeException();
            }

            File file = new File(attachmentFileLocation + attachment.getFile());
            byte[] bytes = FileUtils.readFileToByteArray(file);

            if (bytes == null) {
                throw new RuntimeException();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData(
                    "attachment",
                    new String((attachment.getName() + "." + FilenameUtils.getExtension(attachment.getFile())).getBytes(DEFAULT_CHARSET), CHINESE_SUITABLE_CHARSET));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);

            return responseEntity;
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/delete/{attachmentTag}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> delete(
            @PathVariable String attachmentTag,
            @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            Attachment attachment = attachmentRepository.findAttachmentById(Long.parseLong(generalEncoderService.staticDecrypt(attachmentTag)));

            List<AttachmentAuthority> authorities = attachmentRepository.findAttachmentAuthorities(attachment, loginUser);

            if (!authorities.contains(AttachmentAuthority.DELETE)) {
                throw new RuntimeException();
            }

            File deleteFile = new File(attachmentFileLocation + attachment.getFile());
            if (deleteFile.exists() && deleteFile.isFile()) {
                deleteFile.delete();
            }

            attachmentRepository.removeAttachment(attachment);

            return new HashMap<>() {
                {
                    put("attachmentTag", attachmentTag);
                }
            };
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
