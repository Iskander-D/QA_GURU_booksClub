package helpers;

import com.codeborne.selenide.Selenide;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.clubs.LocalstorageAuthModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;

public class AuthHelper {

    private static final String STORAGE_KEY = "book_club_auth";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Основной удобный метод — полностью заменяет весь блок с localStorageAuthBody
     */
    public static void loginAndSetLocalStorage(SuccessfulRegistrationResponseModel regResponse,
                                               SuccessfulLoginResponseModel loginResponse) {

        LocalstorageAuthModel.BookClubAuth auth = buildAuthModel(regResponse, loginResponse);
        setLocalStorageAuth(auth);
    }

    /**
     * Перегруженный метод — если у тебя нет registrationResponse
     */
    public static void loginAndSetLocalStorage(SuccessfulLoginResponseModel loginResponse, String username) {
        LocalstorageAuthModel.BookClubAuth auth = buildAuthModel(loginResponse, username);
        setLocalStorageAuth(auth);
    }

    private static LocalstorageAuthModel.BookClubAuth buildAuthModel(SuccessfulRegistrationResponseModel reg,
                                                                     SuccessfulLoginResponseModel login) {
        return new LocalstorageAuthModel.BookClubAuth(
                new LocalstorageAuthModel.User(
                        reg.id(),
                        reg.username(),
                        reg.firstName(),
                        reg.lastName(),
                        reg.email(),
                        reg.remoteAddr()
                ),
                login.access(),
                login.refresh(),
                true
        );
    }

    private static LocalstorageAuthModel.BookClubAuth buildAuthModel(SuccessfulLoginResponseModel login, String username) {
        return new LocalstorageAuthModel.BookClubAuth(
                new LocalstorageAuthModel.User(
                        null,           // id можно взять из loginResponse, если есть
                        username,
                        "",
                        "",
                        username + "@test.com",
                        ""
                ),
                login.access(),
                login.refresh(),
                true
        );
    }

    private static void setLocalStorageAuth(LocalstorageAuthModel.BookClubAuth auth) {
        try {
            String json = objectMapper.writeValueAsString(auth);
            Selenide.localStorage().setItem(STORAGE_KEY, json);
            Selenide.refresh();                    // обязательно!

            System.out.println("✅ Авторизация через API + localStorage выполнена успешно");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении auth в localStorage", e);
        }
    }
}