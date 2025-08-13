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
            "installments": {
                "type": "object",
                "properties": {
                    "is_installment": {"type": "boolean"},
                    "number_of_installments": {"type": "integer"},
                    "installment_value": {"type": "number"}
                },
                "required": [
                    "is_installment",
                    "number_of_installments",
                    "installment_value"
                ]
            },
            "items": {"type": "array"}
        },
        "required": [
            "cnpj",
            "amount",
            "currency",
            "date",
            "payment_method",
            "card_last_digits",
            "installments",
            "items"
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
