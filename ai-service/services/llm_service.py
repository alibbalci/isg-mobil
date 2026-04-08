from google import genai
from config import GEMINI_API_KEY

client = genai.Client(api_key=GEMINI_API_KEY)


def generate_comment(description, risks):

    risk_text = ""

    for r in risks:
        risk_text += f"""
Risk: {r.get('name')}
Olası zarar: {r.get('damage')}
Öneriler: {r.get('suggestions')}
"""

    prompt = f"""
Sen bir iş güvenliği raporlama sisteminde çalışan metin düzenleme asistanısın.

Aşağıda bir gözleme ait iş güvenliği önlem maddeleri verilmiştir:

{risk_text}

GÖREVİN:
Bu önlem maddelerini profesyonel iş güvenliği raporu formatında
tek bir bütünleşik "Alınması Gereken Önlemler" paragrafına dönüştür.

KURALLAR:
- SADECE verilen önlemleri kullan.
- Yeni öneri ekleme.
- Teknik anlamı değiştirme.
- Benzer maddeleri birleştir.
- Gereksiz tekrarları kaldır.
- Kurumsal/rapor dilinde yaz.
- Çıktı tek paragraf olsun.
- Madde işareti kullanma.
- Başlık ekleme.

ÇIKTI:
Sadece düzenlenmiş önlem paragrafını ver.
"""

    try:
        response = client.models.generate_content(
            model="gemini-2.5-flash", contents=prompt
        )

        return response.text

    except Exception as e:
        print("LLM ERROR:", e)
        return "Risk analizi yapılamadı"
