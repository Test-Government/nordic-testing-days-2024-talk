import io.qameta.allure.Step
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.http.ContentType

class OwnerService {
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
}
