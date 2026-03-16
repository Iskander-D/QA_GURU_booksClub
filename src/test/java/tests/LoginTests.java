package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static spec.login.LoginSpec.*;


public class LoginTests extends TestBase {

    String username = "qaguru";
    String password = "qaguru123";
    String wrongPassword = "qaguru1234";

    @Test
    @DisplayName("Успешный логин")
    public void successfulLoginTest() {
        SuccessfulLoginResponseModel loginResponse = step("Ввод правильного логина и правильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().as(SuccessfulLoginResponseModel.class);
        });
        step("Проверка, что access и refresh токены начинаются с правильного пути и не совпадают", () -> {
            String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @DisplayName("Неправильный пароль")
    public void wrongCredentialsLoginTest() {
        WrongCredentialsLoginResponseModel loginResponse = step("Ввод правильного логина и неправильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);
        });

//        step("Проверка, что в ответе возвращается правильная ошибка", () -> { todo норм вариант но можно чище
//        String expectedDetailError = "Invalid username or password.";
//        String actualDetailError = loginResponse.detail();
//
//        assertThat(actualDetailError).isEqualTo(expectedDetailError);
//    });

        step("Проверка, что в ответе возвращается правильная ошибка", () -> {
            assertThat(loginResponse.detail()).isEqualTo("Invalid username or password.");
        });
    }


}
