import pandas as pd
import psycopg2

# ==============================
# 🔧 AYARLAR
# ==============================
EXCEL_PATH = "tehlikeveoneri.xlsx"

DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "database": "isg_db",
    "user": "postgres",
    "password": "1234",
}

# ==============================
# 🔌 DB BAĞLANTI
# ==============================
conn = psycopg2.connect(**DB_CONFIG)
cur = conn.cursor()

# ==============================
# 📥 EXCEL OKUMA
# ==============================
df = pd.read_excel(EXCEL_PATH, engine="openpyxl")

# 🔥 kolon hatalarını temizle
df.columns = df.columns.str.strip()

# 🔥 boş değerleri normalize et
df = df.fillna("")

print(f"Toplam kayıt: {len(df)}")

# ==============================
# 🔄 DATA INSERT
# ==============================
success = 0
fail = 0

for i, row in df.iterrows():
    try:
        # 🔥 oneri_listesi parse
        oneri_raw = str(row["oneri_listesi"]).strip()
        if oneri_raw:
            oneri_listesi = [x.strip() for x in oneri_raw.split("|") if x.strip()]
        else:
            oneri_listesi = []

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
                row["tehlike_kodu"],
                row["tehlike_adi"],
                row["kategori"],
                row["olasi_zarar"],
                oneri_listesi,
                int(row["olasilik"]) if row["olasilik"] != "" else None,
                int(row["siddet"]) if row["siddet"] != "" else None,
                row["sorumlu_kisi"],
                (
                    int(row["duzeltme_suresi_gun"])
                    if row["duzeltme_suresi_gun"] != ""
                    else None
                ),
                (
                    int(row["onlem_sonrasi_olasilik"])
                    if row["onlem_sonrasi_olasilik"] != ""
                    else None
                ),
                (
                    int(row["onlem_sonrasi_siddet"])
                    if row["onlem_sonrasi_siddet"] != ""
                    else None
                ),
            ),
        )

        success += 1

    except Exception as e:
        print(f"❌ Hata (satır {i}): {e}")
        fail += 1

# ==============================
# 💾 COMMIT
# ==============================
conn.commit()

cur.close()
conn.close()

# ==============================
# 📊 RAPOR
# ==============================
print("================================")
print(f"✔ Başarılı: {success}")
print(f"❌ Hatalı: {fail}")
print("✔ Import tamamlandı.")
