package com.cowerling.pmn.web.document;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.data.DocumentRepository;
import com.cowerling.pmn.domain.document.Document;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.web.ConstantValue;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/document")
@SessionAttributes({"loginUser"})
public class DocumentController {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @RequestMapping(method = RequestMethod.GET)
    public String list() {
        return "document/list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ToResourceNotFound
    public @ResponseBody Map<String, Object> list(@RequestParam(value = "request") String request) throws ResourceNotFoundException {
        try {
            JSONObject jsonObject = new JSONObject(request);
            int draw = jsonObject.getInt(ConstantValue.LIST_REQUEST_DRAW);

            List<Document> documents = documentRepository.findDocuments();
            documents.forEach(document -> {
                try {
                    document.setTag(generalEncoderService.staticEncrypt(document.getId()));
                } catch (EncoderServiceException e) {
                    throw new RuntimeException();
                }
            });

            Map<String, Object> list = new HashMap<>();
            list.put("draw", draw);
            list.put("documents", documents);

            return list;
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/view/{documentTag}")
    public String content(@PathVariable("documentTag") String documentTag,
                          Model model) throws ResourceNotFoundException {
        try {
            Document document = documentRepository.findDocumentById(Long.parseLong(generalEncoderService.staticDecrypt(documentTag)));

            if (document == null) {
                throw new RuntimeException();
            }

            model.addAttribute("file", document.getFile());

            return "document/view";
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
