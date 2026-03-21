package api;

import io.qameta.allure.Step;
import models.clubs.ClubCreateBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubsListResponseModel;
import models.clubs.UnauthorizedClubCreateResponseModel;

import static io.restassured.RestAssured.given;
import static spec.clubs.ClubsSpec.*;
import static spec.login.LoginSpec.requestSpec;

public class ClubsApiClient {

    @Step("Получить список клубов")
    public ClubsListResponseModel getClubs() {
        return given(requestSpec)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulGetClubsResponseSpec)
                .extract().as(ClubsListResponseModel.class);
    }

    @Step("Попытаться создать клуб без авторизации")
    public UnauthorizedClubCreateResponseModel createClubWithoutAuth(ClubCreateBodyModel clubCreateBodyModel) {
        return given(requestSpec)
                .body(clubCreateBodyModel)
                .when()
                .post("/clubs/")
                .then()
                .spec(unauthorizedCreateClubResponseSpec)
                .extract().as(UnauthorizedClubCreateResponseModel.class);
    }

    @Step("Создать клуб с авторизацией")
    public ClubModel createClub(ClubCreateBodyModel clubCreateBodyModel, String accessToken) {
        return given(requestSpec)
                .body(clubCreateBodyModel)
                .auth().oauth2(accessToken)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract().as(ClubModel.class);
    }
}
