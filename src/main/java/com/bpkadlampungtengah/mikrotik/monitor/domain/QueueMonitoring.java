package com.bpkadlampungtengah.mikrotik.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueMonitoring {
    private Long upload;
    private Long download;
    private Long uploading;
    private Long downloading;
}