package mju.chatuniv.helper.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.support.AbstractTestExecutionListener;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
open class IntegrationTest(@LocalServerPort private var port: Int, @Autowired private var jdbcTemplate: JdbcTemplate) : AbstractTestExecutionListener() {


    @BeforeEach
    open fun initRandomPort() {
        RestAssured.port = this.port;
        validateH2Database();
        val truncateAllTablesQuery = jdbcTemplate.queryForList(
                "SELECT CONCAT('TRUNCATE TABLE ', TABLE_NAME, ';') AS q FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
        String::class.java);
        truncateAllTables(truncateAllTablesQuery);
    }

    private fun validateH2Database() {
        jdbcTemplate.queryForObject("SELECT H2VERSION() FROM DUAL", String::class.java)
    }

    private fun truncateAllTables(truncateAllTablesQuery: List<String>) {
        for (truncateQuery: String in truncateAllTablesQuery) {
            jdbcTemplate.execute(truncateQuery);
        }
    }
}
