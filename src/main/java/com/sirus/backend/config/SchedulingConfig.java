package com.sirus.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Konfiguracija za omogućavanje scheduling-a
    // Cron job-ovi će se izvršavati automatski
}
