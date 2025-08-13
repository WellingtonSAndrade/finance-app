from unittest.mock import MagicMock, patch
import pytest
from src.infrastructure.llm_service import LLMService
from src.config import Config

@pytest.fixture
def mock_config():
    config = MagicMock(spec=Config)
    config.gemini_api_key = "fake-key"
    config.model_name = "gemini-pro"
    return config

@patch("src.infrastructure.llm_service.genai.Client")
def test_llm_service_ask_returns_expected_response(mock_client_class, mock_config):
    # Arrange
    mock_client_instance = MagicMock()
    mock_response = MagicMock()
    mock_response.text = '{"cnpj": "12.345.678/0001-90", "amount": 123.45}'
    mock_client_instance.models.generate_content.return_value = mock_response
    mock_client_class.return_value = mock_client_instance

    llm_service = LLMService(config=mock_config)
    prompt = "Texto extraído do OCR"

    # Act
    result = llm_service.ask(prompt)

    # Assert
    assert result == '{"cnpj": "12.345.678/0001-90", "amount": 123.45}'
    mock_client_instance.models.generate_content.assert_called_once_with(
        model="gemini-pro",
        contents=prompt
    )
