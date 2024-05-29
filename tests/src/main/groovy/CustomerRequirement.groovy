import io.qameta.allure.LabelAnnotation
import io.qameta.allure.LinkAnnotation

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import static io.qameta.allure.util.ResultsUtils.STORY_LABEL_NAME

/**
 * Mixture of Link and Story annotation, use this to add link to specification. Usage:
 * <pre>
 * &#064;CustomerRequirement("MY_REQUIREMENT_ID")
 * public void myTest() {
 *     ...
 * }
 * </pre>
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD, ElementType.TYPE])
@LinkAnnotation(type = "customer_requirement")
@LabelAnnotation(name = STORY_LABEL_NAME)
@interface CustomerRequirement {

  String value() default ""

}
