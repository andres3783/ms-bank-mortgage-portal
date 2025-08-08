package com.bank.mortgage.portal.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataSeederRunner implements CommandLineRunner {

    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String checkQuery = "SELECT COUNT(*) FROM users";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            if (stmt.executeQuery().next() && stmt.getResultSet().getInt(1) > 0) {
                log.info("Users exist, skipping seed");
                return;
            }
        } catch (Exception e) {
            log.info("Users table doesn't exist, creating...");
        }

        createTableAndSeed();
    }

    private void createTableAndSeed() throws Exception {
        String sql = """
            DROP TABLE IF EXISTS users CASCADE;
            CREATE TABLE users (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                username VARCHAR(255) UNIQUE NOT NULL,
                national_id VARCHAR(255) UNIQUE NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                role VARCHAR(50) NOT NULL,
                first_name VARCHAR(255),
                last_name VARCHAR(255),
                is_active BOOLEAN NOT NULL DEFAULT true,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            INSERT INTO users (username, national_id, email, password_hash, role, first_name, last_name) VALUES
            ('admin', '12345678', 'admin@mortgagebank.com', ?, 'ADMIN', 'Admin', 'User'),
            ('officer1', '23456789', 'john.doe@mortgagebank.com', ?, 'OFFICER', 'John', 'Doe'),
            ('officer2', '34567890', 'jane.smith@mortgagebank.com', ?, 'OFFICER', 'Jane', 'Smith'),
            ('applicant1', '45678901', 'michael.johnson@email.com', ?, 'APPLICANT', 'Michael', 'Johnson'),
            ('applicant2', '56789012', 'sarah.williams@email.com', ?, 'APPLICANT', 'Sarah', 'Williams');
            """;

        String password = passwordEncoder.encode("TempPassword123!");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, password);
            }
            stmt.executeUpdate();
            log.info("Created users table and seeded 5 users (password: TempPassword123!)");
        }
    }
}