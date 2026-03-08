package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.PutUpdateBodyModel;
import models.update.SuccessfulPutUpdateResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static spec.login.LoginSpec.loginRequestSpec;
import static spec.login.LoginSpec.successfulLoginResponseSpec;
import static spec.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static spec.update.UpdateUserSpec.successfulPutUpdateResponseSpec;

public class UpdateUserTests extends TestBase {


    String username;
    String password;
    String firstName;
    String lastName;
    String email;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
    }


    @Test
    public void successfulUpdatePutTest() { //todo сперва логинимся а потом делаем метод Put


        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = given(loginRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);

        int userId = registrationResponse.id(); // записали ID нового пользователя в переменную, чтобы потом удалить его в @AfterEach
        String remoteAddr = registrationResponse.remoteAddr(); // записали IP адрес нового пользователя в переменную, чтобы потом проверить его в ответе метода Put

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String authHeader = "Bearer " + loginResponse.access(); //токен для авторизации в методе Put

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(username, firstName, lastName, email);

        SuccessfulPutUpdateResponseModel putUpdateResponse = given(loginRequestSpec)
                .body(putUpdateBody)
                .header("Authorization", authHeader)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulPutUpdateResponseSpec)
                .extract().as(SuccessfulPutUpdateResponseModel.class);

        //todo add assertions
        int actualId = putUpdateResponse.id();
        String actualUsername = putUpdateResponse.username();
        String actualFirstName = putUpdateResponse.firstName();
        String actualLastName = putUpdateResponse.lastName();
        String actualEmail = putUpdateResponse.email();
        String actualAddr = putUpdateResponse.remoteAddr();

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo(firstName);
        assertThat(actualLastName).isEqualTo(lastName);
        assertThat(actualEmail).isEqualTo(email);
        assertThat(actualAddr).isEqualTo(remoteAddr);



    }

    // todo add update user tests


}

