package tests;

import models.clubs.*;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testdata.ClubsTestData;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class ClubsTests extends TestBase {


    private final String username = "qaguru";
    private final String password = "qaguru123";

    @Test
    @DisplayName("Успешное получение списка клубов")
    public void successfulGetClubsTest() {
        ClubsListResponseModel clubsResponse = api.clubs.getClubs();

        step("Проверка пагинации", () -> {
            assertThat(clubsResponse.count()).isGreaterThanOrEqualTo(0);
            assertThat(clubsResponse.results()).isNotNull();
        });

        if (!clubsResponse.results().isEmpty()) {
            ClubModel firstClub = clubsResponse.results().get(0);

            step("Проверка структуры первого клуба", () -> {
                assertThat(firstClub.id()).isGreaterThan(0);
                assertThat(firstClub.bookTitle()).isNotBlank();
                assertThat(firstClub.bookAuthors()).isNotBlank();
                assertThat(firstClub.telegramChatLink()).startsWith("http");
                assertThat(firstClub.members()).isNotNull();
                assertThat(firstClub.reviews()).isNotNull();
            });
        }
    }

    @Test
    @DisplayName("Неуспешное создание клуба без авторизации")
    public void unauthorizedCreateClubTest() {
        ClubCreateBodyModel createBody = ClubsTestData.randomClubCreateBody();

        UnauthorizedClubCreateResponseModel createResponse = api.clubs.createClubWithoutAuth(createBody);

        step("Проверка текста ошибки", () -> {
            assertThat(createResponse.detail()).isEqualTo("Authentication credentials were not provided.");
        });
    }

    @Test
    @DisplayName("Успешное создание клуба с авторизацией и проверка полей")
    public void successfulCreateClubTest() {
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
    @DisplayName("Успешное получение отзыва клуба по id с авторизацией")
    public void successfulGetClubReviewByIdWithAuthTest() {
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
    @DisplayName("Неуспешное получение отзыва клуба по id без авторизации")
    public void unauthorizedGetClubReviewByIdTest() {
        ClubReviewModel existingReview = getAnyExistingReview();

        UnauthorizedGetClubReviewResponseModel response =
                api.clubs.getClubReviewByIdWithoutAuth(existingReview.id());

        step("Проверка текста ошибки", () -> {
            assertThat(response.detail()).isEqualTo("Authentication credentials were not provided.");
        });
    }

    private ClubReviewModel getAnyExistingReview() {
        ClubsListResponseModel clubsResponse = api.clubs.getClubs();

        return step("Поиск существующего отзыва клуба для теста", () -> clubsResponse.results().stream()
                .flatMap(club -> club.reviews().stream())
                .findFirst()
                .orElseThrow(() -> new AssertionError("В выдаче клубов нет отзывов для проверки /clubs/reviews/{id}/")));
    }
}
