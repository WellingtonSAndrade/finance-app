"""Handles local image storage operations for temporary processing."""
import os
import shutil

class LocalStorageClient:
    """Manages image retrieval from the uploads folder and cleanup of temporary files."""
    def __init__(self):
        self.temp_dir = "temp"
        os.makedirs(self.temp_dir, exist_ok=True)

        self.base_dir = os.path.abspath(os.path.join(__file__, "..", "..", "..", "..", ".."))

    def download(self, image_url: str, task_id: str) -> str:
        """copies the image from the uploads folder to temp and returns the local path"""
        relative_path = image_url.lstrip('/')
        source_path = os.path.join(self.base_dir, relative_path)

        if not os.path.exists(source_path):
            raise FileNotFoundError(f"File not found: {image_url}")

        ext = os.path.splitext(source_path)[1]
        filename = f"{task_id}{ext}"
        local_path = os.path.join(self.temp_dir, filename)

        shutil.copy2(source_path, local_path)

        return local_path

    def cleanup(self, file_path: str):
        """Deletes the specified temporary file if it exists."""
        if os.path.exists(file_path):
            os.remove(file_path)
