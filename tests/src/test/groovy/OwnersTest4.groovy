import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test

import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.is

class OwnersTest4 {
  @Test
  void testFindOwners() {
    RestAssured.given()
        .when()
        .get("http://localhost:8080/api/customer/owners")
        .then()
        .log().all()
        .statusCode(200)
        .body("size()", is(10))
        .rootPath("find { it.id == 1 }")
        .body("firstName", is("George"),
            "lastName", is("Franklin"),
            "address", is("110 W. Liberty St."),
            "city", is("Madison"),
            "telephone", is("6085551023"),
            "pets.size()", is(1),
            "pets[0].name", is("Leo"),
            "pets[0].birthDate", is("2000-09-07"),
            "pets[0].type.name", is("cat"))
  }

  @Test
  void testAddPet() {
    RestAssured.given()
        .contentType(ContentType.JSON)
        .body("""
          {"id":0,
          "name":"test",
          "birthDate":"2024-05-26T21:00:00.000Z",
          "typeId":"5"}
          """)
        .when()
        .post("http://localhost:8080/api/customer/owners/1/pets")
        .then()
        .log().all()
        .statusCode(201)
        .body("id", greaterThan(10),
            "name", is("test"),
            "birthDate", is("2024-05-26T00:00:00.000+00:00"),
            "type.id", is(5),
            "type.name", is("bird"))
  }
}
