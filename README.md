# Projeto da Disciplina de Qualidade e Teste 2025.2

Este é o repositório do projeto usado para aplicação do trabalho prático da disciplina de Qualidade e Teste. Neste README estarão os links para os principais artefatos gerados para o projeto.

## 1º Entrega:
- [Plano de Testes do Projeto (Parcial)](https://docs.google.com/document/d/1fgIgzIyryhDrJuJulroLd96kp2agQrKBn5fH6Bf8enM/edit?usp=sharing)
- [Resultados de exeução Testlink](https://drive.google.com/file/d/1xL7c7EXfT_h2VUo2k71meyN4QeeUlOUC/view?usp=sharing)
- [Slides de Apresentação do Projeto](https://www.canva.com/design/DAG0_Owvf5Y/Pfw2zR69lIXSj69j5vAEbQ/edit?utm_content=DAG0_Owvf5Y&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

## 2º Entrega:
- [Plano de Testes do Projeto (Final)](https://docs.google.com/document/d/1qdnpXWp8VUqscI4Ae_ZGLOTABEOiFzmzrknTNNNmiQo/edit?usp=sharing)
- [Relatório de Inspeção Código-Fonte](https://docs.google.com/document/d/1U_yCkVsSMighyUco4tB9qBAWf4rPHTTW1YjrpALwN5s/edit?usp=sharing)
- [Indicação das medidas de cada atributo de qualidade da ISO 25010](https://docs.google.com/document/d/12atZMQvwFSsmVnGhbqumPjXo6LRckiqzfG1a0Q6JJmE/edit?usp=sharing)
- [Slides de Apresentação do Projeto](https://www.canva.com/design/DAG6S0cxXrc/nSnOo4mcYqbocKZYDFCtfA/edit?utm_content=DAG6S0cxXrc&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

## Grupo - E.R.R.O.K:
- Elvis Souza
- Kenji Odate
- Oliver Almeida
- Rachel Barino
- Rafael Roza

# PDV
Sistema de ERP web desenvolvido em Java com Spring Framework 

# Recursos
- Cadastro produtos/clientes/fornecedor
- Controle de estoque
- Gerenciar comandas
- Realizar venda
- Controle de fluxo de caixa
- Controle de pagar e receber
- Venda com cartões
- Gerenciar permissões de usuários por grupos
- Cadastrar novas formas de pagamentos
- Relatórios

# Instalação
Para instalar o sistema, você deve criar o banco de dado "pdv" no mysql e configurar o arquivo application.properties
com os dados do seu usuário root do mysql e rodar o projeto pelo Eclipse ou gerar o jar do mesmo e execultar.

# Logando no sistema
Para logar no sistema, use o usuário "gerente" e a senha "123".

# Tecnologias utilizadas
- Spring Framework 5
- Thymeleaf 3
- MySQL
- Hibernate
- FlyWay

# Execução com Docker
Para executar a aplicação utilizando o docker, utilize o seguinte comando na raiz do projeto:
```sh
docker compose up -d
```
