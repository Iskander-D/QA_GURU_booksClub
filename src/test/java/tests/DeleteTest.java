package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static spec.login.LoginSpec.requestSpec;
import static spec.login.LoginSpec.successfulLoginResponseSpec;
import static spec.registration.RegistrationSpec.successfulRegistrationResponseSpec;

public class DeleteTest extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
    }


    @Test
    public void deleteUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String authHeader = "Bearer " + loginResponse.access();

        given(requestSpec)
                .header("Authorization", authHeader)
                .when()
                .delete("/users/me/")
                .then()
                .log().all()
//                .log().ifValidationFails()
                .statusCode(204)
                .header("Content-Length", "0");
    }
}
