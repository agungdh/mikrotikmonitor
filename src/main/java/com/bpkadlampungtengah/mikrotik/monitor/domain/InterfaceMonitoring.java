package com.bpkadlampungtengah.mikrotik.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceMonitoring {
    private Long upload;
    private Long download;
}