using OpenQA.Selenium.Chrome;
using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;

IWebDriver driver = new ChromeDriver();
driver.Navigate().GoToUrl("http://localhost:8111");

var apiKey = System.Environment.GetEnvironmentVariable("OctopusApiKey");

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

WebDriverWait wait = new WebDriverWait(driver, TimeSpan.FromSeconds(5));

Console.WriteLine("Clicking on 'Administration' link");
wait.Until(ExpectedConditions.ElementToBeClickable(By.LinkText("Administration")));
driver.FindElement(By.LinkText("Administration")).Click();

Console.WriteLine(" - waiting for '<Root Project>' to become clickable");
wait.Until(ExpectedConditions.ElementToBeClickable(By.XPath("//*[@id=\"adminOverview\"]/tbody/tr[1]/td[1]/a")));

Console.WriteLine("Clicking on '<Root Project>'");
driver.FindElement(By.XPath("//*[@id=\"adminOverview\"]/tbody/tr[1]/td[1]/a")).Click();

Console.WriteLine("Clicking on 'VCS Roots'");
driver.FindElement(By.LinkText("VCS Roots")).Click();

if (driver.FindElements(By.XPath("//*[@id=\"projectVcsRoots\"]/tbody/tr[2]/td[1]/span[2]/a")).Count > 0)
{
  Console.WriteLine("VCS Root already created");
}
else
{
  Console.WriteLine("Clicking 'Create VCS Root'");
  driver.FindElement(By.XPath("//*[@id=\"existingVcsRoots\"]/p/a")).Click();

  Console.WriteLine("Choosing 'Git' from dropdown");
  wait.Until(ExpectedConditions.ElementIsVisible(By.Id("-ufd-teamcity-ui-vcsName")));
  driver.FindElement(By.Id("-ufd-teamcity-ui-vcsName")).Click();
  driver.FindElement(By.Id("-ufd-teamcity-ui-vcsName")).Clear();
  driver.FindElement(By.Id("-ufd-teamcity-ui-vcsName")).SendKeys("Git");
  driver.FindElement(By.Id("repositoryUrl")).Click();

  Console.WriteLine(" - waiting for 'VCS Root Name' to appear");
  wait.Until(ExpectedConditions.ElementIsVisible(By.Id("vcsRootName")));

  Console.WriteLine("Entering VCS Root Name");
  driver.FindElement(By.Id("vcsRootName")).Click();
  driver.FindElement(By.Id("vcsRootName")).Clear();
  driver.FindElement(By.Id("vcsRootName")).SendKeys("TeamCityConfiguration");

  Console.WriteLine("Entering VCS Url");
  driver.FindElement(By.Id("url")).Click();
  driver.FindElement(By.Id("url")).Clear();
  driver.FindElement(By.Id("url")).SendKeys("C:\\TeamCityConfiguration");

  Console.WriteLine("Saving VCS Root");
  driver.FindElement(By.XPath("//*[@id=\"vcsRootPropertiesInner\"]/div[3]/div/input[1]")).Click();
  wait.Until(ExpectedConditions.ElementToBeClickable(By.Id("message_vcsRootsUpdated")));
}

Console.WriteLine("Clicking on 'Versioned Settings'");
wait.Until(ExpectedConditions.ElementToBeClickable(By.LinkText("Versioned Settings")));
driver.FindElement(By.LinkText("Versioned Settings")).Click();

Console.WriteLine("Clicking on 'Enabled'");
driver.FindElement(By.Id("enabled")).Click();

Console.WriteLine(" - waiting until 'Apply' button is clickable");
wait.Until(ExpectedConditions.ElementToBeClickable(By.XPath("//*[@id=\"versionedSettingSaveRow\"]/td/input[1]")));
Console.WriteLine(" - waiting until 'Project Settings VCS Root' is visible");
wait.Until(ExpectedConditions.ElementToBeClickable(By.Id("-ufd-teamcity-ui-settingsVcsRootId")));

Console.WriteLine("Choosing VCS Root 'TeamCityConfiguration'");
driver.FindElement(By.Id("-ufd-teamcity-ui-settingsVcsRootId")).Click();
driver.FindElement(By.Id("-ufd-teamcity-ui-settingsVcsRootId")).Clear();
driver.FindElement(By.Id("-ufd-teamcity-ui-settingsVcsRootId")).SendKeys("TeamCityConfiguration");
driver.FindElement(By.Id("buildSettingsModeAlwaysCurrent")).Click();

Console.WriteLine("Clicking 'Apply' button");
driver.FindElement(By.XPath("//*[@id=\"versionedSettingSaveRow\"]/td/input[1]")).Click();

// Console.WriteLine(" - waiting for import confirm box");
// wait.Until(ExpectedConditions.AlertIsPresent());
// var alert = driver.SwitchTo().Alert();
//alert.Accept();

Console.WriteLine(" - waiting for 'Existing Project Settings Detected' dialog to appear");
wait.Until(ExpectedConditions.ElementToBeClickable(By.XPath("//*[@id=\"nonEmptyDirConfirmDialog\"]/div[2]/div[2]/input[3]")));

Console.WriteLine("Clicking 'Import settings from VCS' button");
driver.FindElement(By.XPath("//*[@id=\"nonEmptyDirConfirmDialog\"]/div[2]/div[2]/input[3]")).Click();

Console.WriteLine(" - waiting for settings to import");
while(true)
{
  //todo: add timeout
  try {
    var element = driver.FindElement(By.XPath("//*[@id=\"versionedSettingsStatusInner\"]/table/tbody/tr[1]/td[2]/span"));
    if (element.Text.Contains("Changes from VCS are applied to project settings"))
      break;
  }
  catch (NoSuchElementException e)
  {
    Console.WriteLine("Status element doesn't exist yet - retrying.");
    //do nothing - teamcity is replacing the element every second or so
  }
  catch (StaleElementReferenceException ex)
  {
    Console.WriteLine("DOM changed under us - retrying.");
    //do nothing - teamcity is replacing the element every second or so
  }
  System.Threading.Thread.Sleep(100);
}

Console.WriteLine("Configuration Imported");
//driver.Close();
//driver.Quit();
