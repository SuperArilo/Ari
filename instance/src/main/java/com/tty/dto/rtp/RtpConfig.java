package com.tty.dto.rtp;

import lombok.Data;

@Data
public class RtpConfig {
    private boolean enable = true;
    private double min = 300.0;
    private double max = 1500.0;
}
