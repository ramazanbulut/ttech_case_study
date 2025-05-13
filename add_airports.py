import csv
import requests

with open("airports.csv", newline='', encoding='utf-8') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        if row['iso_country'] and row['iata_code']:  # Türkiye ve IATA kodu olanlar
            data = {
                "name": row['name'],
                "country": row['iso_country'],
                "city": row['municipality'],
                "locationCode": row['iata_code'].lower()
            }
            response = requests.post(
                "http://localhost:8080/api/locations",
                json=data,
                headers={
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }
            )
            print(f"{data['locationCode'].upper()} gönderildi: {response.status_code}")
