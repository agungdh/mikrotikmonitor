package com.bpkadlampungtengah.mikrotik.monitor.resource;

import com.bpkadlampungtengah.mikrotik.monitor.domain.*;
import lombok.extern.slf4j.Slf4j;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mikrotik")
@Slf4j
public class MikrotikResource {
    @Autowired
    Environment env;

    @GetMapping("/vpn")
    public ResponseEntity<List<PppStat>> vpnData() throws MikrotikApiException {
        List<Map<String, String>> pppsRaw = this.getArrayData("/ppp/active/print");
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

            Map<String, String> queuesRaw = this.getArrayData("/queue/simple/print where name=\"<" + ppp.getService() + "-" + ppp.getName() + ">\"").get(0);

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

        return ResponseEntity.ok().body(ppps);
    }

    @GetMapping("/interface")
    public ResponseEntity<ResponseInterface> interfaceData() throws MikrotikApiException {
        Map<String, String> indihomeRaw = this.getArrayData("/interface/monitor-traffic interface=ether10 once").get(0);
        InterfaceMonitoring indihome = new InterfaceMonitoring(
                Long.valueOf(indihomeRaw.get("tx-bits-per-second")),
                Long.valueOf(indihomeRaw.get("rx-bits-per-second"))
        );

        Map<String, String> indihomeStatRaw = this.getArrayData("/interface/ethernet/print stats where name=ether10").get(0);
        InterfaceStat indihomeStat = new InterfaceStat(
                Long.valueOf(indihomeStatRaw.get("tx-bytes")),
                Long.valueOf(indihomeStatRaw.get("rx-bytes"))
        );

        Map<String, String> iconRaw = this.getArrayData("/interface/monitor-traffic interface=ether1 once").get(0);
        InterfaceMonitoring icon = new InterfaceMonitoring(
                Long.valueOf(iconRaw.get("tx-bits-per-second")),
                Long.valueOf(iconRaw.get("rx-bits-per-second"))
        );

        Map<String, String> iconStatRaw = this.getArrayData("/interface/ethernet/print stats where name=ether1").get(0);
        InterfaceStat iconStat = new InterfaceStat(
                Long.valueOf(iconStatRaw.get("tx-bytes")),
                Long.valueOf(iconStatRaw.get("rx-bytes"))
        );

        return ResponseEntity.ok().body(new ResponseInterface(icon, indihome, iconStat, indihomeStat));
    }

    private List<Map<String, String>> getArrayData(String query) throws MikrotikApiException {
        ApiConnection con = ApiConnection.connect(env.getProperty("mikrotik.host"));
        con.login(env.getProperty("mikrotik.username"),env.getProperty("mikrotik.password"));

        List<Map<String, String>> rs = con.execute(query);

        con.close();

        return rs;
    }
}

