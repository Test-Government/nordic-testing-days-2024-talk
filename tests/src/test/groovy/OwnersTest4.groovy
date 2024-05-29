import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test

import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.is

//TODO: String literal
class OwnersTest4 {
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
        .path("id")
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
        .path("id")
  }

  @Test
  void testFindOwners() {
    Map ownerData = [id       : 0,
                     firstName: "Sarah",
                     lastName : "Connor",
                     address  : "1823 Doncaster",
                     city     : "Los Angeles",
                     telephone: "4085551439"
    ]

    int ownerId = addOwner(ownerData)
    Map petData = [id       : 0,
                   name     : "Max",
                   birthDate: "2024-05-26T21:00:00.000Z",
                   typeId   : "2"
    ]

    int petId = addPet(ownerId, petData)

    RestAssured.given()
        .log().all()
        .when()
        .get("http://localhost:8080/api/customer/owners")
        .then()
        .log().all()
        .statusCode(200)
        .body("size()", greaterThan(10))
        .rootPath("find { it.id == ${ownerId}}")
        .body("firstName", is("Sarah"),
            "lastName", is("Connor"),
            "address", is("1823 Doncaster"),
            "city", is("Los Angeles"),
            "telephone", is("4085551439"),
            "pets.size()", is(1),
            "pets[0].id", is(petId),
            "pets[0].name", is("Max"),
            "pets[0].birthDate", is("2024-05-26"),
            "pets[0].type.name", is("dog"))
  }

  @Test
  void testAddPet() {
    Map ownerData = [id       : 0,
                     firstName: "Sarah",
                     lastName : "Connor",
                     address  : "1823 Doncaster",
                     city     : "Los Angeles",
                     telephone: "4085551439"
    ]
    int ownerId = addOwner(ownerData)

    Map petData = [id       : 0,
                   name     : "Max",
                   birthDate: "2024-05-26T21:00:00.000Z",
                   typeId   : "2"
    ]

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
            "name", is(petData.name),
            "birthDate", is(petData.birthDate - "T21:00:00.000Z" + "T00:00:00.000+00:00"),
            "type.id", is(petData.typeId.toInteger()),
            "type.name", is("dog"))
  }
}
