package com.bpkadlampungtengah.mikrotik.monitor.resource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mikrotik")
@Slf4j
public class MikrotikResource {
    @Autowired
    Environment env;

    @GetMapping("/ppps")
    public ResponseEntity<List<Map<String, String>>> ppps() throws MikrotikApiException {
        ApiConnection con = ApiConnection.connect(env.getProperty("mikrotik.host"));
        con.login(env.getProperty("mikrotik.username"),env.getProperty("mikrotik.password"));

        List<Map<String, String>> rs = con.execute("/ppp/active/print");

        con.close();

        return ResponseEntity.ok().body(rs);
    }

    @PostMapping("/queue")
    public ResponseEntity<Map<String, String>> queue(@RequestBody RequestQueue request) throws MikrotikApiException {
        ApiConnection con = ApiConnection.connect(env.getProperty("mikrotik.host"));
        con.login(env.getProperty("mikrotik.username"),env.getProperty("mikrotik.password"));

        List<Map<String, String>> rs = con.execute("/queue/simple/print");

        Map<String, String> data = null;

        for (int i = 1; i <= rs.size(); i++) {
            String tempVar = "<" + request.getService() + "-" + request.getName() + ">";
            if (rs.get(i - 1).get("name").equals(tempVar)) {
                data = rs.get(i - 1);
            }
        }

        con.close();

        if (data == null) {
            return ResponseEntity.badRequest().body(data);
        } else {
            return ResponseEntity.ok().body(data);
        }
    }
}

@Data
class RequestQueue {
    private String service;
    private String name;
}