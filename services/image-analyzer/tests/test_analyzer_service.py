from unittest.mock import AsyncMock, MagicMock, patch
from jsonschema import ValidationError
import pytest
from fastapi import UploadFile
from src.services.analyzer_service import AnalyzerService

@pytest.mark.asyncio
@patch("src.services.analyzer_service.Image")
@patch("src.services.analyzer_service.JSONParser")
@patch("src.services.analyzer_service.PromptTemplate")
async def test_analyze_image_success(mock_prompt_class, mock_json_parser, mock_image_class):
    # Arrange
    mock_ocr = MagicMock()
    mock_llm = MagicMock()
    service = AnalyzerService(ocr_service=mock_ocr, llm_service=mock_llm)

    mock_file = AsyncMock(spec=UploadFile)
    mock_file.read.return_value = b"fake-image-bytes"

    mock_image = MagicMock()
    mock_image_class.open.return_value = mock_image

    mock_ocr.extract_text.return_value = "Texto do OCR"

    mock_prompt_class.return_value.build.return_value = "Prompt gerado"

    mock_llm.ask.return_value = '{"cnpj": "12.345.678/0001-90", "amount": 123.45}'

    mock_json_parser.extract_json_from_text.return_value = {
        "cnpj": "12.345.678/0001-90",
        "amount": 123.45
    }
    mock_json_parser.validate_receipt.return_value = None

    # Act
    result = await service.analyze_image(mock_file)

    # Assert
    assert result == {
        "cnpj": "12.345.678/0001-90",
        "amount": 123.45
    }
    mock_ocr.extract_text.assert_called_once_with(mock_image)
    mock_llm.ask.assert_called_once_with("Prompt gerado")
    mock_json_parser.extract_json_from_text.assert_called_once()
    mock_json_parser.validate_receipt.assert_called_once()

@pytest.mark.asyncio
@patch("src.services.analyzer_service.Image")
@patch("src.services.analyzer_service.JSONParser")
@patch("src.services.analyzer_service.PromptTemplate")
async def test_analyze_image_json_extraction_error(mock_prompt_class, mock_json_parser, mock_image_class):
    # Arrange
    mock_ocr = MagicMock()
    mock_llm = MagicMock()
    service = AnalyzerService(ocr_service=mock_ocr, llm_service=mock_llm)

    mock_file = AsyncMock(spec=UploadFile)
    mock_file.read.return_value = b"fake-image-bytes"
    mock_image_class.open.return_value = MagicMock()

    mock_ocr.extract_text.return_value = "Texto do OCR"
    mock_prompt_class.return_value.build.return_value = "Prompt gerado"
    mock_llm.ask.return_value = "resposta inválida"

    mock_json_parser.extract_json_from_text.side_effect = ValueError("Erro ao extrair JSON")

    # Act
    result = await service.analyze_image(mock_file)

    # Assert
    assert result == {"error": "Failed to extract JSON: Erro ao extrair JSON"}

@pytest.mark.asyncio
@patch("src.services.analyzer_service.Image")
@patch("src.services.analyzer_service.JSONParser")
@patch("src.services.analyzer_service.PromptTemplate")
async def test_analyze_image_json_validation_error(mock_prompt_class, mock_json_parser, mock_image_class):
    # Arrange
    mock_ocr = MagicMock()
    mock_llm = MagicMock()
    service = AnalyzerService(ocr_service=mock_ocr, llm_service=mock_llm)

    mock_file = AsyncMock(spec=UploadFile)
    mock_file.read.return_value = b"fake-image-bytes"
    mock_image_class.open.return_value = MagicMock()

    mock_ocr.extract_text.return_value = "Texto do OCR"
    mock_prompt_class.return_value.build.return_value = "Prompt gerado"
    mock_llm.ask.return_value = '{"cnpj": "12.345.678/0001-90"}'

    mock_json_parser.extract_json_from_text.return_value = {"cnpj": "12.345.678/0001-90"}

    mock_json_parser.validate_receipt.side_effect = ValidationError("Campo 'amount' ausente")

    # Act
    result = await service.analyze_image(mock_file)

    # Assert
    assert result == {"error": "Invalid JSON structure: Campo 'amount' ausente"}
