package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static spec.login.LoginSpec.requestSpec;
import static spec.login.LoginSpec.successfulLoginResponseSpec;
import static spec.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static spec.update.UpdateUserSpec.*;

public class UpdateUserTests extends TestBase {


    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String forbiddenUsername;
    String forbiddenExceededUsername;
    String exceededLengthUsername;
    String forbiddenExceededEmail;
    String forbiddenEmail;
    String emptyUsername = "";
    String emptyFirstName = "";
    String emptyLastName = "";
    String emptyEmail = "";

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
        forbiddenUsername = faker.regexify("[\\!=]{5}");
        forbiddenExceededUsername = faker.regexify("[\\!.=]{151}");
        exceededLengthUsername = faker.regexify("[\\w.@+-]{151}");
        forbiddenExceededEmail = faker.regexify("[\\w.@+-]{255}");
        forbiddenEmail = faker.regexify("[\\!.=+-]{5}");
    }


    @Test
    public void successfulUpdatePutTest() { //todo сперва логинимся а потом делаем метод Put
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);

        int userId = registrationResponse.id(); // записали ID нового пользователя в переменную, чтобы потом сравнитть его в ответе метода Put
        String remoteAddr = registrationResponse.remoteAddr(); // записали IP адрес нового пользователя в переменную, чтобы потом проверить его в ответе метода Put

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String actualAccessToken = loginResponse.access();
        String actualRefreshToken = loginResponse.refresh();
        assertThat(actualAccessToken).isNotEqualTo(actualRefreshToken);

//        String authHeader = "Bearer " + loginResponse.access(); //2 dвариант токен для авторизации в методе Put

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(username, firstName, lastName, email);

        SuccessfulPutUpdateResponseModel putUpdateResponse = given(requestSpec)
                .body(putUpdateBody)
                .auth().oauth2(actualAccessToken) // ← автоматически добавит "Authorization: Bearer <token>"
//                .header("Authorization", "Bearer " + actualAccessToken) //1 вариант - токен для авторизации в методе Put из TestBase, который сохраняется в переменную actualAccessToken при каждом логине
//                .header("Authorization", authHeader) //2 вариант
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

    @Test
    public void wrongExceedUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String authHeader = "Bearer " + loginResponse.access(); //токен для авторизации в методе Put

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(forbiddenUsername, exceededLengthUsername,
                exceededLengthUsername, forbiddenExceededEmail);

        WrongOrNoFieldsPutUpdateResponseModel wrongPutUpdateResponse = given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", authHeader)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);


        String actualUsernameError = wrongPutUpdateResponse.username().get(0);
        String actualFirstNameError = wrongPutUpdateResponse.firstName().get(0);
        String actualLastNameError = wrongPutUpdateResponse.lastName().get(0);
        String actualAmountEmailError = wrongPutUpdateResponse.email().get(0);
        String actualFormatEmailError = wrongPutUpdateResponse.email().get(1);
        String expectedUsernameError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
        String expectedFirstNameError = "Ensure this field has no more than 150 characters.";
        String expectedLastNameError = "Ensure this field has no more than 150 characters.";
        String expectedAmountEmailError = "Ensure this field has no more than 254 characters.";
        String expectedFormatEmailError = "Enter a valid email address.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualFirstNameError).isEqualTo(expectedFirstNameError);
        assertThat(actualLastNameError).isEqualTo(expectedLastNameError);
        assertThat(actualAmountEmailError).isEqualTo(expectedAmountEmailError);
        assertThat(actualFormatEmailError).isEqualTo(expectedFormatEmailError);


    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - поля не переданы в тело")
    public void noFieldsProvidedUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String accessToken = "Bearer " + loginResponse.access();

        EmptyPutUpdateBodyModel putUpdateBody = new EmptyPutUpdateBodyModel();

        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse = given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);

        String actualUsernameError = putUpdateResponse.username().get(0);
        String actualFirstNameError = putUpdateResponse.firstName().get(0);
        String actualLastNameError = putUpdateResponse.lastName().get(0);
        String actualFormatEmailError = putUpdateResponse.email().get(0);
        String expectedUsernameError = "This field is required.";
        String expectedFirstNameError = "This field is required.";
        String expectedLastNameError = "This field is required.";
        String expectedFormatEmailError = "This field is required.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualFirstNameError).isEqualTo(expectedFirstNameError);
        assertThat(actualLastNameError).isEqualTo(expectedLastNameError);
        assertThat(actualFormatEmailError).isEqualTo(expectedFormatEmailError);

    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - пустые поля")
    public void emptyFieldsProvidedUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String accessToken = "Bearer " + loginResponse.access();

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(emptyUsername, emptyFirstName, emptyLastName, emptyEmail);

        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse = given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(emptyFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);

        String actualUsernameError = putUpdateResponse.username().get(0);
        String expectedUsernameError = "This field may not be blank.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);

    }

    @Test
    @DisplayName("Успешная замена всех данных методом PATCH")
    public void successfulAllFieldsUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        int userId = registrationResponse.id();
        String remoteAddress = registrationResponse.remoteAddr();

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String accessToken = "Bearer " + loginResponse.access();

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(username, firstName, lastName, email); // модель на заполнение

        SuccessfulPatchUpdateResponseModel patchUpdateResponse = given(requestSpec)
                //..модель на проверку ввода полей
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulPatchUserUpdateSpec)
                .extract()
                .as(SuccessfulPatchUpdateResponseModel.class);

        int actualId = patchUpdateResponse.id();
        String actualUsername = patchUpdateResponse.username();
        String actualFirstName = patchUpdateResponse.firstName();
        String actualLastName = patchUpdateResponse.lastName();
        String actualEmail = patchUpdateResponse.email();
        String actualRemoteAddr = patchUpdateResponse.remoteAddr();

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo(firstName);
        assertThat(actualLastName).isEqualTo(lastName);
        assertThat(actualEmail).isEqualTo(email);
        assertThat(actualRemoteAddr).isEqualTo(remoteAddress);


    }

    @Test
    @DisplayName("Успешная замена только Email методом PATCH")
    public void onlyEmailUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        int userId = registrationResponse.id();
        String remoteAddress = registrationResponse.remoteAddr();

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String accessToken = "Bearer " + loginResponse.access();

        OnlyEmailPatchUpdateBodyModel patchUpdateBody = new OnlyEmailPatchUpdateBodyModel(email);

        SuccessfulPatchUpdateResponseModel patchUpdateResponse = given(requestSpec)
                //..модель на проверку ввода полей

                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulPatchUserUpdateSpec)
                .extract()
                .as(SuccessfulPatchUpdateResponseModel.class);

        int actualId = patchUpdateResponse.id();
        String actualUsername = patchUpdateResponse.username();
        String actualFirstName = patchUpdateResponse.firstName();
        String actualLastName = patchUpdateResponse.lastName();
        String actualEmail = patchUpdateResponse.email();
        String actualRemoteAddr = patchUpdateResponse.remoteAddr();

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo("");
        assertThat(actualLastName).isEqualTo("");
        assertThat(actualEmail).isEqualTo(email);
        assertThat(actualRemoteAddr).isEqualTo(remoteAddress);
    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - переданы пустые строки")
    public void emptyFieldsUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String accessToken = "Bearer " + loginResponse.access();

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(emptyUsername, emptyFirstName, emptyLastName, emptyEmail);

        WrongOrNoFieldsPatchUpdateResponseModel patchUpdateResponse = given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(emptyFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPatchUpdateResponseModel.class);

        String actualUsernameError = patchUpdateResponse.username().get(0);
        String expectedUsernameError = "This field may not be blank.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);


    }

}

