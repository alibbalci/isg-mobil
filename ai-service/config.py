import os
from pathlib import Path
from dotenv import load_dotenv

# override=True eklendi! Bu sayede sistemdeki eski şifre ezilip, .env dosyasındaki GÜNCEL şifre alınır.
ROOT_ENV = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(ROOT_ENV, override=True)

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

if not GEMINI_API_KEY:
    raise ValueError("GEMINI_API_KEY bulunamadı.")

DB_CONFIG = {
    "dbname": os.getenv("DB_NAME", "isg_db"),
    "user": os.getenv("DB_USER", "postgres"),
    "password": os.getenv("DB_PASSWORD", "1234"),
    "host": os.getenv("DB_HOST", "localhost"),
    "port": os.getenv("DB_PORT", "5432"),
}
