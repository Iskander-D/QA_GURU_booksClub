package spec.logOut;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LogoutSpec {
    public static ResponseSpecification successfulLogoutResponseSpec = new ResponseSpecBuilder().

            log(ALL).
            expectStatusCode(200).
            expectBody(matchesJsonSchemaInClasspath("schemas/logout/successful_logout_response_schema.json")).
            build();

        public static ResponseSpecification invalidLogoutResponseSpec = new ResponseSpecBuilder().
                log(ALL).
                expectStatusCode(401).
                expectBody(matchesJsonSchemaInClasspath("schemas/logout/invalid_token_logout_response_schema.json")).
                expectBody("detail", notNullValue()).
                expectBody("code", notNullValue()).
                build();

    public static ResponseSpecification emptyOrNullLogoutResponseSpec = new ResponseSpecBuilder().
            log(ALL).
            expectStatusCode(400).
            expectBody(matchesJsonSchemaInClasspath("schemas/logout/empty_or_null_logout_response_schema.json")).
            expectBody("refresh", notNullValue()).
            build();
}
