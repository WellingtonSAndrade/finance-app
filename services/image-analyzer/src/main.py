'''
Initializes the FastAPI app, configures services, and registers API routes.

Includes health check and document analysis endpoints using a modular architecture.
'''
from fastapi import FastAPI
from src.config import Config
from src.infrastructure.ocr_service import OCRService
from src.infrastructure.llm_service import LLMService
from src.services.analyzer_service import AnalyzerService
from src.controllers.analyzer_controller import AnalyzerController
from src.controllers.health_controller import HealthController

app = FastAPI()

config = Config()
ocr_service = OCRService(config)
llm_service = LLMService(config)
analyzer_service = AnalyzerService(ocr_service, llm_service)

analyzer_controller = AnalyzerController(analyzer_service)
health_controller = HealthController()

app.include_router(analyzer_controller.router)
app.include_router(health_controller.router)
