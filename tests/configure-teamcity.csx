using OpenQA.Selenium.Chrome;
using OpenQA.Selenium;

IWebDriver driver = new ChromeDriver();
driver.Navigate().GoToUrl("http://localhost:8111");

while (driver.FindElements(By.Id("auto-refresh-note")).Count == 1) {
    Console.WriteLine("TeamCity is still loading...");
    System.Threading.Thread.Sleep(5000);
}

if (driver.FindElements(By.Id("username")).Count == 1) {
  Console.WriteLine("Logging in");
  driver.FindElement(By.Id("username")).SendKeys("admin");
  driver.FindElement(By.Id("password")).SendKeys("Passw0rd123");
  driver.FindElement(By.Name("submitLogin")).Click();
}

/*
 - create "RunsOnEveryDeployment" project
 - create "RunsOnEverySuccessfulDeployment" project
 - create "RunsOnReleaseCreated" project
 - create "RunsWhenTentacleAdded" project
 - create "RunsWhenDeploymentProcessIsChanged" project
*/


driver.Close();
driver.Quit();
