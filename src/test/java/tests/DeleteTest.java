package tests;

import io.restassured.response.Response;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static spec.login.LoginSpec.requestSpec;
import static spec.login.LoginSpec.successfulLoginResponseSpec;
import static spec.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class DeleteTest extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {

        username = faker.name().firstName();
        password = faker.name().firstName();
    }


    @Test
    public void deleteUserTest() {
        step("Register a new user", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        SuccessfulLoginResponseModel loginResponse = step("Get token", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().as(SuccessfulLoginResponseModel.class);
        });

        step("Delete the user", () -> {
            given(requestSpec)
                    .auth().oauth2(loginResponse.access())
                    .when()
                    .delete("/users/me/")
                    .then()
                    .log().all()
                    .statusCode(204)
                    .header("Content-Length", "0");
        });

        Response getResponse = step("Request data deleted user", () ->
                given(requestSpec)
                        .auth().oauth2(loginResponse.access())
                        .when()
                        .get("/users/me/")
                        .then()
                        .log().all()
                        .statusCode(401)
                        .extract().response()
        );

        step("Check body", () -> {
            assertThat(getResponse.jsonPath().getString("detail")).isEqualTo("User not found");

        });

    }
}

