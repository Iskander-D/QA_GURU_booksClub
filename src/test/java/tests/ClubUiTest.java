package tests;

import helpers.AuthHelper;
import io.qameta.allure.*;
import models.clubs.ClubCreateBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubReviewModel;
import models.clubs.ClubsListResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testdata.ClubsTestData;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

public class ClubUiTest extends TestBase {

    String username;
    String password;
    String correctUsername;


//    @BeforeEach
//    public void prepareTestData() {
//
//        username = faker.name().firstName();
//        password = "12345";
//        correctUsername = "wilkinso";
//    }


    @Test
    @DisplayName("Проверка, что владелец клуба не может покинуть клуб")
    public void cantLeaveClubAsOwnerTest() {
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));

        ClubModel createdClub = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());
        String clubId = createdClub.id().toString();

        open("/favicon.ico");
        AuthHelper.loginAndSetLocalStorage(registrationResponse, loginResponse);
        open("/clubs/" + clubId);

        clubPage.checkVisible()
                .leaveClub()
                .checkError("Не удалось покинуть клуб");

    }



    @Test
    @DisplayName("Проверка, что владелец клуба не может покинуть клуб (для заранее зарегистрированного пользователя)")
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

    @Test
    @DisplayName("Отзыв, созданный через API, отображается на странице клуба")
    public void reviewCreatedViaApiIsVisibleOnClubPageTest() {
        SuccessfulRegistrationResponseModel registrationResponse =
                api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubModel club = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());
        models.clubs.ClubReviewWriteBodyModel body = ClubsTestData.randomClubReviewBody(club.id());
        api.clubs.createClubReview(body, loginResponse.access());

        open("/favicon.ico");
        AuthHelper.loginAndSetLocalStorage(registrationResponse, loginResponse);
        open("/clubs/" + club.id());

        String snippet = body.review().length() > 120 ? body.review().substring(0, 120) : body.review();
        clubPage.checkVisible()
                .checkReviewSnippetVisible(snippet);


    }


    @Test
    @DisplayName("Проверка получения отзыва клуба по id /clubs/reviews/{id}/")
    public void getAnyReviewClubTest() {
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubReviewModel expectedReview = getAnyExistingReview();
        api.clubs.getClubReviewById(expectedReview.id(), loginResponse.access());


        open("/favicon.ico");
        AuthHelper.loginAndSetLocalStorage(registrationResponse, loginResponse);
        open("/clubs/" + expectedReview.club());

        clubPage.checkReviewerName(expectedReview.user().username());


    }


    @Test
    @DisplayName("Успешная проверка удаление отзыва  DELETE /clubs/reviews/{id}/")
    public void successfulCheckDeleteClubReviewTest() {

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(correctUsername, password));
        ClubModel club = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());
        ClubReviewModel created = api.clubs.createClubReview(
                ClubsTestData.randomClubReviewBody(club.id()),
                loginResponse.access()
        );

        api.clubs.deleteClubReview(created.id(), loginResponse.access());
        open("/favicon.ico");
        AuthHelper.loginAndSetLocalStorage(loginResponse, correctUsername);
        open("/clubs/" + created.id());

        clubPage.checkReviewDeleted("Пока нет отзывов. Будьте первым, кто поделится впечатлениями!");
    }


    @Step("Получить любой существующий отзыв клуба для тестов /clubs/reviews/{id}/")
    private ClubReviewModel getAnyExistingReview() {
        ClubsListResponseModel clubsResponse = api.clubs.getClubs();

        return step("Поиск существующего отзыва клуба для теста", () ->
                clubsResponse.results().stream()
                             .flatMap(club -> club.reviews().stream())
                             .findFirst()
                             .orElseThrow(() -> new AssertionError("В выдаче клубов нет отзывов для проверки /clubs/reviews/{id}/")));
    }


    @Test
    @AllureId("1")
    @Description("Регистрация и логин через API, создание клуба, имитация сессии в localStorage, переход на страницу клуба. " +
            "Ожидается: при попытке выйти из клуба как владелец показывается ошибка «Не удалось покинуть клуб».")
    @DisplayName("[API] Владелец клуба не может покинуть клуб")
    @Severity(SeverityLevel.CRITICAL)
    public void cantLeaveClubAsOwnerNewTest() {
        SuccessfulLoginResponseModel loginResponse = clubPage.openBlankPageWithNewUser();

        ClubModel createdClub = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());
        String clubId = createdClub.id().toString();

        clubPage.openPageById(clubId)
                .leaveClub()
                .checkError("Не удалось покинуть клуб");
    }

    @Test
    @Tag("ui")
    @AllureId("2")
    @Description("Создаётся новый пользователь, открывается /profile, нажимается выход; проверяется отображение страницы входа.")
    @DisplayName("[UI] Успешный выход из системы (старый пользователь)")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulLogoutAsExistingUserTest() {
        profilePage.openBlankPageWithExistingUser();
        profilePage.openPage()
                   .pressLogoutButton()
                   .loginPageShouldBeOpened();
    }

    @Test
    @Tag("ui")
    @AllureId("3")
    @Description("Создаётся новый пользователь, открывается /profile, нажимается выход; проверяется отображение страницы входа.")
    @DisplayName("[UI] Успешный выход из системы (новый пользователь)")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulLogoutAsNewUserTest() {
        profilePage.openBlankPageWithNewUser();
        profilePage.openPage()
                   .pressLogoutButton()
                   .loginPageShouldBeOpened();
    }





}









