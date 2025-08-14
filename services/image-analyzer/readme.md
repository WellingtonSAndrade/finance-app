# 🧠 Image Analyzer Microservice

O **image-analyzer** é um microserviço inteligente que:

- 📸 Recebe imagens de recibos ou notas fiscais  
- 🔍 Extrai o texto das imagens usando OCR  
- 🤖 Analisa o conteúdo com IA (Gemini) para gerar um JSON estruturado com informações de compras

Este microserviço faz parte de um sistema de gestão financeira automatizado, responsável por processar e analisar recibos antes de enviá-los para outros serviços do sistema.

## ⚙️ Tecnologias Utilizadas

- 🐍 Python 3.11+  
- 🚀 FastAPI  
- 👁️ EasyOCR  
- 🌐 Google Gemini AI (via \`genai\`)
- 🐳 Docker   

## 🗂️ Estrutura do Projeto

```
image-analyzer/
├── src/
│   ├── controllers/         # Endpoints FastAPI
│   ├── services/            # Lógica de análise
│   ├── infrastructure/      # Serviços externos (OCR, LLM)
│   ├── config.py            # Configurações do serviço e prompt para a IA
│   ├── main.py              # Inicialização da aplicação
│   └── utils.py             # Funções auxiliares
├── tests/                   # Testes unitários e integração
├── requirements.txt
├── Dockerfile
└── README.md
```

## 📥 Clonando o Repositório

Para obter o projeto em sua máquina, use:

```
git clone --no-checkout https://github.com/WellingtonSAndrade/P.A.G.A.R..git
cd P.A.G.A.R.
git sparse-checkout init --cone
git sparse-checkout set src/image-analyzer
git checkout master
```

## 🔐 Variáveis de Ambiente

Crie um arquivo .env com:
```
GEMINI_API_KEY=your_gemini_api_key_here   # Chave da API Gemini
OCR_LANGUAGES=pt,en                       # Idiomas suportados pelo OCR (ex: pt,en)
MODEL_NAME=your_api_model_name            # Nome do modelo Gemini a ser usado
```
Use .env.example como modelo.

## 📦 Instalação de Dependências

No ambiente virtual Python:

```
pip install -r requirements.txt
```

## 🚀 Executando o Serviço

### 🔧 Localmente (modo desenvolvimento)

```
unicorn main:app --reload
```

### 🐳 Via Docker

```
docker build -t image-analyzer .
docker run -p 8000:8000 image-analyzer
```

## 🌐 Endpoints

### ✅ Health Check

```
GET /health

Retorna o status do serviço.
```

### 🧾 Analisar Imagem

```
POST /analyze

- Body: \`multipart/form-data\` com campo \`image\` (arquivo de imagem)  
- Retorna JSON com os campos extraídos (CNPJ, valor, data, método de pagamento, etc)
```

#### 📥 Exemplo de Request

```
curl -X POST \"http://localhost:8000/analyze\" -F \"image=@comprovante.jpg\"
```

#### 📤 Exemplo de Resposta

```
json
{
  "cnpj": "12.345.678/0001-99",
  "amount": 150.0,
  "currency": "R$",
  "date": "2025-08-14",
  "payment_method": "PIX",
  "installments": {
    "is_installment": false,
    "number_of_installments": 0,
    "installment_value": 0.0
  },
  "items": [
    {"name": "Produto A", "quantity": 1, "price": 100.0},
    {"name": "Produto B", "quantity": 2, "price": 25.0}
  ]
}
```

## 📝 Notas
  
- 🧪 Testes unitários e de integração estão em `tests/`.
