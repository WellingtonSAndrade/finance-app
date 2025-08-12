'''LLM service using GeminiAI'''
from google import genai
from src.config import Config

class LLMService:
    '''Initializes the LLM client with api_key and model settings'''
    def __init__(self, config: Config):
        self.client = genai.Client(api_key=config.gemini_api_key)
        self.model = config.model_name

    def ask(self, prompt: str) -> str:
        '''Sends a prompt to the Gemini model and returns the generated response.'''
        response = self.client.models.generate_content(
            model=self.model,
            contents=prompt
        )
        return response.text
