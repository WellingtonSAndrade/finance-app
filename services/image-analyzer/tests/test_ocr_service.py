from unittest.mock import MagicMock, patch
import pytest
from PIL import Image
from src.infrastructure.ocr_service import OCRService
from src.config import Config

@pytest.fixture
def mock_config():
    config = MagicMock(spec=Config)
    config.ocr_languages = "pt"
    return config

@patch("src.infrastructure.ocr_service.easyocr.Reader")
def test_extract_text(mock_reader_class, mock_config):
    # Arrange
    mock_reader_instance = MagicMock()
    mock_reader_instance.readtext.return_value = ["CNPJ: 12.345.678/0001-90", "Total: R$ 123,45"]
    mock_reader_class.return_value = mock_reader_instance

    ocr_service = OCRService(config=mock_config)
    dummy_image = MagicMock(spec=Image.Image)

    # Act
    result = ocr_service.extract_text(dummy_image)

    # Assert
    assert result == "CNPJ: 12.345.678/0001-90 Total: R$ 123,45"
    mock_reader_instance.readtext.assert_called_once_with(dummy_image, detail=0)
