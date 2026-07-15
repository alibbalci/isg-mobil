from google import genai
from config import GEMINI_API_KEY
import requests
import base64
from fastapi import HTTPException

# API Anahtarının doğru yüklenip yüklenmediğini kontrol etmek için terminale yazdırıyoruz
print(f"BAŞLATILAN API KEY (İlk 10 Hane): {GEMINI_API_KEY[:10]}...")

client = genai.Client(api_key=GEMINI_API_KEY)


def analyze_image(image_url: str):
    try:
        # 1️⃣ image indir
        response = requests.get(image_url, timeout=5)

        if response.status_code != 200:
            raise HTTPException(
                status_code=400,
                detail="Görsel indirilemedi. Lütfen URL'yi kontrol edin.",
            )

        image_bytes = response.content
        print("IMAGE SIZE:", len(image_bytes))

        # 2️⃣ base64 encode (KRİTİK)
        image_base64 = base64.b64encode(image_bytes).decode("utf-8")

        prompt = """
Sen deneyimli bir iş güvenliği uzmanısın.

Görevin:
Verilen görseldeki iş sağlığı ve güvenliği açısından dikkat edilmesi gereken tehlikeli durumu profesyonel gözlem diliyle açıklamak.

Kurallar:
- Görselde gerçekten görülen durumları yaz.
- Gereksiz yorum veya tahmin ekleme.
- Çok kısa cevap verme.
- Çok uzun rapor yazma.
- Teknik ve profesyonel bir gözlem cümlesi kur.
- Risk puanı veya çözüm önerisi verme.
- Sadece gözlem yaz.

Çıktı formatı:
1-2 cümlelik profesyonel saha gözlemi.
"""

        # 3️⃣ GEMINI CALL
        result = client.models.generate_content(
            model="gemini-2.5-flash",
            contents=[
                {
                    "role": "user",
                    "parts": [
                        {"text": prompt},
                        {
                            "inline_data": {
                                "mime_type": "image/jpeg",
                                "data": image_base64,
                            }
                        },
                    ],
                }
            ],
        )

        print("GEMINI RESPONSE:", result)

        # 4️⃣ güvenli text alma
        if result and result.text:
            return result.text.strip()

        return "Görsel analiz edilemedi."

    except HTTPException as http_exc:
        # Fırlattığımız 400 hatasını ezmemek için yukarı iletiyoruz.
        raise http_exc

    except Exception as e:
        error_msg = str(e)
        print("AI ERROR:", error_msg)

        # Eğer hata 429 Kotaya takılma hatasıysa (RESOURCE_EXHAUSTED)
        if "429" in error_msg or "RESOURCE_EXHAUSTED" in error_msg:
            raise HTTPException(
                status_code=429,
                detail="Yapay zeka analiz kotası doldu. Lütfen 1 dakika bekleyip tekrar deneyin.",
            )

        # Eğer başka, beklenmeyen bir hata oluşursa
        raise HTTPException(
            status_code=500,
            detail="Analiz işlemi sırasında sunucu tabanlı bir hata oluştu.",
        )
