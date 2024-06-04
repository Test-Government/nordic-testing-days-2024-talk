import groovy.sql.GroovyRowResult
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.response.Response
import spock.lang.Specification

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

@Epic("Customers Service")
class OwnersTest10 extends Specification {

  @Feature("Find all owners")
  @Story("Find all owners given scenario")
  def "Find all owners given scenario #fileName"() {
    given: "test1"
    Map data = TestDataSteps.scenario(fileName)
    List owners = data.owners

    when: "test2"
    Response response = CustomerService.findOwnersRequest().get()

    then:
    Steps.step("Owners list size is ${owners.size()}") {
      response
          .then()
          .statusCode(200)
          .body("size()", is(owners.size()))

      owners.each { ownerData ->
        Steps.step("Owner ${ownerData.first_name} ${ownerData.last_name} has expected contact data") {
          response
              .then()
              .rootPath("find { it.id == $ownerData.id }")
              .body("firstName", is(ownerData.first_name),
                  "lastName", is(ownerData.last_name),
                  "address", is(ownerData.address),
                  "city", is(ownerData.city),
                  "telephone", is(ownerData.telephone),
                  "pets.size()", is(ownerData.pets.size()))
        }
        Steps.step("Owner ${ownerData.first_name} ${ownerData.last_name} has pets ${ownerData.pets.collect { it.name }.join(", ")}") {
          ownerData.pets.each { petData ->
            response
                .then()
                .rootPath("find { it.id == ${ownerData.id}}.pets.find { it.id == ${petData.id}}")
                .body("name", is(petData.name),
                    "birthDate", is(petData.birth_date.toString().split()[0]),
                    "type.name", is(data.types.find { it.id == petData.type_id }.name)
                )
          }
        }
      }
    }
    where:
    fileName                     || _
    "default.yml"                || _
    "george_has_an_elephant.yml" || _
    "long_names.yml"             || _
  }

  @Feature("Add pet")
  @CustomerRequirement("C-ADD-PET-BIRTHDATE-RANGE")
  def "Save to DB"() {
    given:
    Map data = TestData.readData("george_has_an_elephant.yml")
    Integer ownerId = data.owners[0].id
    data.owners[0].pets = []
    TestDataSteps.save(data, "George without elephant")

    when:
    Response response = CustomerService.addPetRequest(ownerId)
        .body([id       : 0,
               name     : "Dumbo",
               birthDate: "2024-05-26T21:00:00.000Z",
               typeId   : "7"
        ]).post()

    then:
    response
        .then()
        .statusCode(201)

    GString statement = "select * from pets where owner_id = $ownerId"
    List<GroovyRowResult> rows = TestData.db.rows(statement)
    TestDataSteps.attach("pets rows for George (id=${ownerId})", statement, rows)
    assertThat(rows.size(), is(1))

    with(rows.first()) {
      assertThat(it.id, is(1L))
      assertThat(it.name, is("Dumbo"))
      assertThat(it.birth_date, is(java.time.LocalDate.of(2024, 05, 26).toDate().toTimestamp()))
      assertThat(it.type_id, is(7L))
    }
  }
}
