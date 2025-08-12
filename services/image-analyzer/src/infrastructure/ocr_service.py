'''OCR service using EasyOCR with custom configuration'''
import easyocr
from src.config import Config

class OCRService:
    '''Initializes the OCR reader with language and GPU settings'''
    def __init__(self, config: Config):
        self.reader = easyocr.Reader(config.ocr_languages.split("+"), gpu=config.gpu_enabled)

    def extract_text(self, image_path: str) -> str:
        '''Extracts and retuns text from the image as a single string'''
        text = self.reader.readtext(image_path, detail=0)
        return " ".join(text)
