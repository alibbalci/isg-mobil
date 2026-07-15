from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Any
import asyncio

from services.analyze_image import analyze_image
from services.search_service import search_risk
from services.llm_service import generate_comment

app = FastAPI(title="İSG Mobil AI Service")


class AnalyzeRequest(BaseModel):
    imageUrl: str


class AnalyzeResponse(BaseModel):
    description: str
    risks: List[Dict[str, Any]]


class FormatCommentRequest(BaseModel):
    riskName: str
    damage: str
    suggestions: List[str]


class FormatCommentResponse(BaseModel):
    formattedComment: str


@app.post("/analyze", response_model=AnalyzeResponse)
async def analyze(request: AnalyzeRequest):

    if not request.imageUrl or not request.imageUrl.strip():
        raise HTTPException(status_code=400, detail="imageUrl boş olamaz")

    try:
        print("\n===== NEW ANALYZE REQUEST =====")
        print("IMAGE URL:", request.imageUrl)

        # 1. IMAGE ANALYSIS
        print("\n[STEP 1] analyze_image başladı")
        description = await asyncio.to_thread(analyze_image, request.imageUrl)

        print("[STEP 1 RESULT] description:", description)

        if not description or not description.strip():
            print("[STEP 1 WARNING] description boş, fallback kullanılıyor")
            description = "genel iş güvenliği riski"

        # 2. RISK SEARCH
        print("\n[STEP 2] search_risk başladı")
        risks = await asyncio.to_thread(search_risk, description)

        print("[STEP 2 RESULT] risks:", risks)

        if not risks:
            print("[STEP 2 WARNING] risk bulunamadı")

        print("\n===== ANALYZE SUCCESS RESPONSE =====")

        return {"description": description, "risks": risks}

    except Exception as e:
        print("\n===== ANALYZE ERROR OCCURRED =====")
        print("ERROR TYPE:", type(e))
        print("ERROR DETAIL:", e)

        raise HTTPException(status_code=500, detail="AI analiz hatası")


@app.post("/format-comment", response_model=FormatCommentResponse)
async def format_comment(request: FormatCommentRequest):

    if not request.riskName or not request.riskName.strip():
        raise HTTPException(status_code=400, detail="riskName boş olamaz")

    if not request.suggestions:
        raise HTTPException(status_code=400, detail="suggestions boş olamaz")

    try:
        print("\n===== NEW FORMAT COMMENT REQUEST =====")
        print("RISK NAME:", request.riskName)
        print("DAMAGE:", request.damage)
        print("SUGGESTIONS:", request.suggestions)

        comment = await asyncio.to_thread(
            generate_comment, request.riskName, request.damage, request.suggestions
        )

        print("[FORMAT COMMENT RESULT]:", comment)

        if not comment or not comment.strip():
            comment = "Öneri paragrafı üretilemedi."

        return {"formattedComment": comment}

    except Exception as e:
        print("\n===== FORMAT COMMENT ERROR OCCURRED =====")
        print("ERROR TYPE:", type(e))
        print("ERROR DETAIL:", e)

        raise HTTPException(status_code=500, detail="Öneri paragrafı üretilemedi")
