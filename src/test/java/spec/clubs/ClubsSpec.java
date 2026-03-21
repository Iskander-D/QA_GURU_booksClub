package spec.clubs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class ClubsSpec {

    public static ResponseSpecification successfulGetClubsResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/clubs/successful_get_clubs_response_schema.json"))
            .expectBody("count", notNullValue())
            .expectBody("results", notNullValue())
            .build();

    public static ResponseSpecification unauthorizedCreateClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas/clubs/unauthorized_create_club_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();
}
