'''Handles image analysis using OCR and LLM services.'''
from io import BytesIO
from fastapi import UploadFile
from PIL import Image
from src.infrastructure.ocr_service import OCRService
from src.infrastructure.llm_service import LLMService
from src.config import PromptTemplate

class AnalyzerService:
    '''Combines OCR and LLM to analyze images.'''
    def __init__(self, ocr_service: OCRService, llm_service: LLMService):
        self.ocr = ocr_service
        self.llm = llm_service
        self.prompt = PromptTemplate()

    async def analyze_image(self, file: UploadFile):
        '''Extracts text from an image and gets a response from LLM.'''
        image_bytes = await file.read()
        image = Image.open(BytesIO(image_bytes))
        extracted_text = self.ocr.extract_text(image)
        prompt = self.prompt.build(extracted_text)
        result = self.llm.ask(prompt)
        return result