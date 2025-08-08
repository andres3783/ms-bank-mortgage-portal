package com.bank.mortgage.portal.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class LoanTypeSeederRunner implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting loan types seeding...");

        if (hasExistingLoanTypes()) {
            log.info("Loan types already exist, skipping seeding");
            return;
        }

        createLoanTypesTable();
        seedLoanTypes();

        log.info("Loan types seeding completed successfully");
    }

    private boolean hasExistingLoanTypes() {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT COUNT(*) FROM loan_types";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            log.warn("Loan types table doesn't exist: {}", e.getMessage());
            return false;
        }
    }

    private void createLoanTypesTable() {
        String createTableQuery = """
            CREATE TABLE loan_types (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                name VARCHAR(100) UNIQUE NOT NULL,
                description TEXT,
                min_amount DECIMAL(15,2) NOT NULL,
                max_amount DECIMAL(15,2) NOT NULL,
                min_interest_rate DECIMAL(6,4) NOT NULL,
                max_interest_rate DECIMAL(6,4) NOT NULL,
                min_term_months INTEGER NOT NULL,
                max_term_months INTEGER NOT NULL,
                processing_fee_rate DECIMAL(6,4),
                is_active BOOLEAN NOT NULL DEFAULT true,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE INDEX IF NOT EXISTS idx_loan_types_active ON loan_types(is_active);
            CREATE INDEX IF NOT EXISTS idx_loan_types_name ON loan_types(name);
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(createTableQuery)) {

            stmt.executeUpdate();
            log.info("Loan types table created successfully");

        } catch (Exception e) {
            log.error("Error creating loan types table: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create loan types table", e);
        }
    }

    private void seedLoanTypes() {
        String insertQuery = """
            INSERT INTO loan_types (name, description, min_amount, max_amount, 
                                  min_interest_rate, max_interest_rate, min_term_months, 
                                  max_term_months, processing_fee_rate, is_active) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Object[][] loanTypesData = {
                {
                        "HOME_PURCHASE",
                        "Mortgage loan for purchasing a new home or property",
                        new BigDecimal("500000"), new BigDecimal("50000000"), // 500K - 50M KES
                        new BigDecimal("8.5000"), new BigDecimal("14.5000"),   // 8.5% - 14.5%
                        60, 360,  // 5 - 30 years
                        new BigDecimal("1.0000"), // 1% processing fee
                        true
                },
                {
                        "HOME_CONSTRUCTION",
                        "Mortgage loan for constructing a new home",
                        new BigDecimal("1000000"), new BigDecimal("30000000"), // 1M - 30M KES
                        new BigDecimal("9.0000"), new BigDecimal("15.0000"),   // 9% - 15%
                        12, 240, // 1 - 20 years
                        new BigDecimal("1.5000"), // 1.5% processing fee
                        true
                },
                {
                        "HOME_IMPROVEMENT",
                        "Loan for home renovation and improvement projects",
                        new BigDecimal("100000"), new BigDecimal("10000000"), // 100K - 10M KES
                        new BigDecimal("10.0000"), new BigDecimal("16.0000"), // 10% - 16%
                        6, 120, // 6 months - 10 years
                        new BigDecimal("2.0000"), // 2% processing fee
                        true
                },
                {
                        "COMMERCIAL_PROPERTY",
                        "Mortgage for commercial real estate investment",
                        new BigDecimal("2000000"), new BigDecimal("100000000"), // 2M - 100M KES
                        new BigDecimal("11.0000"), new BigDecimal("18.0000"),   // 11% - 18%
                        60, 300, // 5 - 25 years
                        new BigDecimal("2.5000"), // 2.5% processing fee
                        true
                },
                {
                        "REFINANCING",
                        "Refinance existing mortgage with better terms",
                        new BigDecimal("500000"), new BigDecimal("50000000"), // 500K - 50M KES
                        new BigDecimal("8.0000"), new BigDecimal("13.5000"),  // 8% - 13.5%
                        60, 360, // 5 - 30 years
                        new BigDecimal("0.5000"), // 0.5% processing fee
                        true
                },
                {
                        "LAND_PURCHASE",
                        "Loan for purchasing undeveloped land",
                        new BigDecimal("300000"), new BigDecimal("20000000"), // 300K - 20M KES
                        new BigDecimal("12.0000"), new BigDecimal("17.0000"), // 12% - 17%
                        12, 180, // 1 - 15 years
                        new BigDecimal("3.0000"), // 3% processing fee
                        true
                }
        };

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {

            for (Object[] loanType : loanTypesData) {
                stmt.setString(1, (String) loanType[0]);        // name
                stmt.setString(2, (String) loanType[1]);        // description
                stmt.setBigDecimal(3, (BigDecimal) loanType[2]); // min_amount
                stmt.setBigDecimal(4, (BigDecimal) loanType[3]); // max_amount
                stmt.setBigDecimal(5, (BigDecimal) loanType[4]); // min_interest_rate
                stmt.setBigDecimal(6, (BigDecimal) loanType[5]); // max_interest_rate
                stmt.setInt(7, (Integer) loanType[6]);          // min_term_months
                stmt.setInt(8, (Integer) loanType[7]);          // max_term_months
                stmt.setBigDecimal(9, (BigDecimal) loanType[8]); // processing_fee_rate
                stmt.setBoolean(10, (Boolean) loanType[9]);     // is_active

                stmt.executeUpdate();
                log.info("Created loan type: {}", loanType[0]);
            }

            log.info("Successfully seeded {} loan types", loanTypesData.length);

        } catch (Exception e) {
            log.error("Error seeding loan types: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to seed loan types", e);
        }
    }
}