package testdata;

import models.clubs.ClubCreateBodyModel;
import models.clubs.ClubPatchBodyModel;
import models.clubs.ClubUpdateBodyModel;
import net.datafaker.Faker;

public class ClubsTestData {

    private static final Faker FAKER = new Faker();

    public static ClubCreateBodyModel randomClubCreateBody() {
        return new ClubCreateBodyModel(
                "QA Club " + FAKER.number().digits(4),
                FAKER.book().author(),
                2020 + FAKER.number().numberBetween(0, 6),
                FAKER.lorem().sentence(),
                "https://t.me/qa_guru_bookclub"
        );
    }

    public static ClubUpdateBodyModel randomClubPutBody() {
        return new ClubUpdateBodyModel(
                "PUT Club " + FAKER.number().digits(4),
                FAKER.book().author(),
                2018 + FAKER.number().numberBetween(0, 8),
                FAKER.lorem().sentence(),
                "https://t.me/qa_guru_bookclub"
        );
    }

    public static ClubPatchBodyModel randomClubPatchBody() {
        return new ClubPatchBodyModel(
                "PATCH Club " + FAKER.number().digits(4),
                FAKER.lorem().sentence()
        );
    }
}
