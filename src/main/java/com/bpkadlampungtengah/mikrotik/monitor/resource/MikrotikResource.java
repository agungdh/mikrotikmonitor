package com.bpkadlampungtengah.mikrotik.monitor.resource;

import com.bpkadlampungtengah.mikrotik.monitor.domain.*;
import lombok.extern.slf4j.Slf4j;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mikrotik")
@Slf4j
public class MikrotikResource {
    @Autowired
    Environment env;

    @GetMapping("/")
    public ResponseEntity<MikrotikStat> mikrotikStat() throws MikrotikApiException {
        Map<String, String> indihomeRaw = this.mikrotik("/interface/monitor-traffic interface=ether10 once").get(0);
        InterfaceMonitoring indihome = new InterfaceMonitoring(
                Long.valueOf(indihomeRaw.get("tx-bits-per-second")) / 8,
                Long.valueOf(indihomeRaw.get("rx-bits-per-second"))/ 8
        );

        Map<String, String> indihomeStatRaw = this.mikrotik("/interface/ethernet/print stats where name=ether10").get(0);
        InterfaceStat indihomeStat = new InterfaceStat(
                Long.valueOf(indihomeStatRaw.get("tx-bytes")),
                Long.valueOf(indihomeStatRaw.get("rx-bytes"))
        );

        Map<String, String> iconRaw = this.mikrotik("/interface/monitor-traffic interface=ether1 once").get(0);
        InterfaceMonitoring icon = new InterfaceMonitoring(
                Long.valueOf(iconRaw.get("tx-bits-per-second")) / 8,
                Long.valueOf(iconRaw.get("rx-bits-per-second")) / 8
        );

        Map<String, String> iconStatRaw = this.mikrotik("/interface/ethernet/print stats where name=ether1").get(0);
        InterfaceStat iconStat = new InterfaceStat(
                Long.valueOf(iconStatRaw.get("tx-bytes")),
                Long.valueOf(iconStatRaw.get("rx-bytes"))
        );

        List<Map<String, String>> pppsRaw = this.mikrotik("/ppp/active/print");
        List<PppStat> ppps = new ArrayList<PppStat>();
        for (int i = 1; i <= pppsRaw.size(); i++) {
            Map<String, String> tempData = pppsRaw.get(i - 1);

            Ppp ppp = new Ppp(
                    tempData.get("address"),
                    tempData.get("caller-id"),
                    tempData.get("service"),
                    tempData.get("name"),
                    tempData.get("comment"),
                    tempData.get("uptime")
            );

            Map<String, String> queuesRaw = this.mikrotik("/queue/simple/print where name=\"<" + ppp.getService() + "-" + ppp.getName() + ">\"").get(0);

            String rate = queuesRaw.get("rate");
            String bytes = queuesRaw.get("bytes");

            String[] rateData = rate.split("/");
            String[] bytesData = bytes.split("/");

            QueueMonitoring queueMonitoring = new QueueMonitoring(
                    Long.valueOf(bytesData[0]),
                    Long.valueOf(bytesData[1]),
                    Long.valueOf(rateData[0]),
                    Long.valueOf(rateData[1])
            );

            ppps.add(new PppStat(ppp, queueMonitoring));
        }

        MikrotikStat mikrotikStat = new MikrotikStat(indihome, icon, indihomeStat, iconStat, ppps);

        return ResponseEntity.ok().body(mikrotikStat);
    }

    @GetMapping("/iface/indihome")
    public ResponseEntity<InterfaceMonitoring> indihome() throws MikrotikApiException {
        Map<String, String> rs = this.mikrotik("/interface/monitor-traffic interface=ether10 once").get(0);

        InterfaceMonitoring data = new InterfaceMonitoring(
                Long.valueOf(rs.get("tx-bits-per-second")) / 8,
                Long.valueOf(rs.get("rx-bits-per-second"))/ 8
        );

        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/iface/stat/indihome")
    public ResponseEntity<InterfaceStat> indihomestat() throws MikrotikApiException {
        Map<String, String> rs = this.mikrotik("/interface/ethernet/print stats where name=ether10").get(0);

        InterfaceStat data = new InterfaceStat(
                Long.valueOf(rs.get("tx-bytes")),
                Long.valueOf(rs.get("rx-bytes"))
        );

        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/iface/icon")
    public ResponseEntity<InterfaceMonitoring> icon() throws MikrotikApiException {
        Map<String, String> rs = this.mikrotik("/interface/monitor-traffic interface=ether1 once").get(0);

        InterfaceMonitoring data = new InterfaceMonitoring(
                Long.valueOf(rs.get("tx-bits-per-second")) / 8,
                Long.valueOf(rs.get("rx-bits-per-second")) / 8
        );

        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/iface/stat/icon")
    public ResponseEntity<InterfaceStat> iconstat() throws MikrotikApiException {
        Map<String, String> rs = this.mikrotik("/interface/ethernet/print stats where name=ether1").get(0);

        InterfaceStat data = new InterfaceStat(
                Long.valueOf(rs.get("tx-bytes")),
                Long.valueOf(rs.get("rx-bytes"))
        );

        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/ppps")
    public ResponseEntity<List<Ppp>> ppps() throws MikrotikApiException {
        List<Map<String, String>> rs = this.mikrotik("/ppp/active/print");

        List<Ppp> ppps = new ArrayList<Ppp>();

        for (int i = 1; i <= rs.size(); i++) {
            Map<String, String> tempData = rs.get(i - 1);

            Ppp ppp = new Ppp(
                tempData.get("address"),
                tempData.get("caller-id"),
                tempData.get("service"),
                tempData.get("name"),
                tempData.get("comment"),
                tempData.get("uptime")
            );

            ppps.add(ppp);
        }

        return ResponseEntity.ok().body(ppps);
    }

    @PostMapping("/queue")
    public ResponseEntity<QueueMonitoring> queue(@RequestBody RequestQueue request) throws MikrotikApiException {
        Map<String, String> rs = this.mikrotik("/queue/simple/print where name=\"<" + request.getService() + "-" + request.getName() + ">\"").get(0);

        String rate = rs.get("rate");
        String bytes = rs.get("bytes");

        String[] rateData = rate.split("/");
        String[] bytesData = bytes.split("/");

        QueueMonitoring data = new QueueMonitoring(
                Long.valueOf(bytesData[0]),
                Long.valueOf(bytesData[1]),
                Long.valueOf(rateData[0]),
                Long.valueOf(rateData[1])
        );

        return ResponseEntity.ok().body(data);
    }

    private List<Map<String, String>> mikrotik(String query) throws MikrotikApiException {
        ApiConnection con = ApiConnection.connect(env.getProperty("mikrotik.host"));
        con.login(env.getProperty("mikrotik.username"),env.getProperty("mikrotik.password"));

        List<Map<String, String>> rs = con.execute(query);

        con.close();

        return rs;
    }
}

