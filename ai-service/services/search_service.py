from sentence_transformers import SentenceTransformer
import psycopg2
from psycopg2 import pool
from config import DB_CONFIG

# 🔥 MODELİ SADECE 1 KEZ YÜKLE
model = SentenceTransformer(
    "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
)

# 🔥 CONNECTION POOL
db_pool = psycopg2.pool.SimpleConnectionPool(1, 5, **DB_CONFIG)


def search_risk(query: str, threshold: float = 0.6, top_k: int = 5):

    print("\n===== SEARCH RISK DEBUG =====")
    print("QUERY:", query)

    # 1️⃣ embedding
    embedding = model.encode(query).tolist()
    print("EMBEDDING LENGTH:", len(embedding))

    conn = db_pool.getconn()
    cur = conn.cursor()

    try:
        # 🔥 filtresiz çek → gerçek skorları görmek için
        cur.execute(
            """
            SELECT tehlike_kodu,
                   tehlike_adi,
                   olasi_zarar,
                   oneri_listesi,
                   1 - (embedding <=> %s::vector) AS score
            FROM risk_catalog
            ORDER BY embedding <=> %s::vector
            LIMIT 10
            """,
            (embedding, embedding),
        )

        rows = cur.fetchall()

        print("\n--- RAW RESULTS (THRESHOLD YOK) ---")

        results = []

        for r in rows:
            code = r[0]
            name = r[1]
            damage = r[2]
            suggestions_raw = r[3]
            score = float(r[4])

            print(
                f"""
CODE: {code}
NAME: {name}
SCORE: {score}
---------------------
"""
            )

            # 🔥 threshold kontrol
            if score >= threshold:
                print("✅ PASSED THRESHOLD")

                # 🔥 suggestions FIX
                if isinstance(suggestions_raw, str):
                    suggestions = [s.strip() for s in suggestions_raw.split("|")]
                elif isinstance(suggestions_raw, list):
                    suggestions = suggestions_raw
                else:
                    suggestions = []

                results.append(
                    {
                        "code": code,
                        "name": name,
                        "damage": damage,
                        "suggestions": suggestions,
                        "score": score,
                    }
                )
            else:
                print("❌ BELOW THRESHOLD")

        print("\n--- FINAL SELECTED ---")
        print(results)

        return results[:top_k]

    finally:
        cur.close()
        db_pool.putconn(conn)
