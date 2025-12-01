package net.originmobi.pdv.selenium;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ReqNaoFuncionais{

    protected WebDriver driver;

    @Before
    public void setUp() {
        
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }
    
    @Test
    public void RNF1_paginaDeveCarregarEmAte3SegundosAposLogar() {

        driver.get("http://localhost:8080/login");

        driver.findElement(By.id("user")).sendKeys("gerente");
        driver.findElement(By.id("password")).sendKeys("123");

        long inicio = System.currentTimeMillis();
        driver.findElement(By.id("btn-login")).click();

        // espera a página carregar um elemento
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/section[3]/div/div/div/div[1]/div/div")));

        long fim = System.currentTimeMillis();

        long tempoTotal = fim - inicio;

        assertTrue("Página demorou mais que 3 segundos", tempoTotal <= 3000);
    }

    @Test
    public void RNF2_naoDevePermitirAcessoSemLogin() {

        driver.get("http://localhost:8080/fornecedor");

        String urlAtual = driver.getCurrentUrl();

        // deve redirecionar para login
        assertTrue(urlAtual.contains("/login"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

}
