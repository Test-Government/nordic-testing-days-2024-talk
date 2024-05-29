import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.Test

import java.time.ZonedDateTime

import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.is

class OwnersTest5 {

  @Test
  void testFindOwners() {
    Map ownerData = [id       : 0,
                     firstName: "Sarah",
                     lastName : "Connor",
                     address  : "1823 Doncaster",
                     city     : "Los Angeles",
                     telephone: "4085551439"
    ]
    Map petData = [id       : 0,
                   name     : "Max",
                   birthDate: "2024-05-26T21:00:00.000Z",
                   typeId   : "2"
    ]
    List testData = [["1", "cat"],
                     ["2", "dog"],
                     ["3", "lizard"],
                     ["4", "snake"],
                     ["5", "bird"]]

    testData.each {
      int ownerId = OwnerService.addOwner(ownerData)

      petData.typeId = it[0]
      int petId = OwnerService.addPet(ownerId, petData)

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
              "pets[0].type.name", is(it[1]))
    }
  }

  @Test
  void testAddPet() {
    int ownerId = OwnerService.addOwner(OwnerData.sarahConnor())

    List testData = [[petData: [typeId: "1"], expectedStatus: 201],
                     [petData: [typeId: "7"], expectedStatus: 500],
                     [petData: [birthDate: "0000-00-00T00:00:00.000Z"], expectedStatus: 201],
                     [petData: [birthDate: format(ZonedDateTime.now().plusDays(1))], expectedStatus: 201],
                     [petData: [birthDate: "100000-05-26T21:00:00.000Z"], expectedStatus: 500]]
    testData.each {
      Map petData = petData() << it.petData
      Response response = RestAssured.given()
          .contentType(ContentType.JSON)
          .log().all()
          .body(petData)
          .when()
          .post("http://localhost:8080/api/customer/owners/" + ownerId + "/pets")
      response
          .then()
          .log().all()
          .statusCode(it.expectedStatus)
    }
  }

  static Map petData() {
    [id       : 0,
     name     : "Max",
     birthDate: "2024-05-26T21:00:00.000Z",
     typeId   : "2"
    ]
  }

  static String format(ZonedDateTime zonedDateTime) {
    zonedDateTime.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }
}
