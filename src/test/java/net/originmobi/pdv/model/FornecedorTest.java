package net.originmobi.pdv.selenium;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

public class FornecedorTest extends BaseTest {

    @Test
    public void TF1_deveAlterarFornecedor() {

        // 1. Acessar Fornecedor
        driver.findElement(By.xpath("/html/body/div[3]/div/div[9]")).click();

        // 2. Clicar no ícone de edição
        driver.findElement(By.xpath("/html/body/section[2]/div/div/table/tbody/tr[2]/td[7]/a")).click();

        // 3. Alterar dados principais
        driver.findElement(By.id("nomefantasia")).clear();
        driver.findElement(By.id("nomefantasia")).sendKeys("Fornecedor Teste Selenium");

        driver.findElement(By.id("nome")).clear();
        driver.findElement(By.id("nome")).sendKeys("Fornecedor Selenium LTDA");

        driver.findElement(By.id("cnpj")).clear();
        driver.findElement(By.id("cnpj")).sendKeys("11915857000158");

        driver.findElement(By.id("escricao")).clear();
        driver.findElement(By.id("escricao")).sendKeys("987654321");

        driver.findElement(By.id("situacao")).sendKeys("Ativo");

        // 4. Observação
        driver.findElement(By.id("observacao")).clear();
        driver.findElement(By.id("observacao")).sendKeys("Alterado via Selenium");

        // 5. Endereço
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/ul/li[2]")).click();

        driver.findElement(By.id("cidade")).sendKeys("Seringueiras");

        driver.findElement(By.id("rua")).clear();
        driver.findElement(By.id("rua")).sendKeys("Rua Selenium");

        driver.findElement(By.id("bairro")).clear();
        driver.findElement(By.id("bairro")).sendKeys("Centro");

        driver.findElement(By.id("numero")).clear();
        driver.findElement(By.id("numero")).sendKeys("10");

        driver.findElement(By.id("cep")).clear();
        driver.findElement(By.id("cep")).sendKeys("24000000");

        driver.findElement(By.id("referencia")).clear();
        driver.findElement(By.id("referencia")).sendKeys("Perto do mercado");

        // 6. Contato
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/ul/li[3]")).click();

        driver.findElement(By.id("fone")).clear();
        driver.findElement(By.id("fone")).sendKeys("21999999999");

        driver.findElement(By.id("tipo")).sendKeys("CELULAR");

        // 7. Salvar
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/input")).click();

        // 8. Validação
        String mensagem = driver.findElement(By.xpath("/html/body/section[1]/div/div/div[1]/div/span")).getText();

        Assert.assertEquals("Fornecedor salvo com sucesso", mensagem);
    }
     @Test
    public void TF2_naoDeveCadastrarFornecedorComCnpjDuplicado() {

        // 1. Acessar Fornecedor
        driver.findElement(By.xpath("/html/body/div[3]/div/div[9]")).click();

        // 2. Clicar no ícone de adicionar
        driver.findElement(By.xpath("/html/body/section[2]/div/div/div[2]/a")).click();

        // 3. Alterar dados principais
        driver.findElement(By.id("nomefantasia")).clear();
        driver.findElement(By.id("nomefantasia")).sendKeys("Fornecedor Teste Selenium");

        driver.findElement(By.id("nome")).clear();
        driver.findElement(By.id("nome")).sendKeys("Fornecedor Selenium LTDA");

        driver.findElement(By.id("cnpj")).clear();
        driver.findElement(By.id("cnpj")).sendKeys("11915857000158");

        driver.findElement(By.id("escricao")).clear();
        driver.findElement(By.id("escricao")).sendKeys("987654321");

        driver.findElement(By.id("situacao")).sendKeys("Ativo");

        // 4. Observação
        driver.findElement(By.id("observacao")).clear();
        driver.findElement(By.id("observacao")).sendKeys("Alterado via Selenium");

        // 5. Endereço
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/ul/li[2]")).click();

        driver.findElement(By.id("cidade")).sendKeys("Seringueiras");

        driver.findElement(By.id("rua")).clear();
        driver.findElement(By.id("rua")).sendKeys("Rua Selenium");

        driver.findElement(By.id("bairro")).clear();
        driver.findElement(By.id("bairro")).sendKeys("Centro");

        driver.findElement(By.id("numero")).clear();
        driver.findElement(By.id("numero")).sendKeys("10");

        driver.findElement(By.id("cep")).clear();
        driver.findElement(By.id("cep")).sendKeys("24000000");

        driver.findElement(By.id("referencia")).clear();
        driver.findElement(By.id("referencia")).sendKeys("Perto do mercado");

        // 6. Contato
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/ul/li[3]")).click();

        driver.findElement(By.id("fone")).clear();
        driver.findElement(By.id("fone")).sendKeys("21999999999");

        driver.findElement(By.id("tipo")).sendKeys("CELULAR");

        // 7. Salvar
        driver.findElement(By.xpath("/html/body/section[1]/div/div/form/input")).click();

        // 8. Validação (Esse teste deve falhar (bug encontrado), a resposta do BD não é bem tratada)
        String mensagem = driver.findElement(By.xpath("/html/body/section[1]/div/div/div[1]/div/span")).getText();

        Assert.assertEquals("CNPJ já cadastrado", mensagem);
    }
}