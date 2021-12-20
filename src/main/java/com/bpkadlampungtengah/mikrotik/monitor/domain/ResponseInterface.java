package com.bpkadlampungtengah.mikrotik.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInterface {
    private InterfaceMonitoring icon;
    private InterfaceMonitoring indihome;
    private InterfaceStat iconStat;
    private InterfaceStat indihomeStat;
}
