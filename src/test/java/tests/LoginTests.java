package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;


public class LoginTests extends TestBase {

    String username = "qaguru";
    String password = "qaguru123";
    String wrongPassword = "qaguru1234";

    @Test
    @DisplayName("Успешный логин")
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"; //todo вынести в testData
        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();

        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);

    }

    @Test
    @DisplayName("Неправильный пароль")
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);
        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWrongCredential(loginData);

        step("Проверка, что в ответе возвращается правильная ошибка", () -> {
            assertThat(loginResponse.detail()).isEqualTo("Invalid username or password.");
        });
    }


}
