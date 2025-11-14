package edu.univ.erp.ui.login;

import org.mindrot.jbcrypt.BCrypt;

public class GenerateHash {
    public static void main(String[] args) {
        // --- CHOOSE YOUR PASSWORDS HERE ---
        String adminPass = "admin123";
        String instPass = "inst123";
        String stu1Pass = "stu123";
        String stu2Pass = "stu123";

        // Generate the hashes
        String adminHash = BCrypt.hashpw(adminPass, BCrypt.gensalt());
        String instHash = BCrypt.hashpw(instPass, BCrypt.gensalt());
        String stu1Hash = BCrypt.hashpw(stu1Pass, BCrypt.gensalt());
        String stu2Hash = BCrypt.hashpw(stu2Pass, BCrypt.gensalt());

        // Print them out
        System.out.println("--- COPY THESE INTO YOUR auth_db.sql FILE ---");
        System.out.println("Admin:      " + adminHash);
        System.out.println("Instructor: " + instHash);
        System.out.println("Student 1:  " + stu1Hash);
        System.out.println("Student 2:  " + stu2Hash);
    }
}