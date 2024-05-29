import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class OwnersTest2 {
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

  static int addPet(Integer owner, Map petData) {
    return RestAssured.given()
        .log().all()
        .contentType(ContentType.JSON)
        .body(petData)
        .when()
        .post("http://localhost:8080/api/customer/owners/" + owner + "/pets")
        .then()
        .log().all()
        .statusCode(201)
        .extract()
        .path("id");
  }

  @Test
  public void testFindOwners() {
    int ownerId;
    int petId;

    Map<String, Object> ownerData = new HashMap<>();
    ownerData.put("id", 0);
    ownerData.put("firstName", "Sarah");
    ownerData.put("lastName", "Connor");
    ownerData.put("address", "1823 Doncaster");
    ownerData.put("city", "Los Angeles");
    ownerData.put("telephone", "4085551439");
    ownerId = addOwner(ownerData);

    Map<String, Object> petData = new HashMap<>();
    petData.put("id", 0);
    petData.put("name", "Max");
    petData.put("birthDate", "2024-05-26T21:00:00.000Z");
    petData.put("typeId", "2");
    petId = addPet(ownerId, petData);

    RestAssured.given()
        .log().all()
        .when()
        .get("http://localhost:8080/api/customer/owners")
        .then()
        .log().all()
        .statusCode(200)
        .body("size()", greaterThan(10))
        .rootPath("find { it.id == " + ownerId + "}")
        .body("firstName", is("Sarah"),
            "lastName", is("Connor"),
            "address", is("1823 Doncaster"),
            "city", is("Los Angeles"),
            "telephone", is("4085551439"),
            "pets.size()", is(1),
            "pets[0].id", is(petId),
            "pets[0].name", is("Max"),
            "pets[0].birthDate", is("2024-05-26"),
            "pets[0].type.name", is("dog"));
  }

  @Test
  public void testAddPet() {
    int ownerId;

    Map<String, Object> ownerData = new HashMap<>();
    ownerData.put("id", 0);
    ownerData.put("firstName", "Sarah");
    ownerData.put("lastName", "Connor");
    ownerData.put("address", "1823 Doncaster");
    ownerData.put("city", "Los Angeles");
    ownerData.put("telephone", "4085551439");
    ownerId = addOwner(ownerData);

    Map<String, Object> petData = new HashMap<>();
    petData.put("id", 0);
    petData.put("name", "Max");
    petData.put("birthDate", "2024-05-26T21:00:00.000Z");
    petData.put("typeId", "2");

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
            "name", is(petData.get("name")),
            "birthDate", is(petData.get("birthDate").toString().substring(0, 10) + "T00:00:00.000+00:00"),
            "type.id", is(Integer.valueOf(petData.get("typeId").toString())),
            "type.name", is("dog"));
  }
}
