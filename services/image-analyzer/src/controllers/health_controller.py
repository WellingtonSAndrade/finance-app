'''
Module: health_controller
Provides a health check endpoint.
'''
from fastapi import APIRouter

class HealthController:
    '''Controller for health check.'''
    def __init__(self):
        self.router = APIRouter(prefix="/health")
        self.router.get("/")(self.health_check)

    async def health_check(self):
        '''Returns API status.'''
        return {"status": "ok"}
