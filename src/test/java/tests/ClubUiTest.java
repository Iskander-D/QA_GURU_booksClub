package tests;

import helpers.AuthHelper;
import models.clubs.ClubCreateBodyModel;
import models.clubs.ClubModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testdata.ClubsTestData;

import static com.codeborne.selenide.Selenide.open;

public class ClubUiTest extends TestBase {

    String username;
    String password;
    String correctUsername;


    @BeforeEach
    public void prepareTestData() {

        username = faker.name().firstName();
        password = "12345";
        correctUsername = "wilkinso";
    }

    @Test
    public void cantLeaveClubAsOwnerTest() {
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));

        ClubCreateBodyModel createBody = ClubsTestData.randomClubCreateBody();
        ClubModel createdClub = api.clubs.createClub(createBody, loginResponse.access());
        String clubId = createdClub.id().toString();

        open("/favicon.ico");
        AuthHelper.loginAndSetLocalStorage(registrationResponse, loginResponse);
        open("/clubs/" + clubId);

        clubPage.checkVisible()
                .leaveClub()
                .checkError("Не удалось покинуть клуб");

    }


    @Test
    public void cantLeaveClubAsClubOwnerWithoutRegistrationTest() {
        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(correctUsername, password));

        ClubCreateBodyModel createBody = ClubsTestData.randomClubCreateBody();
        ClubModel createdClub = api.clubs.createClub(createBody, loginResponse.access());
        String clubId = createdClub.id().toString();

        open("/favicon.ico");
        AuthHelper.loginAndSetLocalStorage(loginResponse, correctUsername);
        open("/clubs/" + clubId);

        clubPage.checkVisible()
                .leaveClub()
                .checkError("Не удалось покинуть клуб");
    }


}









