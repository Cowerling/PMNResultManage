package com.cowerling.pmn.web;

import com.cowerling.pmn.geodata.GeoCityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/geocity")
public class GeoCityController {
    @Autowired
    private GeoCityRepository geoCityRepository;

    @RequestMapping(value = "/location", method = RequestMethod.GET, produces = "text/plain")
    public @ResponseBody double location(@RequestParam(value = "name") String name) {
        return geoCityRepository.getLocationByName(name).getPoint(0).x;
    }
}
