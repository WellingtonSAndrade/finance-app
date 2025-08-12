from src.config import Config
from src.infrastructure.ocr_service import OCRService
from src.infrastructure.llm_service import LLMService

def main():
    config = Config()
    ocr = OCRService(config)

    result = ocr.extract_text("src/hello_world.png")
    print(result)

    llm = LLMService(config)

    llm_response = llm.ask("Que dia é hoje?")
    print(llm_response)

if __name__ == "__main__":
    main()
