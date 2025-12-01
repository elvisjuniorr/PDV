package net.originmobi.pdv.selenium;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PessoasTest extends BaseTest {

    @Test
    public void TP1_deveCadastrarPessoa() {

        // 1. Acessar Pessoas
        driver.findElement(By.xpath("/html/body/div[3]/div/div[8]")).click();

        // 2. Clicar em Novo
        driver.findElement(By.xpath("/html/body/section[2]/div/div[2]/a")).click();

        // 3. Preencher dados da pessoa
        driver.findElement(By.id("nome")).sendKeys("Pessoa Selenium");
        driver.findElement(By.id("apelido")).sendKeys("Teste");
        driver.findElement(By.id("cpfcnpj")).sendKeys("12345678919"); // Alterar CPF para um que ainda não foi cadastrado
        driver.findElement(By.id("nascimento")).sendKeys("2000/01/01");

        // 4. Preencher observação
        driver.findElement(By.id("observacao")).sendKeys("Cadastro automático");

        // 5. Preencher endereço
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/ul/li[2]")).click();

        driver.findElement(By.id("cidade")).sendKeys("São Gonçalo");
        driver.findElement(By.id("rua")).sendKeys("Rua 1");
        driver.findElement(By.id("bairro")).sendKeys("Centro");
        driver.findElement(By.id("numero")).sendKeys("22");
        driver.findElement(By.id("cep")).sendKeys("24400000");
        driver.findElement(By.id("referencia")).sendKeys("Ao lado da padaria");

        // 6. Preencher contato
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/ul/li[3]")).click();

        driver.findElement(By.id("fone")).sendKeys("21977777777");
        driver.findElement(By.id("tipo")).sendKeys("CELULAR");

        // 7. Salvar
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/input[2]")).click();

        // 8. Validação
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        String mensagem = alert.getText();

        Assert.assertEquals("Pessoa salva com sucesso", mensagem);

        alert.accept();
    }

    @Test
    public void TP2_deveCadastrarPessoaComCpfJaCadastrado() {

        // 1. Acessar Pessoas
        driver.findElement(By.xpath("/html/body/div[3]/div/div[8]")).click();

        // 2. Clicar em Novo
        driver.findElement(By.xpath("/html/body/section[2]/div/div[2]/a")).click();

        // 3. Preencher dados da pessoa (cpf já cadastrado)
        driver.findElement(By.id("nome")).sendKeys("Pessoa Selenium");
        driver.findElement(By.id("apelido")).sendKeys("Teste");
        driver.findElement(By.id("cpfcnpj")).sendKeys("12345678911");
        driver.findElement(By.id("nascimento")).sendKeys("2000/01/01");

        // 4. Preencher observação
        driver.findElement(By.id("observacao")).sendKeys("Cadastro automático");

        // 5. Preencher endereço
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/ul/li[2]")).click();

        driver.findElement(By.id("cidade")).sendKeys("São Gonçalo");
        driver.findElement(By.id("rua")).sendKeys("Rua Java");
        driver.findElement(By.id("bairro")).sendKeys("Centro");
        driver.findElement(By.id("numero")).sendKeys("22");
        driver.findElement(By.id("cep")).sendKeys("24400000");
        driver.findElement(By.id("referencia")).sendKeys("Ao lado da padaria");

        // 6. Preencher contato
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/ul/li[3]")).click();

        driver.findElement(By.id("fone")).sendKeys("21977777777");
        driver.findElement(By.id("tipo")).sendKeys("CELULAR");

        // 7. Salvar
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/input[2]")).click();

        // 8. Validação
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        String mensagem = alert.getText();

        Assert.assertEquals("Já existe uma pessoa cadastrada com este CPF/CNPJ, verifique", mensagem);

        alert.accept();
    }
}
