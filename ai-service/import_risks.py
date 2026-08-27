import pandas as pd
import psycopg2
from pathlib import Path
from config import DB_CONFIG

EXCEL_PATH = Path(__file__).resolve().parent / "data" / "tehlikeveoneri.xlsx"

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
    "risk_grubu",
]

RISK_LEVEL_MAP = {
    "Düşük Risk": "LOW",
    "Orta Risk": "MEDIUM",
    "Yüksek Risk": "HIGH",
    "Kritik Risk": "CRITICAL",
}


def to_int_or_none(value):
    if value == "" or pd.isna(value):
        return None
    return int(value)


def parse_oneri_listesi(value):
    value = str(value).strip()

    if not value:
        return []

    return [item.strip() for item in value.split("|") if item.strip()]


def parse_risk_level(value):
    risk_group = str(value).strip()
    try:
        return RISK_LEVEL_MAP[risk_group]
    except KeyError as exc:
        raise ValueError(f"Geçersiz risk_grubu: {risk_group!r}") from exc


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

    processed = 0
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
                    onlem_sonrasi_siddet,
                    risk_level
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (tehlike_kodu) DO UPDATE
                SET risk_level = EXCLUDED.risk_level;
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
                    parse_risk_level(row["risk_grubu"]),
                ),
            )

            if cur.rowcount == 1:
                processed += 1

        except Exception as e:
            print(f"❌ Hata satır {i + 2}: {e}")
            failed += 1

    conn.commit()
    cur.close()
    conn.close()

    print("================================")
    print(f"✔ Eklenen/güncellenen: {processed}")
    print(f"❌ Hatalı: {failed}")
    print("✔ Import tamamlandı.")


if __name__ == "__main__":
    main()
