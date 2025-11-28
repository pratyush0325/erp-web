package edu.univ.erp.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupManager {

    private final String dbUser = "root";
    private final String dbPassword = "prabhi12";
    // We backup BOTH databases to ensure User IDs (Auth) and Profiles (ERP) stay in sync.
    private final String[] databases = {"erp_db", "auth_db"};

    public boolean backup(File targetFile) {
        try {
            // Command: mysqldump --user=root --password=... --databases db1 db2 --result-file="path"
            List<String> command = new ArrayList<>();
            command.add("mysqldump");
            command.add("--user=" + dbUser);
            command.add("--password=" + dbPassword);
            command.add("--databases");
            for (String db : databases) command.add(db);
            command.add("--result-file=" + targetFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); // Merge error output
            Process process = pb.start();

            int exitCode = process.waitFor();
            return exitCode == 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restore(File sourceFile) {
        try {
            // Command: mysql --user=root --password=... < "path"
            // Note: ProcessBuilder doesn't support "<" redirection directly easily in all OS.
            // We will read the file and feed it to the process input stream, OR use a shell wrapper.
            // Using list-based command is safer:

            List<String> command = new ArrayList<>();
            command.add("mysql");
            command.add("--user=" + dbUser);
            command.add("--password=" + dbPassword);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectInput(sourceFile); // <--- Key line: Feed the file as input
            pb.redirectErrorStream(true);

            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}