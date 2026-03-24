package api;

import io.qameta.allure.Step;
import models.clubs.ClubCreateBodyModel;
import models.clubs.ClubPatchBodyModel;
import models.clubs.ClubModel;
import models.clubs.ClubReviewModel;
import models.clubs.ClubUpdateBodyModel;
import models.clubs.ClubsListResponseModel;
import models.clubs.UnauthorizedGetClubReviewResponseModel;
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

    @Step("Обновить клуб методом PUT")
    public ClubModel updateClubPut(int clubId, ClubUpdateBodyModel updateBody, String accessToken) {
        return given(requestSpec)
                .body(updateBody)
                .auth().oauth2(accessToken)
                .when()
                .put("/clubs/" + clubId + "/")
                .then()
                .spec(successfulPutClubResponseSpec)
                .extract().as(ClubModel.class);
    }

    @Step("Обновить клуб методом PATCH")
    public ClubModel updateClubPatch(int clubId, ClubPatchBodyModel patchBody, String accessToken) {
        return given(requestSpec)
                .body(patchBody)
                .auth().oauth2(accessToken)
                .when()
                .patch("/clubs/" + clubId + "/")
                .then()
                .spec(successfulPatchClubResponseSpec)
                .extract().as(ClubModel.class);
    }

    @Step("Попытаться обновить клуб методом PATCH без авторизации")
    public UnauthorizedClubCreateResponseModel updateClubPatchWithoutAuth(int clubId, ClubPatchBodyModel patchBody) {
        return given(requestSpec)
                .body(patchBody)
                .when()
                .patch("/clubs/" + clubId + "/")
                .then()
                .spec(unauthorizedPatchClubResponseSpec)
                .extract().as(UnauthorizedClubCreateResponseModel.class);
    }

    @Step("Удалить клуб")
    public void deleteClub(int clubId, String accessToken) {
        given(requestSpec)
                .auth().oauth2(accessToken)
                .when()
                .delete("/clubs/" + clubId + "/")
                .then()
                .spec(successfulDeleteClubResponseSpec);
    }

    @Step("Получить отзыв по id с авторизацией")
    public ClubReviewModel getClubReviewById(int reviewId, String accessToken) {
        return given(requestSpec)
                .auth().oauth2(accessToken)
                .when()
                .get("/clubs/reviews/" + reviewId + "/")
                .then()
                .spec(successfulGetClubReviewResponseSpec)
                .extract().as(ClubReviewModel.class);
    }

    @Step("Получить отзыв по id без авторизации")
    public ClubReviewModel getClubReviewByIdWithoutAuth(int reviewId) {
        return given(requestSpec)
                .when()
                .get("/clubs/reviews/" + reviewId + "/")
                .then()
                .spec(successfulGetClubReviewResponseSpec)
                .extract().as(ClubReviewModel.class);
    }
}
