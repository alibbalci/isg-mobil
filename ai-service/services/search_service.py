from sentence_transformers import SentenceTransformer
import psycopg2
from psycopg2 import pool
from config import DB_CONFIG

# Model sadece 1 kez yüklenir
model = SentenceTransformer(
    "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
)

# Connection pool
db_pool = psycopg2.pool.SimpleConnectionPool(1, 5, **DB_CONFIG)


def normalize_suggestions(suggestions_raw):
    if isinstance(suggestions_raw, str):
        return [s.strip() for s in suggestions_raw.split("|") if s.strip()]

    if isinstance(suggestions_raw, list):
        return [str(s).strip() for s in suggestions_raw if str(s).strip()]

    return []


def search_risk(query: str, threshold: float = 0.5, top_k: int = 3):
    print("\n===== SEARCH RISK DEBUG =====")
    print("QUERY:", query)

    if not query or not query.strip():
        return []

    # 1. Query embedding
    embedding = model.encode(query).tolist()
    print("EMBEDDING LENGTH:", len(embedding))

    conn = db_pool.getconn()
    cur = conn.cursor()

    try:
        cur.execute(
            """
            SELECT 
                tehlike_kodu,
                tehlike_adi,
                olasi_zarar,
                oneri_listesi,
                1 - (embedding <=> %s::vector) AS score
            FROM risk_catalog
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> %s::vector
            LIMIT 10
            """,
            (embedding, embedding),
        )

        rows = cur.fetchall()

        print("\n--- RAW RESULTS ---")

        results = []

        for row in rows:
            code = row[0]
            name = row[1]
            damage = row[2]
            suggestions_raw = row[3]
            score = float(row[4])

            print(f"""
CODE: {code}
NAME: {name}
SCORE: {score}
---------------------
""")

            if score < threshold:
                print("❌ BELOW THRESHOLD")
                continue

            print("✅ PASSED THRESHOLD")

            results.append(
                {
                    "code": code,
                    "name": name,
                    "damage": damage,
                    "suggestions": normalize_suggestions(suggestions_raw),
                    "score": score,
                }
            )

        selected = results[:top_k]

        print("\n--- FINAL SELECTED ---")
        print(selected)

        return selected

    finally:
        cur.close()
        db_pool.putconn(conn)
