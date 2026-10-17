package tests;

import api.ApiClient;
import clubPages.ClubPage;
import clubPages.ProfilePage;
import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;

import java.util.Locale;

public class TestBase {
    ApiClient api = new ApiClient();
    ClubPage clubPage = new ClubPage();
    ProfilePage profilePage = new ProfilePage();
    Faker faker = new Faker(Locale.CANADA);


    @BeforeAll
    public static void setUp() {
        Configuration.baseUrl = "https://book-club.qa.guru";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";
//        Configuration.holdBrowserOpen = true;
        RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";
    }
}
