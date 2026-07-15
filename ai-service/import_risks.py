import pandas as pd
import psycopg2
from config import DB_CONFIG

EXCEL_PATH = "tehlikeveoneri.xlsx"

REQUIRED_COLUMNS = [
    "tehlike_kodu",
    "tehlike_adi",
    "kategori",
    "olasi_zarar",
    "oneri_listesi",
    "olasilik",
    "siddet",
    "sorumlu_kisi",
    "duzeltme_suresi_gun",
    "onlem_sonrasi_olasilik",
    "onlem_sonrasi_siddet",
]


def to_int_or_none(value):
    if value == "" or pd.isna(value):
        return None
    return int(value)


def parse_oneri_listesi(value):
    value = str(value).strip()

    if not value:
        return []

    return [item.strip() for item in value.split("|") if item.strip()]


def main():
    df = pd.read_excel(EXCEL_PATH, engine="openpyxl")

    df.columns = df.columns.str.strip()
    df = df.fillna("")

    missing_columns = [col for col in REQUIRED_COLUMNS if col not in df.columns]

    if missing_columns:
        raise ValueError(f"Excel içinde eksik kolonlar var: {missing_columns}")

    print(f"Toplam Excel satırı: {len(df)}")

    conn = psycopg2.connect(**DB_CONFIG)
    cur = conn.cursor()

    inserted = 0
    skipped = 0
    failed = 0

    for i, row in df.iterrows():
        try:
            oneri_listesi = parse_oneri_listesi(row["oneri_listesi"])

            cur.execute(
                """
                INSERT INTO risk_catalog (
                    tehlike_kodu,
                    tehlike_adi,
                    kategori,
                    olasi_zarar,
                    oneri_listesi,
                    olasilik,
                    siddet,
                    sorumlu_kisi,
                    duzeltme_suresi_gun,
                    onlem_sonrasi_olasilik,
                    onlem_sonrasi_siddet
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (tehlike_kodu) DO NOTHING;
                """,
                (
                    str(row["tehlike_kodu"]).strip(),
                    str(row["tehlike_adi"]).strip(),
                    str(row["kategori"]).strip(),
                    str(row["olasi_zarar"]).strip(),
                    oneri_listesi,
                    to_int_or_none(row["olasilik"]),
                    to_int_or_none(row["siddet"]),
                    str(row["sorumlu_kisi"]).strip(),
                    to_int_or_none(row["duzeltme_suresi_gun"]),
                    to_int_or_none(row["onlem_sonrasi_olasilik"]),
                    to_int_or_none(row["onlem_sonrasi_siddet"]),
                ),
            )

            if cur.rowcount == 1:
                inserted += 1
            else:
                skipped += 1

        except Exception as e:
            print(f"❌ Hata satır {i + 2}: {e}")
            failed += 1

    conn.commit()
    cur.close()
    conn.close()

    print("================================")
    print(f"✔ Eklenen: {inserted}")
    print(f"↪ Zaten vardı / geçildi: {skipped}")
    print(f"❌ Hatalı: {failed}")
    print("✔ Import tamamlandı.")


if __name__ == "__main__":
    main()
