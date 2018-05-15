package com.cowerling.pmn.web.spatial;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.security.GeneralEncoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("/geoservice")
@SessionAttributes({"loginUser"})
public class GeoServiceController {
    private static final String LEGAL_IPV4_REMOTE_ADDRESS = "127.0.0.1";
    private static final String LEGAL_IPV6_REMOTE_ADDRESS = "0:0:0:0:0:0:0:1";

    @Value("${geoserver.url}")
    private String url;

    @Value("${geoserver.workspace}")
    private String workspace;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @RequestMapping(value = "/server", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody HashMap<String, Object> server(@ModelAttribute("loginUser") final User loginUser) throws EncoderServiceException {
        HashMap<String, Object> server = new HashMap<String, Object>();
        server.put("url", url);
        server.put("workspace", workspace);
        server.put("authkey", generalEncoderService.encrypt(loginUser.getName()));

        return server;
    }

    @RequestMapping(value = "/analyzekey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#request.getRemoteAddr().equals('"+ LEGAL_IPV4_REMOTE_ADDRESS +"') or #request.getRemoteAddr().equals('"+ LEGAL_IPV6_REMOTE_ADDRESS +"')")
    @ToResourceNotFound
    public @ResponseBody String analyzeKey(@RequestParam(value = "authkey") String authenticationKey, HttpServletRequest request) throws EncoderServiceException {
        return String.format("{\"user\":\"%s\"}", generalEncoderService.decrypt(authenticationKey));
    }
}
