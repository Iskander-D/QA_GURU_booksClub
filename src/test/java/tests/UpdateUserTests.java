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

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        Faker faker = new Faker(Locale.CANADA);
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
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка данных регистрации", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);
        step("Проверка, что access и refresh токены не совпадают", () -> {
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(username, firstName, lastName, email);
        SuccessfulPutUpdateResponseModel updateResponce = api.users.update(putUpdateBody, loginResponse.access());
        step("Проверка обновленных данных", () -> {
            assertThat(updateResponce.username()).isEqualTo(username);
            assertThat(updateResponce.firstName()).isEqualTo(firstName);
            assertThat(updateResponce.lastName()).isEqualTo(lastName);
            assertThat(updateResponce.email()).isEqualTo(email);
        });

    }

    @Test
    public void wrongExceedUpdatePutTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка данных регистрации", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

        });
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);
        step("Проверка, что access и refresh токены не совпадают", () -> {
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(forbiddenUsername, exceededLengthUsername,
                exceededLengthUsername, forbiddenExceededEmail);
        WrongOrNoFieldsPutUpdateResponseModel wrongPutUpdateResponse = api.users.wrongBadFilesPutUpdate(putUpdateBody, loginResponse.access());

        step("Проверка ошибок валидации при превышении допустимых значений", () -> {
            assertAll(
                    () -> assertThat(wrongPutUpdateResponse.username().get(0))
                            .isEqualTo("Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters."),

                    () -> assertThat(wrongPutUpdateResponse.firstName().get(0))
                            .isEqualTo("Ensure this field has no more than 150 characters."),

                    () -> assertThat(wrongPutUpdateResponse.lastName().get(0))
                            .isEqualTo("Ensure this field has no more than 150 characters."),

                    () -> assertThat(wrongPutUpdateResponse.email().get(0))
                            .isEqualTo("Ensure this field has no more than 254 characters.")

            );
        });


    }


    @Test
    @DisplayName("Неуспешная замена данных методом PUT - поля не переданы в тело")
    public void noFieldsProvidedUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка данных регистрации", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);
        step("Проверка, что access и refresh токены не совпадают", () -> {
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });

        EmptyPutUpdateBodyModel putUpdateBody = new EmptyPutUpdateBodyModel();
        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse = api.users.emptyPutUpdate(putUpdateBody, loginResponse.access());

        assertThat(putUpdateResponse.username().get(0)).isEqualTo("This field is required.");
        assertThat(putUpdateResponse.firstName().get(0)).isEqualTo("This field is required.");
        assertThat(putUpdateResponse.lastName().get(0)).isEqualTo("This field is required.");
        assertThat(putUpdateResponse.email().get(0)).isEqualTo("This field is required.");

    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - пустые поля")
    public void emptyFieldsProvidedUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка данных регистрации", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");
        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);
        step("Проверка, что access и refresh токены не совпадают", () -> {
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(emptyUsername, emptyFirstName, emptyLastName, emptyEmail);
        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse = api.users.wrongNoFilesPutUpdate(putUpdateBody, loginResponse.access());
        assertThat(putUpdateResponse.username().get(0)).isEqualTo("This field may not be blank.");

    }

    @Test
    @DisplayName("Успешная замена всех данных методом PATCH")
    public void successfulAllFieldsUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);
        int userId = registrationResponse.id();
        String remoteAddress = registrationResponse.remoteAddr();

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(username, firstName, lastName, email); // модель на заполнение
        SuccessfulPatchUpdateResponseModel patchUpdateResponse = api.users.patchUpdate(patchUpdateBody, loginResponse.access());

        assertThat(patchUpdateResponse.id()).isEqualTo(userId);
        assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        assertThat(patchUpdateResponse.username()).isEqualTo(username);
        assertThat(patchUpdateResponse.firstName()).isEqualTo(firstName);
        assertThat(patchUpdateResponse.lastName()).isEqualTo(lastName);
        assertThat(patchUpdateResponse.email()).isEqualTo(email);
        assertThat(patchUpdateResponse.remoteAddr()).isEqualTo(remoteAddress);


    }

    @Test
    @DisplayName("Успешная замена только Email методом PATCH")
    public void onlyEmailUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);
        int userId = registrationResponse.id();
        String remoteAddress = registrationResponse.remoteAddr();

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        OnlyEmailPatchUpdateBodyModel patchUpdateBody = new OnlyEmailPatchUpdateBodyModel(email);
        SuccessfulPatchUpdateResponseModel patchUpdateResponse = api.users.patchEmailUpdate(patchUpdateBody, loginResponse.access());

        assertThat(patchUpdateResponse.id()).isEqualTo(userId);
        assertThat(patchUpdateResponse.username()).isEqualTo(username);
        assertThat(patchUpdateResponse.firstName()).isEqualTo("");
        assertThat(patchUpdateResponse.lastName()).isEqualTo("");
        assertThat(patchUpdateResponse.email()).isEqualTo(email);
        assertThat(patchUpdateResponse.remoteAddr()).isEqualTo(remoteAddress);

    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - переданы пустые строки")
    public void emptyFieldsUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(emptyUsername, emptyFirstName, emptyLastName, emptyEmail);
        WrongOrNoFilesPatchResponseModel patchUpdateResponse = api.users.wrongNoFilesPatch(patchUpdateBody, loginResponse.access());

        assertThat(patchUpdateResponse.detail()).isEqualTo("Authentication credentials were not provided.");


    }

}

