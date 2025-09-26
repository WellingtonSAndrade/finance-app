# 💰 Finance App

Aplicação de **gestão financeira pessoal**, que permite registrar despesas manualmente ou automaticamente a partir da **análise de fotos** com OCR e IA.  
O projeto combina **Java 17 (Spring Boot)** e **Python (EasyOCR + IA)** em uma arquitetura baseada em **microserviços**, com comunicação via **RabbitMQ** e orquestração por **Docker**.

---

## 📌 Visão Geral

O **Finance App** foi desenvolvido para simplificar o controle financeiro do dia a dia, oferecendo flexibilidade ao usuário:

- Inserir despesas manualmente pela API.  
- Capturar despesas automaticamente por meio da leitura de imagens (ex: notas fiscais, comprovantes) com OCR + IA.  

Essa integração garante mais praticidade e precisão no gerenciamento das finanças.

---

## ✨ Funcionalidades

### ✅ Implementadas
- Cadastro manual de **despesas**.  
- Cadastro automático de despesas via **OCR + IA**.  
- Categorização inteligente dos gastos.  
- Ranking mensal de gastos.

### 🚧 Roadmap
- Relatórios financeiros com comparativos e tendências.  
- Projeções de gastos futuros.  
- Alertas de limite de orçamento.  
- Suporte a múltiplas moedas e tags personalizadas.  

---

## 🛠️ Tecnologias Utilizadas

- **Java 17 + Spring Boot** → API principal (backend).  
- **Python** → OCR e análise de imagens.  
- **EasyOCR** → Reconhecimento de texto em imagens.  
- **IA (LLM/validação inteligente)** → Estruturação de dados financeiros.  
- **RabbitMQ** → Comunicação assíncrona entre serviços.  
- **Docker & Docker Compose** → Orquestração e deploy.  
- **Maven** → Build e gerenciamento de dependências (Java).  

---

## 🚀 Como Executar

### Pré-requisitos
- [Docker](https://www.docker.com/)  
- [Docker Compose](https://docs.docker.com/compose/)  
- Java 17 (se for rodar a API separada sem Docker)  
- Python 3.10+ (se for rodar OCR separadamente)  

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/WellingtonSAndrade/finance-app.git
   cd finance-app

2. Suba os serviços com Docker:
    ```bash
    docker-compose up --build

3. Acesse os serviços:

    - API (Java/Spring Boot): http://localhost:8080

    - RabbitMQ Dashboard: http://localhost:15672 
        - Usuário: rabbitmq
        - Senha: rabbitmq

---

## 🧑‍💻 Uso

- Cadastro manual: via endpoints da API (ou futura interface web/app).

- Cadastro automático: envie imagens de notas fiscais ou comprovantes para o serviço de OCR → elas serão processadas, validadas pela IA e transformadas em despesas financeiras registradas automaticamente.

---

## 📈 Status do Projeto

Atualmente em desenvolvimento ativo.
Funcionalidades principais já implementadas e roadmap definido para evolução contínua.