'''Module for extracting and validating receipt JSON data.'''
import json
import re
from jsonschema import validate

class JSONParser:
    '''Extracts and validates receipt data from text.'''

    receipt_schema = {
        "type": "object",
        "properties": {
            "cnpj": {"type": "string"},
            "amount": {"type": "number"},
            "currency": {"type": "string"},
            "date": {"type": "string"},
            "payment_method": {"type": "string"},
            "card_last_digits": {"type": "string"},
            "has_installment": {"type": "boolean"},
            "installments": {
                "type": "object",
                "properties": {
                    "total_installments": {"type": "integer"},
                    "amount": {"type": "number"}
                },
                "required": [
                    "total_installments",
                    "amount"
                ]
            }
        },
        "required": [
            "cnpj",
            "amount",
            "currency",
            "date",
            "payment_method",
            "card_last_digits",
            "has_installment",
            "installments"
        ]
    }

    @staticmethod
    def extract_json_from_text(text: str) -> dict:
        '''Finds and parses a JSON object from a string.'''
        match = re.search(r"\{.*\}", text, re.DOTALL)
        if not match:
            raise ValueError("No json found in text")
        return json.loads(match.group())

    @classmethod
    def validate_receipt(cls, data: dict):
        '''Checks if the JSON matches the receipt schema.'''
        validate(instance=data, schema=cls.receipt_schema)
