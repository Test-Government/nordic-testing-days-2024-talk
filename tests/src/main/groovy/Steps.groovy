import io.qameta.allure.Allure

class Steps {

  static void step(String name, Runnable closure) {
    Allure.step(name, closure as Allure.ThrowableRunnable)
  }
}
