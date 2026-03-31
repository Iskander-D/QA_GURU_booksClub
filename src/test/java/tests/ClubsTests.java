package tests;

import io.qameta.allure.Step;
import models.clubs.*;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testdata.ClubsTestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class ClubsTests extends TestBase {


    private String username;
    private String password;

    @BeforeEach
    public void prepareTestData() {

        username = faker.name().firstName();
        password = faker.credentials().password();
    }

    @Test
    @DisplayName("Успешное получение списка клубов")
    public void successfulGetClubsTest() {
        ClubsListResponseModel response = api.clubs.getClubs();

        assertThat(response).isNotNull();
        assertThat(response.count()).isGreaterThanOrEqualTo(0);
        assertThat(response.results()).isNotNull();
        assertThat(response.results()).hasSize(response.count());
    }


    @Test
    @DisplayName("Неуспешное создание клуба без авторизации")
    public void unauthorizedCreateClubTest() {

        UnauthorizedClubCreateResponseModel createResponse = api.clubs.createClubWithoutAuth(ClubsTestData.randomClubCreateBody());

        step("Проверка текста ошибки", () -> {
            assertThat(createResponse.detail()).isEqualTo("Authentication credentials were not provided.");
        });
    }

    @Test
    @DisplayName("Успешное создание клуба с авторизацией и проверка полей")
    public void successfulCreateClubTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));

        ClubCreateBodyModel createBody = ClubsTestData.randomClubCreateBody();
        ClubModel createdClub = api.clubs.createClub(createBody, loginResponse.access());

        step("Проверка полей созданного клуба", () -> {
            assertThat(createdClub.id()).isGreaterThan(0);
            assertThat(createdClub.bookTitle()).isEqualTo(createBody.bookTitle());
            assertThat(createdClub.bookAuthors()).isEqualTo(createBody.bookAuthors());
            assertThat(createdClub.publicationYear()).isEqualTo(createBody.publicationYear());
            assertThat(createdClub.description()).isEqualTo(createBody.description());
            assertThat(createdClub.telegramChatLink()).isEqualTo(createBody.telegramChatLink());
            assertThat(createdClub.members()).isNotNull();
            assertThat(createdClub.reviews()).isNotNull();
        });
    }

    @Test
    @DisplayName("Повторное создание клуба возвращает новый id")
    public void createClubTwiceDifferentIdTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));

        ClubCreateBodyModel firstBody = ClubsTestData.randomClubCreateBody();
        ClubCreateBodyModel secondBody = ClubsTestData.randomClubCreateBody();

        ClubModel firstClub = api.clubs.createClub(firstBody, loginResponse.access());
        ClubModel secondClub = api.clubs.createClub(secondBody, loginResponse.access());

        step("Проверка, что созданы две разные сущности", () -> {
            assertThat(firstClub.id()).isGreaterThan(0);
            assertThat(secondClub.id()).isGreaterThan(0);
            assertThat(secondClub.id()).isNotEqualTo(firstClub.id());
        });
    }

    @Test
    @DisplayName("Успешное обновление клуба методом PUT")
    public void successfulUpdateClubPutTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubModel createdClub = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());

        ClubUpdateBodyModel putBody = ClubsTestData.randomClubPutBody();
        ClubModel updatedClub = api.clubs.updateClubPut(createdClub.id(), putBody, loginResponse.access());

        step("Проверка полей после PUT", () -> {
            assertThat(updatedClub.id()).isEqualTo(createdClub.id());
            assertThat(updatedClub.bookTitle()).isEqualTo(putBody.bookTitle());
            assertThat(updatedClub.bookAuthors()).isEqualTo(putBody.bookAuthors());
            assertThat(updatedClub.publicationYear()).isEqualTo(putBody.publicationYear());
            assertThat(updatedClub.description()).isEqualTo(putBody.description());
            assertThat(updatedClub.telegramChatLink()).isEqualTo(putBody.telegramChatLink());
        });
    }

    @Test
    @DisplayName("Успешное частичное обновление клуба методом PATCH")
    public void successfulUpdateClubPatchTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubModel createdClub = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());

        ClubPatchBodyModel patchBody = ClubsTestData.randomClubPatchBody();
        ClubModel patchedClub = api.clubs.updateClubPatch(createdClub.id(), patchBody, loginResponse.access());

        step("Проверка измененных полей после PATCH", () -> {
            assertThat(patchedClub.id()).isEqualTo(createdClub.id());
            assertThat(patchedClub.bookTitle()).isEqualTo(patchBody.bookTitle());
            assertThat(patchedClub.description()).isEqualTo(patchBody.description());
        });
    }

    @Test
    @DisplayName("Успешное удаление клуба методом DELETE")
    public void successfulDeleteClubTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubModel createdClub = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());

        api.clubs.deleteClub(createdClub.id(), loginResponse.access());
        ClubsListResponseModel clubsAfterDelete = api.clubs.getClubs();

        step("Проверка, что удаленный клуб отсутствует в выдаче", () -> {
            assertThat(clubsAfterDelete.results())
                    .extracting(ClubModel::id)
                    .doesNotContain(createdClub.id());
        });
    }

    @Test
    @DisplayName("Неуспешное обновление клуба методом PATCH без авторизации")
    public void unauthorizedUpdateClubPatchTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));

        ClubModel createdClub = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());
        ClubPatchBodyModel patchBody = ClubsTestData.randomClubPatchBody();

        UnauthorizedClubCreateResponseModel patchResponse =
                api.clubs.updateClubPatchWithoutAuth(createdClub.id(), patchBody);

        step("Проверка текста ошибки", () -> {
            assertThat(patchResponse.detail()).isEqualTo("Authentication credentials were not provided.");
        });
    }

    @Test
    @DisplayName("Успешное создание отзыва (POST) и обновление (PUT) с рандомными данными")
    public void successfulCreateAndUpdateClubReviewTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubModel club = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());

        ClubReviewWriteBodyModel createBody = ClubsTestData.randomClubReviewBody(club.id());
        ClubReviewModel created = api.clubs.createClubReview(createBody, loginResponse.access());

        step("Проверка полей созданного отзыва", () -> {
            assertThat(created.id()).isGreaterThan(0);
            assertThat(created.club()).isEqualTo(club.id());
            assertThat(created.review()).isEqualTo(createBody.review());
            assertThat(created.assessment()).isEqualTo(createBody.assessment());
            assertThat(created.readPages()).isEqualTo(createBody.readPages());
        });

        ClubReviewWriteBodyModel putBody = ClubsTestData.randomClubReviewBody(club.id());
        ClubReviewModel updated = api.clubs.updateClubReviewPut(created.id(), putBody, loginResponse.access());

        step("Проверка полей после PUT /clubs/reviews/{id}/", () -> {
            assertThat(updated.id()).isEqualTo(created.id());
            assertThat(updated.club()).isEqualTo(club.id());
            assertThat(updated.review()).isEqualTo(putBody.review());
            assertThat(updated.assessment()).isEqualTo(putBody.assessment());
            assertThat(updated.readPages()).isEqualTo(putBody.readPages());
        });
    }

    @Test
    @DisplayName("Успешное удаление отзыва DELETE /clubs/reviews/{id}/")
    public void successfulDeleteClubReviewTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubModel club = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());
        ClubReviewModel created = api.clubs.createClubReview(
                ClubsTestData.randomClubReviewBody(club.id()),
                loginResponse.access()
        );

        api.clubs.deleteClubReview(created.id(), loginResponse.access());

        step("Проверка, что отзыва нет в выдаче клубов", () -> {
            assertThat(api.clubs.getClubs().results().stream()
                                .flatMap(c -> c.reviews().stream())
                                .map(ClubReviewModel::id)
                                .toList())
                    .doesNotContain(created.id());
        });
    }

    @Test
    @DisplayName("Неуспешное обновление отзыва PUT без авторизации")
    public void unauthorizedUpdateClubReviewPutTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubModel club = api.clubs.createClub(ClubsTestData.randomClubCreateBody(), loginResponse.access());
        ClubReviewModel created = api.clubs.createClubReview(
                ClubsTestData.randomClubReviewBody(club.id()),
                loginResponse.access()
        );

        ClubReviewWriteBodyModel putBody = ClubsTestData.randomClubReviewBody(club.id());
        UnauthorizedClubCreateResponseModel response =
                api.clubs.updateClubReviewPutWithoutAuth(created.id(), putBody);

        step("Проверка текста ошибки", () -> {
            assertThat(response.detail()).isEqualTo("Authentication credentials were not provided.");
        });
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
    @DisplayName("Успешное получение отзыва клуба по id с авторизацией")
    public void successfulGetClubReviewByIdWithAuthTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));

        ClubReviewModel expectedReview = getAnyExistingReview();
        ClubReviewModel actualReview = api.clubs.getClubReviewById(expectedReview.id(), loginResponse.access());

        step("Проверка полей полученного отзыва", () -> {
            assertThat(actualReview.id()).isEqualTo(expectedReview.id());
            assertThat(actualReview.club()).isEqualTo(expectedReview.club());
            assertThat(actualReview.user()).isNotNull();
            assertThat(actualReview.user().id()).isGreaterThan(0);
            assertThat(actualReview.user().username()).isNotBlank();
            assertThat(actualReview.review()).isNotBlank();
            assertThat(actualReview.assessment()).isBetween(1, 5);
            assertThat(actualReview.readPages()).isGreaterThanOrEqualTo(0);
        });
    }

    @Test
    @DisplayName("Успешное получение отзыва клуба по id без авторизации")
    public void successfulGetClubReviewByIdWithoutAuthTest() {
        api.users.register(new RegistrationBodyModel(username, password));

        ClubReviewModel expectedReview = getAnyExistingReview();

        ClubReviewModel actualReview = api.clubs.getClubReviewByIdWithoutAuth(expectedReview.id());

        step("Проверка полей полученного отзыва без авторизации", () -> {
            assertThat(actualReview.id()).isEqualTo(expectedReview.id());
            assertThat(actualReview.club()).isEqualTo(expectedReview.club());
            assertThat(actualReview.user()).isNotNull();
            assertThat(actualReview.user().id()).isGreaterThan(0);
            assertThat(actualReview.user().username()).isNotBlank();
            assertThat(actualReview.review()).isNotBlank();
            assertThat(actualReview.assessment()).isBetween(1, 5);
            assertThat(actualReview.readPages()).isGreaterThanOrEqualTo(0);
        });
    }


}
