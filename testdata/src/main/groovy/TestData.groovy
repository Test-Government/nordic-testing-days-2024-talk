//This file is duplicated in the testdata module, use sync_testdata.sh to sync changes

import groovy.sql.Sql
import org.yaml.snakeyaml.Yaml

class TestData {
  static Sql db
  static Properties props = new Properties()

  static loadProperties() {
    props.load(TestData.class.getClassLoader().getResourceAsStream('application.properties'))
  }

  static getValue(String key) {
    return System.getenv(key) ?: props.get(key)
  }

  static reset(Integer ownerMaxId = 10) {
    db.execute("DELETE FROM PETS WHERE OWNER_ID > $ownerMaxId")
    db.execute("DELETE FROM OWNERS WHERE ID > $ownerMaxId")
  }

  static truncate() {
    db.execute("SET FOREIGN_KEY_CHECKS = 0")
    db.execute("TRUNCATE owners")
    db.execute("TRUNCATE pets")
    db.execute("TRUNCATE types")
    db.execute("SET FOREIGN_KEY_CHECKS = 1")
  }

  static Map scenario(String scenario) {
    loadProperties()
    db = Sql.newInstance(url: getValue("db_url"),
        user: getValue("db_username"),
        password: getValue("db_password"),
        driver: getValue("db_driver"))
    truncate()
    return insertYMLData(TestData.class.getClassLoader().getResourceAsStream(scenario))
  }

  static Map insertYMLData(testData) {
    Map data = getData(testData)
    data.types.each { insertType(it) }
    data.owners.each { insertOwner(it) }
    return data
  }

  static Map getData(testData) {
    return new Yaml().load(testData)
  }

  static Map readData(String scenario) {
    return new Yaml().load(TestData.class.getClassLoader().getResourceAsStream(scenario))
  }

  static void insertType(Map data) {
    Map typeData = [:]
    typeData << filterValues(data)
    insertMapData(db, "types", typeData)
  }

  static void insertOwner(Map data) {
    Map ownerData = [:]
    ownerData << filterValues(data)
    insertMapData(db, "owners", ownerData)
    if (data.containsKey("pets")) {
      data.pets.each {
        insertPet(it, data["id"])
      }
    }
  }

  static void insertPet(Map data, Integer ownerId) {
    Map petData = ["owner_id": ownerId]
    petData << filterValues(data)
    insertMapData(db, "pets", petData)
  }

  static insertMapData(Sql db, String tableName, Map data) {
    db.execute("""
             INSERT INTO ${tableName}
                    (${data.keySet().join(',')})
                    VALUES
                    (${data.keySet().collect { key -> ":" + key }.join(',')})
        """, data)
  }

  static Map filterValues(Map data) {
    List keys = data.findAll { !(it.value instanceof Map) && !(it.value instanceof List) }.collect() { it.key }
    data.each { key, value ->
      if (value.getClass() == Date) {
        data[key] = value.toTimestamp()
      }
    }
    return data.subMap(keys)
  }
}
