import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import spock.lang.Specification

import java.time.ZonedDateTime

import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.is

@Epic("Customers Service")
class OwnersTest9 extends Specification {

  @Feature("Find all owners")
  @CustomerRequirement("C-FIND-OWNERS-PET-TYPE-NAME")
  def "Given saved pet typeId: #typeId then findOwner returns: #expectedTypeName"() {
    given: "test1"
    Map ownerData = OwnerData.sarahConnor()
    ownerData.id = CustomerService.addOwnerStepWithName(ownerData)
    Map petData = petData()
    petData.typeId = typeId
    petData.id = CustomerService.addPetStepWithName(ownerData, petData)

    when: "test2"
    Response response = RestAssured.given()
        .filter(new AllureRestAssured())
        .when()
        .get("http://localhost:8080/api/customer/owners")

    then:
    Steps.step("Owner ${ownerData.firstName} ${ownerData.lastName} has pet ${expectedTypeName} ${petData.name}") {
      response
          .then()
          .statusCode(200)
          .body("size()", greaterThan(10))
          .rootPath("find { it.id == ${ownerData.id}}")
          .body(
              "pets.size()", is(1),
              "pets[0].id", is(petData.id),
              "pets[0].type.name", is(expectedTypeName))
    }
    where:
    typeId || expectedTypeName
    "1"    || "cat"
    "2"    || "dog"
    "3"    || "lizard"
    "4"    || "snake"
    "5"    || "bird"
    //"6"    || "elephant" //failing test example
  }

  @Feature("Add pet")
  @CustomerRequirement("C-ADD-PET-BIRTHDATE-RANGE")
  def "Given #description(#field: #value) then status code #expectedStatusCode"() {
    given:
    int ownerId = CustomerService.addOwnerStepWithName(OwnerData.sarahConnor())
    Map petData = petData()
    petData[field] = value

    when:
    Response response = RestAssured.given()
        .filter(new AllureRestAssured())
        .contentType(ContentType.JSON)
        .body(petData)
        .when()
        .post("http://localhost:8080/api/customer/owners/${ownerId}/pets")

    then:
    response
        .then()
        .statusCode(expectedStatusCode)

    where:
    description                       | field       | value                                   || expectedStatusCode
    "valid typeId"                    | "typeId"    | "1"                                     || 201
    "invalid typeId"                  | "typeId"    | "7"                                     || 500
    "birthDate in past"               | "birthDate" | "0000-00-00T00:00:00.000Z"              || 201
    "birthDate in future"             | "birthDate" | format(ZonedDateTime.now().plusDays(1)) || 201
    "birthDate too far in the future" | "birthDate" | "100000-05-26T21:00:00.000Z"            || 500
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
