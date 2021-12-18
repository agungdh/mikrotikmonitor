package com.bpkadlampungtengah.mikrotik.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ppp {
    private String localAddress;
    private String remoteAddress;
    private String service;
    private String name;
    private String comment;
    private String uptime;
}