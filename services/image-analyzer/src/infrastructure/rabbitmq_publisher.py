"""Publishes receipt data to a RabbitMQ exchange."""
import json
import pika
from src.config import Config

class RabbitMQProducer:
    """Setup connection, exchange, and queue."""
    def __init__(self, config: Config):
        self.exchange = config.exchange_name
        self.response_queue = config.response_queue_name
        self.routing_key_response = config.response_routing_key

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

        self.channel.queue_declare(queue=self.response_queue, durable=True)
        self.channel.queue_bind(
            exchange=self.exchange,
            queue=self.response_queue,
            routing_key=self.routing_key_response
        )

    def publish(self, task_id: str, receipt_schema: dict, user_id: str):
        """Send receipt data with task ID."""
        payload = {
            "task_id": task_id,
            "user_id": user_id,
            "receipt_schema": receipt_schema
        }
        self.channel.basic_publish(
            exchange=self.exchange,
            routing_key=self.routing_key_response,
            body=json.dumps(payload),
            properties=pika.BasicProperties(delivery_mode=2)
        )

    def close(self):
        """Close RabbitMQ connection."""
        self.connection.close()
