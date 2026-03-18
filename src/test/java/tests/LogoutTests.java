package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.EmptyOrNullLogoutResponseModel;
import models.logout.InvalidLogoutTokenModel;
import models.logout.LogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static spec.logOut.LogoutSpec.*;
import static spec.login.LoginSpec.requestSpec;
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
        SuccessfulLoginResponseModel loginResponse = step("Авторизоваться пользователем и получить токен", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(logoutRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract()
                    .as(SuccessfulLoginResponseModel.class);
        });
        step("Выполнить логаут с refresh токеном", () -> {
            String refreshToken = loginResponse.refresh();
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            given(logoutRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successfulLogoutResponseSpec);

        });
    }

    @Test
    @DisplayName("Передан невалидный токен")
    public void invalidTokenLogoutTest() {
        InvalidLogoutTokenModel invalidToken = step(" Передаем невалидный токен ", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(badToken);
            return given(requestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(invalidLogoutResponseSpec)
                    .extract().as(InvalidLogoutTokenModel.class);
        });

        step("Проверить, ошибку 'Token is invalid '", () -> {
            String expectedDetailError = "Token is invalid";
            String expectedCodeError = "token_not_valid";

            String actualDetailError = invalidToken.detail();
            String actualCodeError = invalidToken.code();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
            assertThat(actualCodeError).isEqualTo(expectedCodeError);
        });
    }

    @Test
    @DisplayName("Передан пустой токен")
    public void emptyTokenLogoutTest() {

        EmptyOrNullLogoutResponseModel invalidToken = step(" Передаем пустой токен ", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(emptyToken);
            return given(requestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(emptyOrNullLogoutResponseSpec)
                    .extract().as(EmptyOrNullLogoutResponseModel.class);
        });
        step("Проверить, что вернулась ошибка: 'This field may not be blank.'", () -> {
            String expectedRefreshError = "This field may not be blank.";
            String actualRefreshError = invalidToken.refresh().get(0);

            assertThat(actualRefreshError).isEqualTo(expectedRefreshError);
        });
    }

}
