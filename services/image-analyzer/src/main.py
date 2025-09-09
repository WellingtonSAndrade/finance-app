'''
Initializes the FastAPI app, configures services, and registers API routes.

Includes health check and document analysis endpoints using a modular architecture.
'''
from src.config import Config
from src.infrastructure.ocr_service import OCRService
from src.infrastructure.llm_service import LLMService
from src.services.analyzer_service import AnalyzerService
from src.infrastructure.local_storage_client import LocalStorageClient
from src.infrastructure.rabbitmq_consumer import RabbitMQConsumer
from src.infrastructure.rabbitmq_publisher import RabbitMQProducer

config = Config()

ocr_service = OCRService(config)
llm_service = LLMService(config)
analyzer_service = AnalyzerService(ocr_service, llm_service)

local_storage_client = LocalStorageClient()
producer = RabbitMQProducer(config)

consumer = RabbitMQConsumer(config, local_storage_client, analyzer_service, producer)
consumer.start()
