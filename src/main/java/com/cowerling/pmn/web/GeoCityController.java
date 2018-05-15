package com.cowerling.pmn.web;

import com.cowerling.pmn.geodata.GeoCityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RequestMapping("/geocity")
public class GeoCityController {
    @Autowired
    private GeoCityRepository geoCityRepository;

    @RequestMapping(value = "/location", method = RequestMethod.GET, produces = "text/plain")
    public @ResponseBody double location(@RequestParam(value = "name") String name) throws SQLException {
        /*Point[] points = {new Point(0, 0)};
        MultiPoint location = new MultiPoint(points);
        location.setSrid(4326);
        geoCityRepository.saveLocation(name, location);*/
        return geoCityRepository.getLocationByName(name).getPoint(0).x;
    }
}
