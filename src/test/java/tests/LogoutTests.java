package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.LogoutBodyModel;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static spec.LoginSpec.loginRequestSpec;
import static spec.LoginSpec.successfulLoginResponseSpec;


public class LogoutTests extends TestBase {

    String username = "qaguru";
    String password = "qaguru123";

    @Test
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        String refreshToken = loginResponse.refresh();
        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        given(loginRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .log().all()
                .statusCode(200)
                .body(equalTo("{}"));


    }

    // todo add more negative tests
}
