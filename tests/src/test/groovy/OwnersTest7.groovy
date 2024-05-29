import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import spock.lang.Specification

import java.time.ZonedDateTime

import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.is

class OwnersTest7 extends Specification {

  @Feature("Find all owners")
  @Story("Find owner displays pet type name")
  def "Given saved pet typeId: #typeId then findOwner returns: #expectedTypeName"() {
    given: "test1"
    int ownerId = OwnerService.addOwnerStep(OwnerData.sarahConnor())
    Map petData = petData()
    petData.typeId = typeId
    int petId = OwnerService.addPet(ownerId, petData)

    when: "test2"
    Response response = RestAssured.given()
        .filter(new AllureRestAssured())
        .when()
        .get("http://localhost:8080/api/customer/owners")

    then:
    response
        .then()
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

  @Feature("Add pet")
  @Story("Add pet rejects invalid values")
  def "Given add pet field #field: #value then status code #expectedStatusCode"() {
    given:
    int ownerId = OwnerService.addOwnerStep(OwnerData.sarahConnor())
    Map petData = petData()
    petData[field] = value

    when:
    Response response = RestAssured.given()
        .filter(new AllureRestAssured())
        .contentType(ContentType.JSON)
        .body(petData)
        .when()
        .post("http://localhost:8080/api/customer/owners/" + ownerId + "/pets")

    then:
    response
        .then()
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
