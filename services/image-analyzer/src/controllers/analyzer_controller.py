'''
Module: analyzer_controller
Provides an endpoint to analyze uploaded images.
'''
from fastapi import APIRouter, UploadFile, File
from src.services.analyzer_service import AnalyzerService

class AnalyzerController:
    '''Controller for image analysis.'''
    def __init__(self, analyzer: AnalyzerService):
        self.service = analyzer
        self.router = APIRouter(prefix="/analyze")
        self.router.post("/")(self.analyze)

    async def analyze(self, file: UploadFile = File(...)):
        '''Analyzes an uploaded image file.'''
        response = await self.service.analyze_image(file)
        return response
