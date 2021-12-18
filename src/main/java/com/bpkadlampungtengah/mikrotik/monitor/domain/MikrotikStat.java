package com.bpkadlampungtengah.mikrotik.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MikrotikStat {
    private InterfaceMonitoring icon;
    private InterfaceMonitoring indihome;
    private InterfaceStat iconStat;
    private InterfaceStat indihomeStat;
    private List<PppStat> ppps;
}
