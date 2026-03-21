package tests;

import models.clubs.ClubCreateBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubsListResponseModel;
import models.clubs.UnauthorizedClubCreateResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class ClubsTests extends TestBase {

    private final Faker faker = new Faker();

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
}
