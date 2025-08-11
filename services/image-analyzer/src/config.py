'''Loads and exposes environment variables for the Image Analyzer microservice.'''
import os
from dotenv import load_dotenv, find_dotenv

load_dotenv(find_dotenv())

class Config:
    '''Retrieves and validates required environment variables for runtime configuration.'''
    def __init__(self):
        self.gemini_api_key = self._get("GEMINI_API_KEY")
        self.ocr_languages = self._get("OCR_LANGUAGES")
        self.gpu_enabled = self._get("GPU_ENABLED", default=True)
        self.model_name = self._get("MODEL_NAME")

    def _get(self, name, default=None):
        value = os.getenv(name, default)
        if (value is None):
            raise ValueError(f"Mandatory environment variable: {name}")
        return value