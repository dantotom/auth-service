package it.dtd.note.bdd.support.steps;

import io.cucumber.java.Before;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import it.dtd.note.bdd.support.TestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class AuthSteps {
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;
    private final TestContext ctx;
    @LocalServerPort
    int port;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
    }

    @Dato("il database utenti è vuoto")
    public void db_vuoto() {
        jdbc.update("DELETE FROM users");
    }

    @Dato("esiste un utente con email {string} e password {string}")
    public void seed_utente(String email, String rawPwd) {
        String hash = encoder.encode(rawPwd);
        jdbc.update("""
                  INSERT INTO users(id, email, password_hash, full_name, status, user_role, created_at, updated_at)
                  VALUES (RANDOM_UUID(), ?, ?, 'Alice', 'ACTIVE', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """, email, hash);
    }

    @Quando("effettuo login con {string} e {string}")
    public void login(String email, String pwd) {
        Response res = given().port(port).contentType("application/json")
                .body(Map.of("email", email, "password", pwd))
                .when().post("/api/auth/login").then().extract().response();
        ctx.setLastResponse(res);
    }

    @Allora("lo stato HTTP è {int}")
    public void status(int code) {
        log.debug("Login body: {}", ctx.getLastResponse().asString());
        assertThat(ctx.getLastResponse().statusCode()).isEqualTo(code);
    }

    @Allora("il body contiene un token JWT")
    public void body_token() {
        String raw = ctx.getLastResponse().asString();
        io.restassured.path.json.JsonPath jp = ctx.getLastResponse().jsonPath();

        String token =
                java.util.stream.Stream.of("token", "accessToken", "jwt", "access_token")
                        .map(k -> {
                            try {
                                return jp.getString(k);
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(java.util.Objects::nonNull)
                        .findFirst()
                        .orElse(null);

        assertThat(token).as("Body era: " + raw).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
        ctx.getVars().put("jwt", token);
    }


    @Allora("il subject del token è {string}")
    public void subject(String expected) {
        String token = (String) ctx.getVars().get("jwt");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(token.split("\\.")[1]));
        assertThat(payload).contains("\"sub\":\"" + expected + "\"");
    }
}
