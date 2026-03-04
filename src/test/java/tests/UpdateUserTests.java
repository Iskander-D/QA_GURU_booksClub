package tests;

import io.restassured.http.ContentType;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.update.UpdateUser;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static spec.LoginSpec.loginRequestSpec;
import static spec.LoginSpec.successfulLoginResponseSpec;

public class UpdateUserTests extends TestBase {

    @Test
    public void successfulPutTest() {
        String username = "qaguru";
        String password = "qaguru123";

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();

        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);

        String newUsername = "ZKjwE5rPGBFn+N";
        UpdateUser updateData = new UpdateUser(newUsername);


        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updateData)
                .when()
                .put("/api/v1/users/me/")
                .then()
                .log().all()
                .statusCode(401)
                .body("username", is(newUsername)) // Проверяем, что в ответе новое имя
                .body("id", notNullValue());
    }

    // todo add update user tests


}

