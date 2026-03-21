package tests;

import models.clubs.ClubCreateBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubsListResponseModel;
import models.clubs.UnauthorizedClubCreateResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class ClubsTests extends TestBase {

    private final Faker faker = new Faker();
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
        ClubCreateBodyModel createBody = new ClubCreateBodyModel(
                faker.book().title(),
                faker.book().author(),
                2024,
                faker.lorem().sentence(),
                "https://t.me/qa_guru_bookclub"
        );

        UnauthorizedClubCreateResponseModel createResponse = api.clubs.createClubWithoutAuth(createBody);

        step("Проверка текста ошибки", () -> {
            assertThat(createResponse.detail()).isEqualTo("Authentication credentials were not provided.");
        });
    }

    @Test
    @DisplayName("Успешное создание клуба с авторизацией")
    public void successfulCreateClubTest() {
        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        ClubCreateBodyModel createBody = new ClubCreateBodyModel(
                faker.book().title(),
                faker.book().author(),
                2025,
                faker.lorem().sentence(),
                "https://t.me/qa_guru_bookclub"
        );

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

        ClubCreateBodyModel firstBody = new ClubCreateBodyModel(
                "QA Club " + faker.number().digits(4),
                faker.book().author(),
                2022,
                faker.lorem().sentence(),
                "https://t.me/qa_guru_bookclub"
        );
        ClubCreateBodyModel secondBody = new ClubCreateBodyModel(
                "QA Club " + faker.number().digits(4),
                faker.book().author(),
                2023,
                faker.lorem().sentence(),
                "https://t.me/qa_guru_bookclub"
        );

        ClubModel firstClub = api.clubs.createClub(firstBody, loginResponse.access());
        ClubModel secondClub = api.clubs.createClub(secondBody, loginResponse.access());

        step("Проверка, что созданы две разные сущности", () -> {
            assertThat(firstClub.id()).isGreaterThan(0);
            assertThat(secondClub.id()).isGreaterThan(0);
            assertThat(secondClub.id()).isNotEqualTo(firstClub.id());
        });
    }
}
