from generate_embedding import search_risk

if __name__ == "__main__":
    query = input("Risk tanımı gir: ")
    results = search_risk(query)

    for r in results:
        print(r)
