package api;

import io.qameta.allure.Step;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.PutUpdateBodyModel;
import models.update.SuccessfulPutUpdateResponseModel;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static spec.login.LoginSpec.requestSpec;
import static spec.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static spec.update.UpdateUserSpec.successfulPutUpdateResponseSpec;

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

    public SuccessfulPutUpdateResponseModel update(PutUpdateBodyModel putUpdateBody, String token) {

        return given(requestSpec)
                .body(putUpdateBody)
                .auth().oauth2(token) // ← автоматически добавит "Authorization: Bearer <token>" в тесте брем из прошлого ответа
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulPutUpdateResponseSpec)
                .extract().as(SuccessfulPutUpdateResponseModel.class);
    }
}
