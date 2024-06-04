import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.filter.FilterContext
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification

class AllureRestAssuredWithStep extends AllureRestAssured {
  @Override
  Response filter(final FilterableRequestSpecification requestSpec,
                  final FilterableResponseSpecification responseSpec,
                  final FilterContext filterContext) {
    setRequestAttachmentName("Request - ${requestSpec.getMethod()} ${requestSpec.getURI() - requestSpec.getBaseUri()}")
    if (requestSpec.hasProperty("allureStepName") && requestSpec?.allureStepName) {
      Response response = Steps.stepWithValue(requestSpec.allureStepName) {
        Response response = super.filter(requestSpec, responseSpec, filterContext)
        if (requestSpec.hasProperty("requestMethodsToInvoke") && requestSpec?.requestMethodsToInvoke) {
          requestSpec.requestMethodsToInvoke.each {
            it(requestSpec)
          }
        }
        if (requestSpec.hasProperty("responseMethodsToInvoke") && requestSpec?.responseMethodsToInvoke) {
          requestSpec.responseMethodsToInvoke.each {
            it(response)
          }
        }
        return response
      }
      return response
    } else {
      return super.filter(requestSpec, responseSpec, filterContext)
    }
  }
}
