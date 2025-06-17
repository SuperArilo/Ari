package com.tty.dto.rtp;

import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
public class RtpConfig {
    @Expose
    private boolean enable = true;
    private double min = 300.0;
    private double max = 1500.0;
}
