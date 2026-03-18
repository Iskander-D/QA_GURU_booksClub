package api;

import io.qameta.allure.Step;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.logout.EmptyOrNullLogoutResponseModel;
import models.logout.InvalidLogoutTokenModel;
import models.logout.LogoutBodyModel;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static spec.logOut.LogoutSpec.*;
import static spec.login.LoginSpec.*;

public class AuthApiClient {

    @Step("Авторизоваться с логином и паролем")
    public SuccessfulLoginResponseModel login(LoginBodyModel loginBody) {
        return given(requestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);
    }

    @Step("Авторизоваться с неверным логином и паролем")
    public WrongCredentialsLoginResponseModel loginWrongCredential(LoginBodyModel loginBody) {
        return given(requestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Авторизация и получение токена")
    public String loginAndGetRefreshToken(LoginBodyModel loginBody) {
        return given(requestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("refresh");
    }

    @Step("Выполнить логаут с refresh токеном")
    public void logout(LogoutBodyModel logoutBodyModel) {
        given(logoutRequestSpec)
                .body(logoutBodyModel)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);

    }

    @Step("Выполнить логаут с устаревшим токеном")
    public InvalidLogoutTokenModel logoutWithInvalidToken(LogoutBodyModel logoutBodyModel) {
        return given(logoutRequestSpec)
                .body(logoutBodyModel)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract().as(InvalidLogoutTokenModel.class);

    }

    @Step("Выполнить логаут с пустым токеном")
    public EmptyOrNullLogoutResponseModel invalidToken(LogoutBodyModel logoutBodyModel) {
        return given(requestSpec)
                .body(logoutBodyModel)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyOrNullLogoutResponseSpec)
                .extract().as(EmptyOrNullLogoutResponseModel.class);
    }
}

