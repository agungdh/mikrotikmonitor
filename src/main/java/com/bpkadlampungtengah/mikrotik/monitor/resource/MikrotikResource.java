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

    @GetMapping("/iface/indihome")
    public ResponseEntity<Map<String, String>> indihome() throws MikrotikApiException {
        ApiConnection con = ApiConnection.connect(env.getProperty("mikrotik.host"));
        con.login(env.getProperty("mikrotik.username"),env.getProperty("mikrotik.password"));

        List<Map<String, String>> rs = con.execute("/interface/monitor-traffic interface=ether10 once");

        return ResponseEntity.ok().body(rs.get(0));
    }

    @GetMapping("/iface/icon")
    public ResponseEntity<Map<String, String>> icon() throws MikrotikApiException {
        ApiConnection con = ApiConnection.connect(env.getProperty("mikrotik.host"));
        con.login(env.getProperty("mikrotik.username"),env.getProperty("mikrotik.password"));

        List<Map<String, String>> rs = con.execute("/interface/monitor-traffic interface=ether1 once");

        return ResponseEntity.ok().body(rs.get(0));
    }

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

        Map<String, String> rs = con.execute("/queue/simple/print where name=\"<" + request.getService() + "-" + request.getName() + ">\"").get(0);

        con.close();

        return ResponseEntity.ok().body(rs);
    }
}

@Data
class RequestQueue {
    private String service;
    private String name;
}