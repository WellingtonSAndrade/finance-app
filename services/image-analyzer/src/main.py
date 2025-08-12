from src.config import Config
from src.infrastructure.ocr_service import OCRService

def main():
    config = Config()
    ocr = OCRService(config)

    result = ocr.extract_text("src/hello_world.png")
    print(result)

if __name__ == "__main__":
    main()
