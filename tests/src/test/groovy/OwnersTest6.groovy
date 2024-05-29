import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import spock.lang.Specification

import java.time.ZonedDateTime

import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.is

class OwnersTest6 extends Specification {

  def "Given saved pet typeId: #typeId then findOwner returns: #expectedTypeName"() {
    given:
    int ownerId = OwnerService.addOwner(OwnerData.sarahConnor())
    Map petData = petData()
    petData.typeId = typeId
    int petId = OwnerService.addPet(ownerId, petData)

    when:
    Response response =
        RestAssured.given()
            .log().all()
            .when()
            .get("http://localhost:8080/api/customer/owners")

    then:
    response
        .then()
        .log().all()
        .statusCode(200)
        .body("size()", greaterThan(10))
        .rootPath("find { it.id == ${ownerId}}")
        .body(
            "pets.size()", is(1),
            "pets[0].id", is(petId),
            "pets[0].type.name", is(expectedTypeName))

    where:
    typeId || expectedTypeName
    "1"    || "cat"
    "2"    || "dog"
    "3"    || "lizard"
    "4"    || "snake"
    "5"    || "bird"
  }

  def "Given add pet field #field: #value then status code #expectedStatusCode"() {
    given:
    int ownerId = OwnerService.addOwner(OwnerData.sarahConnor())
    Map petData = petData()
    petData[field] = value

    when:
    Response response = RestAssured.given()
        .contentType(ContentType.JSON)
        .log().all()
        .body(petData)
        .when()
        .post("http://localhost:8080/api/customer/owners/" + ownerId + "/pets")

    then:
    response
        .then()
        .log().all()
        .statusCode(expectedStatusCode)

    where:
    field       | value                                   || expectedStatusCode
    "typeId"    | "1"                                     || 201
    "typeId"    | "7"                                     || 500
    "birthDate" | "0000-00-00T00:00:00.000Z"              || 201
    "birthDate" | format(ZonedDateTime.now().plusDays(1)) || 201
    "birthDate" | "100000-05-26T21:00:00.000Z"            || 500
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
