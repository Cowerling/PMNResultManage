package com.cowerling.pmn.web.data;

import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.domain.data.*;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.*;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.utils.DataUtils;
import com.cowerling.pmn.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/data")
@SessionAttributes({"loginUser"})
public class DataController {
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String CHINESE_SUITABLE_CHARSET = "iso-8859-1";

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ProjectRepository projectRepository;

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

            if (project == null || project.getMembers().stream().noneMatch(member -> member.getId() == loginUser.getId())) {
                throw new RuntimeException(ExceptionMessage.DATA_UPLOAD_PROJECT_MEMBER);
            }

            DataRecordCategory dataRecordCategory = DataUtils.getDataFileCategory(dataFile);

            if (!projectRepository.findDataRecordCategoriesByProject(project).contains(dataRecordCategory)) {
                throw new RuntimeException(ExceptionMessage.DATA_UPLOAD_DATA_CATEGORY);
            }

            String file = StringUtils.random() + "." + dataFileExtension;
            dataFile.transferTo(new File(dataFileLocation + file));

            DataRecord dataRecord = new DataRecord();
            dataRecord.setName(FilenameUtils.getBaseName(dataFile.getOriginalFilename()));
            dataRecord.setFile(file);
            dataRecord.setProject(project);
            dataRecord.setUploader(loginUser);
            dataRecord.setCategory(dataRecordCategory);

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

            JSONArray valuesJsonArray = new JSONArray();
            JSONArray attributeNamesJsonArray = new JSONArray();

            dataContents.forEach(dataContent -> {
                valuesJsonArray.put(dataContent.values());

                if (attributeNamesJsonArray.length() == 0) {
                    dataContent.attributeNames().forEach(attributeName -> attributeNamesJsonArray.put(attributeName));
                }
            });

            model.addAttribute("attributeNames", attributeNamesJsonArray.toString());
            model.addAttribute("values", valuesJsonArray.toString());

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

            if (dataRecord.getStatus() == DataRecordStatus.UNAUDITED) {
                File file = new File(dataFileLocation + dataRecord.getFile());
                bytes = FileUtils.readFileToByteArray(file);
            } else if (dataRecord.getStatus() == DataRecordStatus.QUALIFIED) {
                List<? extends DataContent> dataContents = dataRepository.findDataContentsByDataRecord(dataRecord);
                bytes = DataUtils.getDataFile(dataRecord.getCategory(), dataContents);
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

    @RequestMapping("/verification")
    public String verification() {
        return "/data/verification";
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

            List<? extends DataContent> dataContents = DataUtils.getDataFileContents(dataRecord, dataFileLocation);

            dataRepository.saveDataContentsByDataRecord(dataRecord, dataContents);
            dataRepository.updateDataRecord(dataRecord);
            dataRepository.saveDataRecordAuthorities(dataRecord, loginUser, new DataRecordAuthority[] {DataRecordAuthority.DELETE});

            return "redirect:/data/verification";
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
