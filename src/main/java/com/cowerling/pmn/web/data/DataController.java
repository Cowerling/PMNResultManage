package com.cowerling.pmn.web.data;

import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.domain.data.DataContent;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.data.DataRecordAuthority;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/data")
@SessionAttributes({"loginUser"})
public class DataController {
    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @RequestMapping("/list")
    public String list() {
        return "/data/list";
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
