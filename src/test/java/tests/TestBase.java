package tests;

import api.ApiClient;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;

import java.util.Locale;

public class TestBase {
    ApiClient  api = new ApiClient();
    Faker faker = new Faker(Locale.CANADA);

    @BeforeAll
    public static void setUp() {
//        RestAssured.baseURI = "http://bookclub.qa.guru:8000";
       RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";
    }
}
