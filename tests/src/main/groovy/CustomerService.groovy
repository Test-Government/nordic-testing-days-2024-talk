import io.qameta.allure.Allure
import io.qameta.allure.Step
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification

class CustomerService {
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

  @Step("Add owner")
  static int addOwnerStep(Map ownerData) {
    return RestAssured.given()
        .filter(new AllureRestAssured())
        .contentType(ContentType.JSON)
        .body(ownerData)
        .when()
        .post("http://localhost:8080/api/customer/owners")
        .then()
        .statusCode(201)
        .extract()
        .path("id")
  }

  @Step("Add owner")
  static int addOwnerStepWithName(Map ownerData) {
    Allure.getLifecycle().updateStep {
      it.name = "Add owner ${ownerData.firstName} ${ownerData.lastName}"
    }
    return RestAssured.given()
        .filter(new AllureRestAssured())
        .contentType(ContentType.JSON)
        .body(ownerData)
        .when()
        .post("http://localhost:8080/api/customer/owners")
        .then()
        .statusCode(201)
        .extract()
        .path("id")
  }

  @Step("Add pet")
  static int addPetStepWithName(Map ownerData, Map petData) {
    Map petTypeNames = [
        "1": "cat",
        "2": "dog",
        "3": "lizard",
        "4": "snake",
        "5": "bird"]
    Allure.getLifecycle().updateStep {
      it.name = "Add pet ${petTypeNames.get(petData.typeId, petData.typeId)} ${petData.name} to owner ${ownerData.firstName} ${ownerData.lastName}"
    }

    return RestAssured.given()
        .filter(new AllureRestAssured())
        .contentType(ContentType.JSON)
        .body(petData)
        .when()
        .post("http://localhost:8080/api/customer/owners/${ownerData.id}/pets")
        .then()
        .statusCode(201)
        .extract()
        .path("id")
  }

  static RequestSpecification findOwnersRequest() {
    RequestSpecification requestSpecification = RestAssured.given()
        .filter(new AllureRestAssuredWithStep())
        .baseUri("http://localhost:8080/api/customer/owners")

    requestSpecification.metaClass.allureStepName = "Find owners"
    return requestSpecification
  }

  static RequestSpecification addPetRequest(ownerId) {
    RequestSpecification requestSpecification = RestAssured.given()
        .filter(new AllureRestAssuredWithStep())
        .contentType(ContentType.JSON)
        .baseUri("http://localhost:8080/api/customer/owners/${ownerId}/pets")

    requestSpecification.metaClass.allureStepName = "Add pet"
    return requestSpecification
  }
}
