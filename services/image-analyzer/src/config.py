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
        self.rabbit_host = self._get("RABBIT_HOST")
        self.rabbit_user = self._get("RABBIT_USER")
        self.rabbit_password = self._get("RABBIT_PASSWORD")
        self.request_queue_name = self._get("REQUEST_QUEUE_NAME")
        self.response_queue_name = self._get("RESPONSE_QUEUE_NAME")
        self.exchange_name = self._get("EXCHANGE_NAME")
        self.response_routing_key = self._get("RESPONSE_ROUTING_KEY")

    def _get(self, name, default=None):
        value = os.getenv(name, default)
        if value is None:
            raise ValueError(f"Mandatory environment variable: {name}")
        return value

class PromptTemplate:
    '''Template handler for generating structured prompts.'''
    TEMPLATE = """
    You are an assistant specialized in extracting purchase information from receipts, invoices, and financial documents.
    Carefully analyze the text below and return a JSON with the following fields:
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


    Extraction Rules:
    - All fields above are mandatory — never return null values.
    - For "amount":
    - Look for total, final, or paid amounts.
    - Extract only the numeric value with currency symbol (R$, $, €).
    - For "currency":
    - Identify currency symbols or codes.
    - For "date":
    - Accept any format (DD/MM/YYYY, MM/DD/YYYY, etc.).
    - Convert to the format YYYY-MM-DD.
    - Look for terms like "Data", "Date", "Emissão".
    - For "payment_method":
    - Identify payment types such as "Credit Card", "Debit", "PIX", "Cash".
    - For "card_last_digits":
    - Look for expressions like "**** 1234", "final 1234", "ending in 1234".
    - Extract only the last 4 digits.
    - For "installments":
    - Check if the payment was made in installments.
    - Look for "parcelado", "x de", "installments".
    - Provide the number of installments and the value of each.
    - For "cnpj":
    - Look for "CNPJ" followed by numbers in the format XX.XXX.XXX/XXXX-XX.
    - For "items":
    - List the purchased products or services, if present.
    - If no items are found, return an empty list [].
    - Ignore additional fees and discounts — consider only the final amount paid.
    - Be precise and thorough — receipts may contain scattered and varied formats of information.
    Text to analyze:
    {text}
    """

    @classmethod
    def build(cls, text: str) -> str:
        '''Builds a prompt by injecting OCR text into the template.'''
        return cls.TEMPLATE.replace("{text}", text)
