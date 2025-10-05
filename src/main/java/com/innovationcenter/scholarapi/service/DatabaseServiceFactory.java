package com.innovationcenter.scholarapi.service;

import com.innovationcenter.scholarapi.service.impl.MySQLDatabaseService;
import com.innovationcenter.scholarapi.service.impl.SingleConnectionDatabaseService;

/**
 * Selects a DatabaseService implementation based on configuration.
 */
public class DatabaseServiceFactory {

    public static DatabaseService create(ConfigurationService configService) {
        String mode = configService.getProperty("DB_MODE");
        if (mode != null && mode.equalsIgnoreCase("single")) {
            return new SingleConnectionDatabaseService(configService);
        }
        return new MySQLDatabaseService(configService);
    }
}
