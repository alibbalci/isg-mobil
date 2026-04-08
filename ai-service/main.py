from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Any
import asyncio

from services.analyze_image import analyze_image
from services.search_service import search_risk
from services.llm_service import generate_comment

app = FastAPI()


class AnalyzeRequest(BaseModel):
    imageUrl: str


class AnalyzeResponse(BaseModel):
    description: str
    risks: List[Dict[str, Any]]
    comment: str


@app.post("/analyze", response_model=AnalyzeResponse)
async def analyze(request: AnalyzeRequest):

    if not request.imageUrl:
        raise HTTPException(status_code=400, detail="imageUrl boş olamaz")

    try:
        print("\n===== NEW REQUEST =====")
        print("IMAGE URL:", request.imageUrl)

        # 1️⃣ IMAGE ANALYSIS
        print("\n[STEP 1] analyze_image başladı")
        description = await asyncio.to_thread(analyze_image, request.imageUrl)

        print("[STEP 1 RESULT] description:", description)

        if not description:
            print("[STEP 1 WARNING] description boş, fallback kullanılıyor")
            description = "genel iş güvenliği riski"

        # 2️⃣ RISK SEARCH
        print("\n[STEP 2] search_risk başladı")
        risks = await asyncio.to_thread(search_risk, description)

        print("[STEP 2 RESULT] risks:", risks)

        if not risks:
            print("[STEP 2 WARNING] risk bulunamadı")

        # 3️⃣ LLM COMMENT
        print("\n[STEP 3] generate_comment başladı")
        comment = await asyncio.to_thread(generate_comment, description, risks)

        print("[STEP 3 RESULT] comment:", comment)

        if not comment:
            print("[STEP 3 WARNING] comment boş")
            comment = "Öneri üretilemedi"

        print("\n===== SUCCESS RESPONSE =====")

        return {"description": description, "risks": risks, "comment": comment}

    except Exception as e:
        print("\n===== ERROR OCCURRED =====")
        print("ERROR TYPE:", type(e))
        print("ERROR DETAIL:", e)

        raise HTTPException(status_code=500, detail="AI analiz hatası")
