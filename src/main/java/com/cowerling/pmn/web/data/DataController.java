package com.cowerling.pmn.web.data;

import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.data.UserRepository;
import com.cowerling.pmn.domain.data.*;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.project.ProjectStatus;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.*;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/data")
@SessionAttributes({"loginUser"})
public class DataController {
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String CHINESE_SUITABLE_CHARSET = "iso-8859-1";
    private static final String CHANGE_CONTENT_UPDATES = "updates";
    private static final String CHANGE_CONTENT_INSERTS = "inserts";
    private static final String CHANGE_CONTENT_DELETES = "deletes";

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @Value("${file.data.location}")
    private String dataFileLocation;

    @RequestMapping("/list")
    public String list() {
        return "/data/list";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload() {
        return "/data/upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> processUpload(@RequestParam("projectTag") String projectTag,
                                                           @RequestPart("dataFile") MultipartFile dataFile,
                                                           @ModelAttribute("loginUser") final User loginUser) throws DataUploadException {
        try {
            Project project = projectRepository.findProjectById(Long.parseLong(generalEncoderService.staticDecrypt(projectTag)));
            String dataFileExtension = FilenameUtils.getExtension(dataFile.getOriginalFilename());

            if (project.getStatus() != ProjectStatus.PROGRESS || project.getMembers().stream().noneMatch(member -> member.getId() == loginUser.getId())) {
                throw new RuntimeException(ExceptionMessage.DATA_UPLOAD_PROJECT_MEMBER);
            }

            Map.Entry<DataRecordCategory, Map<GeoUtils.GeoDefine, String>> dataRecordMeta = DataUtils.getDataFileCategory(dataFile);

            if (!projectRepository.findDataRecordCategoriesByProject(project).contains(dataRecordMeta.getKey())) {
                throw new RuntimeException(ExceptionMessage.DATA_UPLOAD_DATA_CATEGORY);
            }

            String file = StringUtils.random() + "." + dataFileExtension;
            dataFile.transferTo(new File(dataFileLocation + file));

            DataRecord dataRecord = new DataRecord();
            dataRecord.setName(FilenameUtils.getBaseName(dataFile.getOriginalFilename()));
            dataRecord.setFile(file);
            dataRecord.setProject(project);
            dataRecord.setUploader(loginUser);
            dataRecord.setCategory(dataRecordMeta.getKey());
            dataRecord.setSourceProJ(dataRecordMeta.getValue().get(GeoUtils.GeoDefine.PROJ));
            dataRecord.setRemark(dataRecordMeta.getValue().get(GeoUtils.GeoDefine.ORIGIN));

            dataRepository.saveDataRecord(dataRecord);

            dataRepository.saveDataRecordAuthorities(dataRecord, loginUser, new DataRecordAuthority[] {
                    DataRecordAuthority.BASIS,
                    DataRecordAuthority.VIEW,
                    DataRecordAuthority.DOWNLOAD
            });

            dataRepository.saveDataRecordAuthorities(dataRecord, project.getPrincipal(), new DataRecordAuthority[] {
                    DataRecordAuthority.BASIS,
                    DataRecordAuthority.VIEW,
                    DataRecordAuthority.DOWNLOAD
            });

            return new HashMap<>() {
                {
                    put("append", true);
                }
            };
        } catch (RuntimeException e) {
            throw new DataUploadException(e.getMessage());
        } catch (IOException | EncoderServiceException e) {
            throw new DataUploadException(ExceptionMessage.DATA_UPLOAD_UNKNOWN);
        } catch (DataParseException e) {
            throw new DataUploadException(ExceptionMessage.DATA_PARSE_CONTENT_INCONFORMITY);
        } catch (Exception e) {
            throw new DataUploadException(ExceptionMessage.DATA_UPLOAD_UNKNOWN);
        }
    }

    @RequestMapping(value = "/view/{dataRecordTag}")
    public String dataContent(@PathVariable("dataRecordTag") String dataRecordTag,
                              @ModelAttribute("loginUser") final User loginUser,
                              Model model) throws ResourceNotFoundException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));
            dataRecord.setTag(dataRecordTag);

            if (dataRecord == null) {
                throw new RuntimeException();
            }

            List<DataRecordAuthority> authorities = dataRepository.findDataRecordAuthorities(dataRecord, loginUser);

            if (!authorities.contains(DataRecordAuthority.VIEW)) {
                throw new RuntimeException();
            }

            List<? extends DataContent> dataContents = dataRecord.getStatus() == DataRecordStatus.QUALIFIED ?
                    dataRepository.findDataContentsByDataRecord(dataRecord) :
                    DataUtils.getDataFileContents(dataRecord, dataFileLocation);

            if (dataContents == null) {
                throw new RuntimeException();
            }

            JSONArray idsJsonArray = new JSONArray();
            JSONArray valuesJsonArray = new JSONArray();
            JSONArray attributeNamesJsonArray = new JSONArray();

            dataContents.forEach(dataContent -> {
                idsJsonArray.put(dataContent.getId());
                valuesJsonArray.put(dataContent.values().stream().map(value -> value instanceof Date ? DateUtils.format((Date) value) : value).collect(Collectors.toList()));

                if (attributeNamesJsonArray.length() == 0) {
                    dataContent.attributeNames().forEach(attributeName -> attributeNamesJsonArray.put(attributeName));
                }
            });

            model.addAttribute(dataRecord);
            model.addAttribute("attributeNames", attributeNamesJsonArray.toString());
            model.addAttribute("ids", idsJsonArray);
            model.addAttribute("values", valuesJsonArray.toString());
            model.addAttribute("editable", authorities.contains(DataRecordAuthority.EDIT));

            return "data/detail";
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/download/{dataRecordTag}")
    public ResponseEntity<byte[]> download(@PathVariable("dataRecordTag") String dataRecordTag,
                                             @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord == null) {
                throw new RuntimeException();
            }

            List<DataRecordAuthority> authorities = dataRepository.findDataRecordAuthorities(dataRecord, loginUser);

            if (!authorities.contains(DataRecordAuthority.DOWNLOAD)) {
                throw new RuntimeException();
            }

            byte[] bytes = null;

            if (dataRecord.getStatus() == DataRecordStatus.QUALIFIED) {
                List<? extends DataContent> dataContents = dataRepository.findDataContentsByDataRecord(dataRecord);
                bytes = DataUtils.getDataFile(dataRecord, dataContents);
            } else {
                File file = new File(dataFileLocation + dataRecord.getFile());
                bytes = FileUtils.readFileToByteArray(file);
            }

            if (bytes == null) {
                throw new RuntimeException();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", new String((dataRecord.getStatus() == DataRecordStatus.UNAUDITED ?
                    dataRecord.getName() + "." + FilenameUtils.getExtension(dataRecord.getFile()) :
                    dataRecord.getName() + "." + DataUtils.EXCEL_XLS_EXTENSION).getBytes(DEFAULT_CHARSET), CHINESE_SUITABLE_CHARSET));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);

            return responseEntity;
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping("/verificationAndAuthority")
    public String verificationAndAuthority() {
        return "/data/verificationAndAuthority";
    }

    @RequestMapping(value = "/verification", method = RequestMethod.POST)
    public String processVerification(String dataRecordTag,
                                    String dataRecordStatus,
                                    String remark,
                                    @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord == null || dataRecord.getProject().getPrincipal().getId() != loginUser.getId()) {
                throw new RuntimeException();
            }

            dataRecord.setStatus(DataRecordStatus.valueOf(dataRecordStatus.toUpperCase()));
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(remark)) {
                dataRecord.setRemark(remark);
            }

            if (dataRecord.getStatus() == DataRecordStatus.QUALIFIED) {
                List<? extends DataContent> dataContents = DataUtils.getDataFileContents(dataRecord, dataFileLocation);

                dataRepository.saveDataContentsByDataRecord(dataRecord, dataContents);
                dataRepository.saveDataRecordAuthorities(dataRecord, loginUser, new DataRecordAuthority[] {
                        DataRecordAuthority.EDIT
                });

                File deleteFile = new File(dataFileLocation + dataRecord.getFile());
                if (deleteFile.exists() && deleteFile.isFile()) {
                    deleteFile.delete();
                }
            }

            dataRepository.updateDataRecord(dataRecord);
            dataRepository.saveDataRecordAuthorities(dataRecord, loginUser, new DataRecordAuthority[] {
                    DataRecordAuthority.DELETE
            });
            dataRepository.saveDataRecordAuthorities(dataRecord, dataRecord.getProject().getCreator(), new DataRecordAuthority[] {
                    DataRecordAuthority.BASIS,
                    DataRecordAuthority.VIEW,
                    DataRecordAuthority.DOWNLOAD
            });
            dataRepository.saveDataRecordAuthorities(dataRecord, dataRecord.getProject().getManager(), new DataRecordAuthority[] {
                    DataRecordAuthority.BASIS,
                    DataRecordAuthority.VIEW,
                    DataRecordAuthority.DOWNLOAD
            });

            return "redirect:/data/verificationAndAuthority";
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/delete/{dataRecordTag}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> delete(
            @PathVariable String dataRecordTag,
            @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord == null) {
                throw new RuntimeException();
            }

            List<DataRecordAuthority> authorities = dataRepository.findDataRecordAuthorities(dataRecord, loginUser);

            if (!authorities.contains(DataRecordAuthority.DELETE)) {
                throw new RuntimeException();
            }

            if (dataRecord.getStatus() == DataRecordStatus.UNQUALIFIED) {
                File deleteFile = new File(dataFileLocation + dataRecord.getFile());
                if (deleteFile.exists() && deleteFile.isFile()) {
                    deleteFile.delete();
                }
            }

            dataRepository.removeDataRecord(dataRecord);

            return new HashMap<>() {
                {
                    put("dataRecordTag", dataRecordTag);
                }
            };
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/edit/{dataRecordTag}", method = RequestMethod.GET)
    public String edit(@PathVariable("dataRecordTag") String dataRecordTag,
                              @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord == null) {
                throw new RuntimeException();
            }

            List<DataRecordAuthority> authorities = dataRepository.findDataRecordAuthorities(dataRecord, loginUser);

            if (!authorities.contains(DataRecordAuthority.EDIT)) {
                throw new RuntimeException();
            }

            return "redirect:/data/view/" + dataRecordTag;
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/edit/{dataRecordTag}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> processEdit(
            @PathVariable String dataRecordTag,
            @RequestParam(value = "changeContent") String changeContent,
            @ModelAttribute("loginUser") final User loginUser) throws DataEditException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord == null) {
                throw new RuntimeException();
            }

            List<DataRecordAuthority> authorities = dataRepository.findDataRecordAuthorities(dataRecord, loginUser);

            if (!authorities.contains(DataRecordAuthority.EDIT)) {
                throw new RuntimeException();
            }

            Class<?> dataContentClass = null;

            switch (dataRecord.getCategory()) {
                case CP0:
                    dataContentClass = CP0DataContent.class;
                    break;
                case CPI_2D:
                    dataContentClass = CPI2DDataContent.class;
                    break;
                case CPI_3D:
                    dataContentClass = CPI3DDataContent.class;
                    break;
                case CPII:
                    dataContentClass = CPIIDataContent.class;
                    break;
                case CPIII:
                    dataContentClass = CPIIIDataContent.class;
                    break;
                case CPII_LE:
                    dataContentClass = CPIILEDataContent.class;
                    break;
                case TSIT:
                    dataContentClass = TSITDataContent.class;
                    break;
                case EC:
                    dataContentClass = ECDataContent.class;
                    break;
                case H3D:
                    dataContentClass = Horizontal3DDataContent.class;
                    break;
                case H2D:
                    dataContentClass = Horizontal2DDataContent.class;
                    break;
                case E:
                    dataContentClass = ElevationDataContent.class;
                    break;
                case CPIII_E:
                    dataContentClass = CPIIIElevationDataContent.class;
                    break;
                default:
                    break;
            }

            List<Method> methods = new ArrayList<>();

            for (String attributeName : ((DataContent) dataContentClass.getConstructor().newInstance()).attributeNames()) {
                methods.add(ClassUtils.getMethod(dataContentClass, "set" + org.apache.commons.lang3.StringUtils.capitalize(attributeName)));
            }

            JSONObject changeContentJsonObject = new JSONObject(changeContent);
            JSONArray updates = changeContentJsonObject.getJSONArray(CHANGE_CONTENT_UPDATES),
                    inserts = changeContentJsonObject.getJSONArray(CHANGE_CONTENT_INSERTS),
                    deletes = changeContentJsonObject.getJSONArray(CHANGE_CONTENT_DELETES);

            for (Object update: updates.toList()) {
                List values = (List) update;

                DataContent dataContent = (DataContent) dataContentClass.getConstructor().newInstance();

                for (int i = 0; i < values.size(); i++) {
                    if (i == 0) {
                        Integer id = (Integer) values.get(i);
                        dataContent.setId((long) id);
                    } else {
                        ClassUtils.invokeMethod(methods.get(i - 1), dataContent, values.get(i));
                    }
                }

                dataRepository.updateDataContent(dataRecord.getCategory(), dataContent);
            }

            for (Object insert: inserts.toList()) {
                List values = (List) insert;

                DataContent dataContent = (DataContent) dataContentClass.getConstructor().newInstance();

                for (int i = 0; i < values.size(); i++) {
                    ClassUtils.invokeMethod(methods.get(i), dataContent, values.get(i));
                }

                dataRepository.saveDataContentByDataRecord(dataRecord, dataContent);
            }

            for (Object delete: deletes.toList()) {
                Integer id = (Integer) delete;
                dataRepository.removeDataContentById(dataRecord.getCategory(), (long) id);
            }

            return new HashMap<>() {
                {
                    put("dataRecordTag", dataRecordTag);
                }
            };
        } catch (Exception e) {
            throw new DataEditException();
        }
    }

    @RequestMapping(value = "/authority/{dataRecordTag}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<DataRecordAuthority> authority(
            @PathVariable String dataRecordTag,
            @RequestParam(value = "userName") String userName,
            @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord == null || dataRecord.getProject().getPrincipal().getId() != loginUser.getId()) {
                throw new RuntimeException();
            }

            return dataRepository.findDataRecordAuthorities(dataRecord, userRepository.findUserByName(userName));
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/authority/{dataRecordTag}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> processAuthority(
            @PathVariable String dataRecordTag,
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "allUser") boolean allUser,
            @RequestParam(value = "authorities[]", required = false) String[] authorities,
            @ModelAttribute("loginUser") final User loginUser) throws DataAuthorityEditException {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord.getProject().getPrincipal().getId() != loginUser.getId()) {
                throw new RuntimeException();
            }

            if (authorities == null) {
                authorities = new String[] {};
            }

            DataRecordAuthority[] dataRecordAuthorities = Arrays.stream(authorities).map(authority -> DataRecordAuthority.valueOf(authority)).toArray(DataRecordAuthority[]::new);

            if (allUser) {
                for (User member : dataRecord.getProject().getMembers()) {
                    dataRepository.updateDataRecordAuthorities(dataRecord, member, dataRecordAuthorities);
                }
            } else {
                dataRepository.updateDataRecordAuthorities(dataRecord, userRepository.findUserByName(userName), dataRecordAuthorities);
            }

            return new HashMap<>() {
                {
                    put("dataRecordTag", dataRecordTag);
                    put("authorities", dataRecordAuthorities);
                }
            };
        } catch (Exception e) {
            throw new DataAuthorityEditException();
        }
    }
}
