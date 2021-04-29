package MvnPckg4.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import MvnPckg4.util.MyXLSReader;

public class BaseTest {
	
	public Properties prop=null;
	public MyXLSReader xlsx;
	public ExtentTest eTest;
	public ExtentReports eReport;
	public WebDriver driver=null;
	
	
	public void initialize() 
	
	{
		if(prop==null) 
		{
	    prop = new Properties();
		
		File propFile = new File("src//test//resources//projectconfig.properties");
		
		try {
		
		FileInputStream fis = new FileInputStream(propFile);
		
		prop.load(fis);
	         }catch(Exception e) {
	        	 e.printStackTrace();
	         }
		}
	}

	public void openBrowser(String browserType)
	
	{
		eTest.log(LogStatus.INFO, "Opening the browser"+browserType);
		
		
		if(browserType.equalsIgnoreCase("firefox"))
		{
			System.setProperty("webdriver.gecko.driver", prop.getProperty("firefoxDriverPath"));
			driver = new FirefoxDriver();
		} else if (browserType.equalsIgnoreCase("chrome"))
		
		{
			System.setProperty("webdriver.chrome.driver", prop.getProperty("chromeDriverPath"));
			driver = new ChromeDriver();
		}
		
		eTest.log(LogStatus.INFO, "Browser "+browserType+" got opened");
		driver.manage().window().maximize();
		
		eTest.log(LogStatus.INFO, "Browser "+browserType+" got maximized");
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
	}
	
	public void navigate(String urlKey)
	{
		String URL = prop.getProperty(urlKey);
		driver.get(URL);
		eTest.log(LogStatus.INFO, "Application got opened in the browser");
	}
	
	public boolean doLogin(String username, String password) 
	{
		//Click on the Login link - click()
		
		click("LoginLink_classname");
		type("EmailTextBox_id", username);
		click("Next_Button_id");
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		type("PasswordTextBox_id", password);
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		click("SignInButton_id");
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		if(isElementPresent("CRMOption_cssselector")) {
			
			return true;
			
		} else {
			return false;
		
		}
	}
	
	public void type(String locatorKey, String text)
	{
		WebElement element = getElement(locatorKey);
		element.sendKeys(text);
	}
	
	public void click(String locatorKey)
	{
		
	WebElement element = getElement(locatorKey);
	element.click();
	
	}
	
	public WebElement getElement(String locatorKey) 
	{
		WebElement element =null;
		String locatorValue = prop.getProperty(locatorKey);
		
		try
		{
	if(locatorKey.endsWith("_id"))
	
	{
		 element = driver.findElement(By.id(locatorValue));
	}else if (locatorKey.endsWith("_name"))
	{
		 element = driver.findElement(By.name(locatorValue));
	}else if (locatorKey.endsWith("_classname"))
	{
		 element = driver.findElement(By.className(locatorValue));
	}else if (locatorKey.endsWith("_linktext"))
	{
		 element = driver.findElement(By.linkText(locatorValue));
	}else if (locatorKey.endsWith("_cssselector"))
	{
		 element = driver.findElement(By.cssSelector(locatorValue));
	}else if (locatorKey.endsWith("_xpath"))
	{
		 element = driver.findElement(By.xpath(locatorValue));
	}
	
		}catch (Throwable t)
		{
			reportFail(locatorKey+" holding the " + locatorValue + " is not findable");
		}
		
		return element;
	}
	
	public boolean isElementPresent(String locatorKey) {
		
		WebElement element = getElement(locatorKey);
		
		boolean elementDisplayStatus = element.isDisplayed();
		
		return elementDisplayStatus;
	}
	
	public void reportPass(String message)
	{
		eTest.log(LogStatus.PASS, message);
	}
	
	public void reportFail(String message)
	{
		eTest.log(LogStatus.FAIL, message);
		takeScreenshot();
		Assert.fail(message);
	}
	
	public void takeScreenshot()
	
	{
		Date d = new Date();
		String screenshotFile = d.toString().replace(":", "_").replace(" ", "_")+".png";
		
		File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try {
			
			FileUtils.copyFile(srcFile, new File("screenshots//"+screenshotFile));
		}   catch (IOException e) {
			e.printStackTrace();
		}
		
		//put screenshot file in reports
		eTest.log(LogStatus.INFO,"Screenshot-> "+ eTest.addScreenCapture(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
		
	
	}
}



