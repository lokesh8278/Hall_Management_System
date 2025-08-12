package com.hallbooking.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

    @Configuration // Spring config
    @EntityScan("com.hallbooking.hall_service.entity")
    public class PostGISConfig {

        public void configurePostGIS() {
            Properties properties = new Properties();
            properties.put(AvailableSettings.DIALECT, "org.hibernate.spatial.dialect.postgis.PostgisDialect");
            // PostGIS dialect Hibernate ki chepthundi
        }
    }


