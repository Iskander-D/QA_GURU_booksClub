package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.EmptyOrNullLogoutResponseModel;
import models.logout.InvalidLogoutTokenModel;
import models.logout.LogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static spec.logOut.LogoutSpec.*;
import static spec.login.LoginSpec.loginRequestSpec;
import static spec.login.LoginSpec.successfulLoginResponseSpec;


public class LogoutTests extends TestBase {

    String username = "qaguru";
    String password = "qaguru123";
    String badToken = "1";
    String emptyToken = "";
    String nullToken = null;           // todo add null token test (same as empty token but with null value)

    @Test
    @DisplayName("Успешный выход")
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
                .spec(successfulLogoutResponseSpec);


    }

    @Test
    @DisplayName("Передан невалидный токен")
    public void invalidTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(badToken);

        InvalidLogoutTokenModel invalidToken = given(loginRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract().as(InvalidLogoutTokenModel.class);

        String expectedDetailError = "Token is invalid";
        String expectedCodeError = "token_not_valid";

        String actualDetailError = invalidToken.detail();
        String actualCodeError = invalidToken.code();

        assertThat(actualDetailError).isEqualTo(expectedDetailError);
        assertThat(actualCodeError).isEqualTo(expectedCodeError);

    }
    @Test
    @DisplayName("Передан пустой токен")
    public void emptyTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(emptyToken);

        EmptyOrNullLogoutResponseModel invalidToken = given(loginRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyOrNullLogoutResponseSpec)
                .extract().as(EmptyOrNullLogoutResponseModel.class);

        String expectedRefreshError = "This field may not be blank.";
        String actualRefreshError = invalidToken.refresh().get(0);

        assertThat(actualRefreshError).isEqualTo(expectedRefreshError);

    }

}
