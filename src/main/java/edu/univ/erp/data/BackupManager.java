package edu.univ.erp.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class BackupManager {

    private static final Logger log = LoggerFactory.getLogger(BackupManager.class);

    private final String dbUser;
    private final String dbPassword;
    private final String[] databases = {"erp_db", "auth_db"};

    public BackupManager(
            @Value("${app.datasource.auth.username}") String dbUser,
            @Value("${app.datasource.auth.password}") String dbPassword) {
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public boolean backup(File targetFile) {
        try {
            List<String> command = new ArrayList<>();
            command.add("mysqldump");
            command.add("--user=" + dbUser);
            command.add("--password=" + dbPassword);
            command.add("--databases");
            for (String db : databases) command.add(db);
            command.add("--result-file=" + targetFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            int exitCode = pb.start().waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.error("Database backup failed to {}", targetFile.getAbsolutePath(), e);
            return false;
        }
    }

    public boolean restore(File sourceFile) {
        try {
            List<String> command = new ArrayList<>();
            command.add("mysql");
            command.add("--user=" + dbUser);
            command.add("--password=" + dbPassword);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectInput(sourceFile);
            pb.redirectErrorStream(true);

            int exitCode = pb.start().waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.error("Database restore failed from {}", sourceFile.getAbsolutePath(), e);
            return false;
        }
    }
}
