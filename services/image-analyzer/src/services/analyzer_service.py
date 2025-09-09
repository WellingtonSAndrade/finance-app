'''Handles image analysis using OCR and LLM services.'''
from jsonschema import ValidationError
from src.infrastructure.ocr_service import OCRService
from src.infrastructure.llm_service import LLMService
from src.config import PromptTemplate
from src.utils.json_parser import JSONParser

class AnalyzerService:
    '''Combines OCR and LLM to analyze images.'''
    def __init__(self, ocr_service: OCRService, llm_service: LLMService):
        self.ocr = ocr_service
        self.llm = llm_service
        self.prompt = PromptTemplate()

    def analyze_image(self, file_path: str):
        '''Extracts text from an image, sends it to LLM, and validates the returned JSON.'''
        try:
            extracted_text = self.ocr.extract_text(file_path)

            prompt = self.prompt.build(extracted_text)

            llm_response = self.llm.ask(prompt)

            data = JSONParser.extract_json_from_text(llm_response)

            JSONParser.validate_receipt(data)

            return data
        except ValueError as e:
            return {"error": f"Failed to extract JSON: {str(e)}"}
        except ValidationError as e:
            return {"error": f"Invalid JSON structure: {e.message}"}
