package api;

import io.qameta.allure.Step;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;

import static io.restassured.RestAssured.given;
import static spec.login.LoginSpec.requestSpec;
import static spec.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static spec.update.UpdateUserSpec.*;

public class UsersApiClient {
    @Step("Зарегистрироваться с логином и паролем")
    public SuccessfulRegistrationResponseModel register(RegistrationBodyModel registrationBody) {

        return given(requestSpec)
                .body(registrationBody)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Обновить данные пользователя")
    public SuccessfulPutUpdateResponseModel update(PutUpdateBodyModel putUpdateBody, String token) {

        return given(requestSpec)
                .body(putUpdateBody)
                .auth()
                .oauth2(token) // ← автоматически добавит "Authorization: Bearer <token>" в тесте брем из прошлого ответа
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulPutUpdateResponseSpec)
                .extract().as(SuccessfulPutUpdateResponseModel.class);
    }

    @Step("Обновить данные пользователя с отсутствующими полями")
    public WrongOrNoFieldsPutUpdateResponseModel emptyPutUpdate(EmptyPutUpdateBodyModel putUpdateBody, String token) {

        return given(requestSpec)
                .body(putUpdateBody)
                .auth().oauth2(token)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);

    }

    @Step("Обновить данные пользователя с не верными полями")
    public WrongOrNoFieldsPutUpdateResponseModel wrongNoFilesPutUpdate(PutUpdateBodyModel putUpdateBody, String token) {

        return given(requestSpec)
                .body(putUpdateBody)
                .auth().oauth2(token)
                .when()
                .put("/users/me/")
                .then()
                .spec(emptyFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);

    }

    @Step("Обновить данные пользователя с не верными данными")
    public WrongOrNoFieldsPutUpdateResponseModel wrongBadFilesPutUpdate(PutUpdateBodyModel putUpdateBody, String token) {

        return given(requestSpec)
                .body(putUpdateBody)
                .auth().oauth2(token)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);
    }

    @Step("Обновить все данные пользователя через {patchUpdateBody} Patch")
    public SuccessfulPatchUpdateResponseModel patchUpdate(PatchUpdateBodyModel patchUpdateBody, String token) {

        return given(requestSpec)
                .body(patchUpdateBody)
                .auth().oauth2(token)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulPatchUserUpdateSpec)
                .extract()
                .as(SuccessfulPatchUpdateResponseModel.class);
    }

    @Step("Обновить Email пользователя через {patchUpdateBody} Patch")
    public SuccessfulPatchUpdateResponseModel patchEmailUpdate(OnlyEmailPatchUpdateBodyModel patchUpdateBody, String token) {

        return given(requestSpec)
                .body(patchUpdateBody)
                .auth().oauth2(token)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulPatchUserUpdateSpec)
                .extract()
                .as(SuccessfulPatchUpdateResponseModel.class);
    }

    @Step("Обновить все данные пользователя с пустыми значениями  через {patchUpdateBody} Patch")
    public WrongOrNoFilesPatchResponseModel wrongNoFilesPatch(PatchUpdateBodyModel patchUpdateBody, String token) {
        return given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", token) // todo Если твой сервер НЕ принимает приставку Bearer,
                //todo  то вместо .auth().oauth2(token) в ApiClient используй прямую передачу заголовка:
                .when()
                .patch("/users/me/")
                .then()
                .spec(emptyFieldsPatchSpec)
                .extract().as(WrongOrNoFilesPatchResponseModel.class);
    }

}
