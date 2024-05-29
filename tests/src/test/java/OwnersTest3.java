import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class OwnersTest3 {
  static int addDefaultOwner() {
    Map<String, Object> ownerData = new HashMap<>();
    ownerData.put("id", 0);
    ownerData.put("firstName", "Sarah");
    ownerData.put("lastName", "Connor");
    ownerData.put("address", "1823 Doncaster");
    ownerData.put("city", "Los Angeles");
    ownerData.put("telephone", "4085551439");
    return addOwner(ownerData);
  }

  static int addOwner(Map ownerData) {
    return RestAssured.given()
        .log().all()
        .contentType(ContentType.JSON)
        .body(ownerData)
        .when()
        .post("http://localhost:8080/api/customer/owners")
        .then()
        .log().all()
        .statusCode(201)
        .extract()
        .path("id");
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/pets.csv", numLinesToSkip = 1)
  public void testAddPet(String name, String birthDate, String typeId, String expectedName, String expectedBirthDate, String expectedTypeId, String expectedTypeName) {
    int ownerId = addDefaultOwner();

    Map<String, Object> petData = new HashMap<>();
    petData.put("id", 0);
    petData.put("name", name);
    petData.put("birthDate", birthDate);
    petData.put("typeId", typeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .log().all()
        .body(petData)
        .when()
        .post("http://localhost:8080/api/customer/owners/" + ownerId + "/pets")
        .then()
        .log().all()
        .statusCode(201)
        .body("id",
            greaterThan(10),
            "name", is(expectedName),
            "birthDate", is(expectedBirthDate),
            "type.id", is(Integer.valueOf(expectedTypeId)),
            "type.name", is(expectedTypeName));
  }
}
