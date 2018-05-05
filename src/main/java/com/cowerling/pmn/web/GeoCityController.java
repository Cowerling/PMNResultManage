package com.cowerling.pmn.web;

import com.cowerling.pmn.geodata.GeoCityRepository;
import com.cowerling.pmn.geodata.domain.GeoRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RequestMapping("/geocity")
public class GeoCityController {
    @Autowired
    private GeoCityRepository geoCityRepository;

    @RequestMapping(value = "/location", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody double location(@RequestParam(value = "name") String name) throws SQLException {
        /*Point[] points = {new Point(0, 0)};
        MultiPoint location = new MultiPoint(points);
        location.setSrid(4326);
        geoCityRepository.saveLocation(name, location);*/
        return geoCityRepository.getLocationByName(name).getPoint(0).x;
    }

    @RequestMapping(value = "/key", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody GeoRole key(@RequestParam(value = "authkey") String authenticationkey) {
        String[] authenticationkeys = authenticationkey.split("-");

        if (authenticationkeys.length == 2 && authenticationkeys[0].equals(authenticationkeys[1])) {
            return GeoRole.admin();
        } else {
            return null;
        }
    }
}
