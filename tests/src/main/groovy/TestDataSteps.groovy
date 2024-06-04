import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import io.qameta.allure.Allure
import io.qameta.allure.Step
import org.yaml.snakeyaml.Yaml

class TestDataSteps extends TestData {

  @Step("Given scenario {scenario}")
  static Map scenario(String scenario) {
    Allure.addAttachment(scenario, "text/yaml", TestDataSteps.class.getClassLoader().getResourceAsStream(scenario).text)
    TestData.scenario(scenario)
  }

  @Step("Given custom scenario {description}")
  static save(Map data, String description) {
    Yaml yaml = new Yaml()
    StringWriter writer = new StringWriter()
    yaml.dump(data, writer)
    Allure.addAttachment(description, "text/yaml", writer.toString())
    loadProperties()
    db = Sql.newInstance(url: getValue("db_url"),
        user: getValue("db_username"),
        password: getValue("db_password"),
        driver: getValue("db_driver"))
    truncate()
    insertYMLData(new ByteArrayInputStream(writer.toString().bytes))
  }

  static void attach(String description, String statement, List<GroovyRowResult> rows) {
    Allure.addAttachment(description, "text/html", sqlResultToHtml(statement, rows), ".html")
  }

  static String sqlResultToHtml(String statement, List<GroovyRowResult> rows) {
    StringWriter st = new StringWriter()
    def mkup = new MarkupBuilder(st)
    if (rows.size() > 0) {
      mkup.html {
        p { mkp.yield statement }
        table(style: 'border:1px solid;text-align:center;') {
          tr {
            th()
            rows[0].keySet().each { th(it) }
          }
          rows.eachWithIndex { data, index ->
            tr(style: "outline: thin solid;") {
              td(index + 1)
              data.each { k, v -> td(v) }
            }
          }
        }
      }
    } else {
      mkup.html {}
    }
    return st.toString()
  }
}
