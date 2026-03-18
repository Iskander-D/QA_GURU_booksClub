package tests;

import models.login.LoginBodyModel;
import models.logout.EmptyOrNullLogoutResponseModel;
import models.logout.InvalidLogoutTokenModel;
import models.logout.LogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


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
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        api.auth.logout(logoutData);

    }

    @Test
    @DisplayName("Передан невалидный токен")
    public void invalidTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(badToken);
        InvalidLogoutTokenModel responceInvalidToken = api.auth.logoutWithInvalidToken(logoutData);


        String expectedDetailError = "Token is invalid";
        String expectedCodeError = "token_not_valid";

        String actualDetailError = responceInvalidToken.detail();
        String actualCodeError = responceInvalidToken.code();

        assertThat(actualDetailError).isEqualTo(expectedDetailError);
        assertThat(actualCodeError).isEqualTo(expectedCodeError);


    }

    @Test
    @DisplayName("Передан пустой токен")
    public void emptyTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(emptyToken);
        EmptyOrNullLogoutResponseModel responceInvalidToken = api.auth.invalidToken(logoutData);


        String expectedRefreshError = "This field may not be blank.";
        String actualRefreshError = responceInvalidToken.refresh().get(0);

        assertThat(actualRefreshError).isEqualTo(expectedRefreshError);

    }

}
