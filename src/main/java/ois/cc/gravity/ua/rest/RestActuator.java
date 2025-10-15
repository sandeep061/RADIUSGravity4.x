package ois.cc.gravity.ua.rest;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import code.ua.events.*;
import code.ua.requests.Request;
import jakarta.servlet.http.HttpServletRequest;
import ois.cc.gravity.framework.requests.RequestHealthCheck;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins =
        {
                "*"
        }, allowedHeaders =
        {
                "*"
        }, allowCredentials = "false")
@RestController
@RequestMapping("/gravity-api")
public class RestActuator {

    private static Logger logger = LoggerFactory.getLogger(RestActuator.class);
   @GetMapping("/health")
    public ResponseEntity<?> getHealth(HttpServletRequest httprequest){

      return new ResponseEntity<>(HttpStatus.OK);
    }

}
