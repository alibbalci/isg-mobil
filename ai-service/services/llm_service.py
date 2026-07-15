from google import genai
from config import GEMINI_API_KEY

client = genai.Client(api_key=GEMINI_API_KEY)


def generate_comment(risk_name: str, damage: str, suggestions):
    """
    Seçilen tek riskin önerilerini profesyonel rapor paragrafına dönüştürür.
    Bu fonksiyon yeni öneri üretmez, sadece verilen önerileri düzenler.
    """

    if not suggestions:
        return "Bu risk için tanımlı öneri bulunamadı."

    if isinstance(suggestions, list):
        suggestions_text = "\n".join([f"- {s}" for s in suggestions if str(s).strip()])
    else:
        suggestions_text = str(suggestions).replace("|", "\n- ")

    prompt = f"""
Sen bir iş güvenliği raporlama sisteminde çalışan metin düzenleme asistanısın.

Aşağıda seçilmiş TEK bir iş güvenliği riski ve bu riske ait katalog önerileri verilmiştir.

Risk adı:
{risk_name}

Olası zarar:
{damage}

Verilen öneriler:
{suggestions_text}

GÖREVİN:
Sadece verilen önerileri kullanarak profesyonel iş güvenliği raporu dilinde
tek bir "Alınması Gereken Önlemler" paragrafı oluştur.

KURALLAR:
- SADECE verilen önerileri kullan.
- Yeni öneri ekleme.
- Mevzuat, eğitim, periyodik kontrol gibi inputta olmayan öneriler ekleme.
- Teknik anlamı değiştirme.
- Olası zararı öneri gibi yazma.
- Benzer maddeleri anlamı bozmadan birleştir.
- Gereksiz tekrarları kaldır.
- Kurumsal ve resmi rapor dilinde yaz.
- Çıktı tek paragraf olsun.
- Madde işareti kullanma.
- Başlık ekleme.
- Risk puanı, olasılık veya şiddet yazma.

ÇIKTI:
Sadece düzenlenmiş önlem paragrafını ver.
"""

    try:
        response = client.models.generate_content(
            model="gemini-2.5-flash", contents=prompt
        )

        if not response.text or not response.text.strip():
            return "Öneri paragrafı üretilemedi."

        return response.text.strip()

    except Exception as e:
        print("LLM ERROR:", e)
        return "Öneri paragrafı üretilemedi."
