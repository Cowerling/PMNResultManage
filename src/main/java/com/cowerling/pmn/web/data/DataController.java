package com.cowerling.pmn.web.data;

import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.data.ProjectRepository;
import com.cowerling.pmn.domain.data.DataContent;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.data.DataRecordAuthority;
import com.cowerling.pmn.domain.data.DataRecordCategory;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.DataUploadException;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ExceptionMessage;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.utils.DataUtils;
import com.cowerling.pmn.utils.StringUtils;
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
import org.springframework.http.MediaType;
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
    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @Value("${data.file.location}")
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
    public void processUpload(@RequestParam("projectTag") String projectTag,
                      @RequestParam("dataFile") MultipartFile dataFile,
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

            String file = dataFileLocation + StringUtils.random() + "." + dataFileExtension;
            dataFile.transferTo(new File(dataFileLocation + file));

            DataRecord dataRecord = new DataRecord();
            dataRecord.setName(FilenameUtils.getBaseName(dataFile.getOriginalFilename()));
            dataRecord.setFile(file);
            dataRecord.setProject(project);
            dataRecord.setUploader(loginUser);
            dataRecord.setCategory(dataRecordCategory);

            dataRepository.saveDataRecord(dataRecord);
        } catch (RuntimeException e) {
            throw new DataUploadException(e.getMessage());
        } catch (IOException | EncoderServiceException e) {
            throw new DataUploadException(ExceptionMessage.DATA_UPLOAD_UNKNOWN);
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

            List<? extends DataContent> dataContents = dataRepository.findDataContentsByDataRecord(dataRecord);

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
}
