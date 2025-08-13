'''
Provides configuration loading and prompt generation utilities for the Image Analyzer microservice.
'''
import os
from dotenv import load_dotenv, find_dotenv

load_dotenv(find_dotenv())

class Config:
    '''Retrieves and validates required environment variables for runtime configuration.'''
    def __init__(self):
        self.gemini_api_key = self._get("GEMINI_API_KEY")
        self.ocr_languages = self._get("OCR_LANGUAGES")
        self.model_name = self._get("MODEL_NAME")

    def _get(self, name, default=None):
        value = os.getenv(name, default)
        if value is None:
            raise ValueError(f"Mandatory environment variable: {name}")
        return value

class PromptTemplate:
    '''Template handler for generating structured prompts.'''
    TEMPLATE = """
    Você é um assistente especializado em extrair informações de compras de recibos, notas fiscais e documentos financeiros.

    Analise cuidadosamente o texto abaixo e retorne um JSON com os seguintes campos:

    {
    "cnpj": "",
    "amount": 0.0,
    "currency": "",
    "date": "",
    "payment_method": "",
    "card_last_digits": "",
    "installments": {
            "is_installment": false,
            "number_of_installments": 0,
            "installment_value": 0.0
        },
    "items": []
    }

    Regras de extração:
    1. Todos os campos acima são obrigatórios — nunca retorne valores nulos.
    2. Para "amount":
    - Procure por valores totais, finais ou pagos.
    - Extraia apenas o valor numérico com símbolo de moeda (R$, $, €).
    3. Para "currency":
    - Identifique símbolos monetários ou códigos de moeda.
    4. Para "date":
    - Aceite qualquer formato (DD/MM/YYYY, MM/DD/YYYY, etc.).
    - Converta para o formato YYYY-MM-DD.
    - Procure por termos como "Data", "Date", "Emissão".
    5. Para "payment_method":
    - Identifique tipos de pagamento como "Cartão de Crédito", "Débito", "PIX", "Dinheiro", "Cash".
    6. Para "card_last_digits":
    - Procure por expressões como "**** 1234", "final 1234", "terminado em 1234".
    - Extraia apenas os 4 dígitos finais.
    7. Para "installments":
    - Verifique se o pagamento foi parcelado.
    - Procure por "parcelado", "x de", "parcelas".
    - Informe número de parcelas e valor de cada uma.
    8. Para "cnpj":
    - Procure por "CNPJ" seguido de números no formato XX.XXX.XXX/XXXX-XX.
    9. Para "items":
    - Liste os produtos ou serviços comprados, se estiverem presentes.
    - Caso não haja itens, retorne uma lista vazia [].
    10. Ignore taxas adicionais e descontos — considere apenas o valor final pago.
    11. Seja preciso e minucioso — recibos podem ter informações espalhadas e em formatos variados.

    Texto para analisar:
    {text}
    """

    @classmethod
    def build(cls, text: str) -> str:
        '''Builds a prompt by injecting OCR text into the template.'''
        return cls.TEMPLATE.replace("{text}", text)
