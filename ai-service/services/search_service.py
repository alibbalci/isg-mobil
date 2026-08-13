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

    if suggestions_raw is None:
        return []

    # PostgreSQL driver doğrudan liste döndürürse
    if isinstance(suggestions_raw, list):

        normalized = []

        for item in suggestions_raw:

            if not item:
                continue

            item = str(item).strip()

            # Liste içinde PostgreSQL array string'i varsa:
            # {"Öneri 1","Öneri 2"}
            if item.startswith("{") and item.endswith("}"):

                item = item[1:-1]

                parts = item.split('","')

                for part in parts:
                    clean = part.strip().strip('"')

                    if clean:
                        normalized.append(clean)

            # | ile ayrılmışsa
            elif "|" in item:

                normalized.extend(
                    [part.strip() for part in item.split("|") if part.strip()]
                )

            else:
                normalized.append(item)

        return normalized

    # String geldiyse
    if isinstance(suggestions_raw, str):

        raw = suggestions_raw.strip()

        if not raw:
            return []

        # PostgreSQL array biçimi:
        # {"Öneri 1","Öneri 2"}
        if raw.startswith("{") and raw.endswith("}"):

            raw = raw[1:-1]

            return [
                part.strip().strip('"') for part in raw.split('","') if part.strip()
            ]

        # Eski | formatını da destekle
        if "|" in raw:

            return [part.strip() for part in raw.split("|") if part.strip()]

        return [raw]

    return []


def search_risk(query: str, threshold: float = 0.45):

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

            print("---------------------")
            print("CODE:", code)
            print("NAME:", name)
            print("SCORE:", score)

            if score < threshold:

                print("❌ BELOW THRESHOLD")
                continue

            print("✅ PASSED THRESHOLD")

            suggestions = normalize_suggestions(suggestions_raw)

            print("SUGGESTIONS:", suggestions)

            results.append(
                {
                    "code": code,
                    "name": name,
                    "damage": damage,
                    "suggestions": suggestions,
                    "score": score,
                }
            )

        print("\n--- FINAL SELECTED ---")

        for risk in results:
            print(risk["code"], risk["score"])

        return results

    finally:

        cur.close()
        db_pool.putconn(conn)
