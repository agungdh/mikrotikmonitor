package com.bpkadlampungtengah.mikrotik.monitor.resource;

import com.bpkadlampungtengah.mikrotik.monitor.domain.InterfaceMonitoring;
import com.bpkadlampungtengah.mikrotik.monitor.domain.RequestQueue;
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
    public ResponseEntity<InterfaceMonitoring> indihome() throws MikrotikApiException {
        Map<String, String> rs = this.mikrotik("/interface/monitor-traffic interface=ether10 once").get(0);

        InterfaceMonitoring data = new InterfaceMonitoring(
                Long.valueOf(rs.get("tx-bits-per-second")),
                Long.valueOf(rs.get("rx-bits-per-second"))
        );

        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/iface/icon")
    public ResponseEntity<InterfaceMonitoring> icon() throws MikrotikApiException {
        Map<String, String> rs = this.mikrotik("/interface/monitor-traffic interface=ether1 once").get(0);

        InterfaceMonitoring data = new InterfaceMonitoring(
                Long.valueOf(rs.get("tx-bits-per-second")),
                Long.valueOf(rs.get("rx-bits-per-second"))
        );

        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/ppps")
    public ResponseEntity<List<Map<String, String>>> ppps() throws MikrotikApiException {
        return ResponseEntity.ok().body(this.mikrotik("/ppp/active/print"));
    }

    @PostMapping("/queue")
    public ResponseEntity<Map<String, String>> queue(@RequestBody RequestQueue request) throws MikrotikApiException {
       return ResponseEntity.ok().body(this.mikrotik("/queue/simple/print where name=\"<" + request.getService() + "-" + request.getName() + ">\"").get(0));
    }

    private List<Map<String, String>> mikrotik(String query) throws MikrotikApiException {
        ApiConnection con = ApiConnection.connect(env.getProperty("mikrotik.host"));
        con.login(env.getProperty("mikrotik.username"),env.getProperty("mikrotik.password"));

        List<Map<String, String>> rs = con.execute(query);

        con.close();

        return rs;
    }
}

