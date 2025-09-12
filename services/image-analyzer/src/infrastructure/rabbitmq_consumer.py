"""
RabbitMQ consumer module for processing image analysis tasks.
"""
import json
import pika
from src.config import Config
from src.infrastructure.local_storage_client import LocalStorageClient
from src.services.analyzer_service import AnalyzerService
from src.infrastructure.rabbitmq_publisher import RabbitMQProducer

class RabbitMQConsumer:
    """
    Consumes messages from RabbitMQ, processes image tasks, and outputs structured data.
    """
    def __init__(self, config: Config, storage_client: LocalStorageClient,
                  analyzer: AnalyzerService, producer: RabbitMQProducer):
        self.queue_name = config.request_queue_name
        self.exchange = config.exchange_name
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=config.rabbit_host,
                credentials=pika.PlainCredentials(
                    username=config.rabbit_user,
                    password=config.rabbit_password
                )
            )
        )
        self.channel = self.connection.channel()
        self.channel.exchange_declare(exchange=self.exchange, exchange_type="direct", durable=True)
        self.channel.queue_declare(queue=self.queue_name, durable=True)

        self.analyzer = analyzer
        self.storage_client = storage_client
        self.producer = producer

    def start(self):
        """Starts consuming messages from the RabbitMQ queue."""
        self.channel.basic_consume(
            queue=self.queue_name,
            on_message_callback=self._callback
        )
        self.channel.start_consuming()

    def _callback(self, ch, method, properties, body):
        try:
            data = json.loads(body)
            image_url = data['imageUrl']
            task_id = data['taskId']
            user_id = data['userId']

            local_path = self.storage_client.download(image_url, task_id)

            extracted_data = self.analyzer.analyze_image(local_path)

            self.producer.publish(task_id, extracted_data, user_id)

            ch.basic_ack(delivery_tag=method.delivery_tag)
        except json.JSONDecodeError as e:
            print(f"Failed to decode JSON message: {e}")
        except KeyError as e:
            print(f"Missing expected key in message: {e}")
        except FileNotFoundError as e:
            print(f"Image file not found: {e}")
        finally:
            if 'local_path' in locals():
                self.storage_client.cleanup(local_path)
