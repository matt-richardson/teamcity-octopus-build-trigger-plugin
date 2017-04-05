using OpenQA.Selenium.Chrome;
using OpenQA.Selenium;

IWebDriver driver = new ChromeDriver();
driver.Navigate().GoToUrl("http://localhost:8111");

while (true) {
  if (driver.FindElements(By.LinkText("I'm a server administrator, show me the details")).Count == 1) {
    Console.WriteLine("Clicking the \"I'm a server administrator, show me the details\" link");
    driver.FindElement(By.LinkText("I'm a server administrator, show me the details")).Click();
  }
  else if (driver.FindElements(By.Id("data-directory-location")).Count == 1 && driver.FindElement(By.Id("proceedButton")) != null) {
    Console.WriteLine("Accepting default data directory");
    var button = driver.FindElement(By.Id("proceedButton"));
    button.Click();
  }
  else if (driver.FindElements(By.Id("dbType")).Count == 1 && driver.FindElement(By.Id("proceedButton")) != null) {
    Console.WriteLine("Accepting default database config");
    var button = driver.FindElement(By.Id("proceedButton"));
    button.Click();
  }
  else if (driver.FindElements(By.Name("_accept")).Count == 1) {
    Console.WriteLine("Accepting license agreement");
    driver.FindElement(By.Id("accept")).Click();
    var button = driver.FindElement(By.Name("Continue"));
    button.Click();
  }
  else if (driver.FindElements(By.Id("input_teamcityUsername")).Count == 1 && driver.FindElements(By.ClassName("btn")).Count == 1) {
    Console.WriteLine("Creating admin account");
    driver.FindElement(By.Id("input_teamcityUsername")).SendKeys("admin");
    driver.FindElement(By.Id("password1")).SendKeys("Passw0rd123");
    driver.FindElement(By.Id("retypedPassword")).SendKeys("Passw0rd123");
    var button = driver.FindElement(By.ClassName("btn"));
    button.Click();
  }
  else if (driver.FindElements(By.Id("input_teamcityEmail")).Count == 1) {
    Console.WriteLine("TeamCity configuration complete");
    break;
  }
  else if (driver.FindElements(By.Id("username")).Count == 1) {
    Console.WriteLine("TeamCity is already configured");
    break;
  }
  Console.Write(".");
  System.Threading.Thread.Sleep(1000);
}

driver.Close();
driver.Quit();
