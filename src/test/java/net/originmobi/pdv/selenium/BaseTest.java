package net.originmobi.pdv.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.concurrent.TimeUnit;

public class BaseTest {

    protected WebDriver driver;

    @Before
    public void setUp() {
        
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, "ignore");
        
        driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        driver.get("http://localhost:8080/login");

        driver.findElement(By.id("user")).sendKeys("gerente");
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.id("btn-login")).click();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
