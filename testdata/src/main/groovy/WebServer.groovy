import groovy.sql.Sql
import groovy.xml.MarkupBuilder

import java.nio.charset.StandardCharsets

import static spark.Spark.get
import static spark.Spark.head
import static spark.Spark.port
import static spark.Spark.post

class WebServer {
    static void main(String... args) {
        port(8080)
        get("/", (req, res) -> index())
        get("/yaml/:name", (req, res) -> yaml(req.params(":name")))
        post("/submit", (req, res) -> submit(req.queryParams("yaml_data")))
        get("/reset", (req, res) -> reset())
    }

    static String submit(String yaml) {
        try {
            TestData.loadProperties()
            TestData.db = Sql.newInstance(url: TestData.getValue("db_url"),
                    user: TestData.getValue("db_username"),
                    password: TestData.getValue("db_password"),
                    driver: TestData.getValue("db_driver"))
            TestData.truncate()
            TestData.insertYMLData(yaml)
        } catch (Exception ex) {
            return ex.toString()
        }
        return "Data saved <br> <a href=/>Back</a>"
    }

    static String index() {

        def sb = new StringWriter()
        def html = new MarkupBuilder(sb)

        html.doubleQuotes = true
        html.expandEmptyElements = true
        html.omitEmptyAttributes = false
        html.omitNullAttributes = false
        html.html {
            head {
                title 'Petclinic YAML testdata'
                style "table, th, td {\n" +
                        "  border: 1px solid black;\n" +
                        "  border-collapse: collapse;\n" +
                        "}"
            }
            body {
                table {
                    tr {
                        th "Data file"
                        th "Description"
                    }
                    tr {
                        td {
                            a href: "/yaml/default", "default"
                        }
                        td "Default Petclinic data with 10 owners and 13 pets "
                    }
                    tr {
                        td {
                            a href: "/yaml/george_has_an_elephant", "george_has_an_elephant"
                        }
                        td "George has an elephant named Jumbo"
                    }
                    tr {
                        td {
                            a href: "/yaml/long_names", "long_names"
                        }
                        td "Owner and pet with long names"
                    }
                }
            }
        }
        return sb.toString()
    }

    static String yaml(String file) {
        def sb = new StringWriter()
        def html = new MarkupBuilder(sb)

        html.doubleQuotes = true
        html.expandEmptyElements = true
        html.omitEmptyAttributes = false
        html.omitNullAttributes = false
        html.html {
            head {
                title('Petclinic YAML testdata')
            }
            body {
                form(action: "/submit", method: "POST") {
                    textarea(id: "yaml", name: "yaml_data", rows: "50", cols: "160") {
                        mkp.yield(new String(WebServer.class.getClassLoader().getResourceAsStream("${file}.yml").readAllBytes(), StandardCharsets.UTF_8))
                    }
                    div {
                        button {
                            mkp.yield("Submit")
                        }
                    }
                }
            }
        }
        return sb.toString()
    }

    static String reset() {
        String data = new String(WebServer.class.getClassLoader().getResourceAsStream("default.yml").readAllBytes(), StandardCharsets.UTF_8)
        return submit(data)
    }
}
