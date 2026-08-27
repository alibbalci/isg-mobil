from sentence_transformers import SentenceTransformer
import psycopg2

from config import DB_CONFIG


MODEL_NAME = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"


def build_embedding_text(name, category, damage):
    return f"{name}. Kategori: {category}. Zarar: {damage}"


def main():
    model = SentenceTransformer(MODEL_NAME)

    with psycopg2.connect(**DB_CONFIG) as conn:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT tehlike_kodu, tehlike_adi, kategori, olasi_zarar
                FROM risk_catalog
                ORDER BY tehlike_kodu
                """
            )
            rows = cur.fetchall()

            texts = [
                build_embedding_text(name or "", category or "", damage or "")
                for _, name, category, damage in rows
            ]
            embeddings = model.encode(texts, batch_size=32, show_progress_bar=True)

            for (code, *_), embedding_text, embedding in zip(rows, texts, embeddings):
                cur.execute(
                    """
                    UPDATE risk_catalog
                    SET embedding_text = %s, embedding = %s::vector
                    WHERE tehlike_kodu = %s
                    """,
                    (embedding_text, embedding.tolist(), code),
                )

    print(f"Embedding oluşturulan/güncellenen kayıt: {len(rows)}")


if __name__ == "__main__":
    main()
