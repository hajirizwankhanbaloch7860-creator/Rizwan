import urllib.request
import json
import os
import sys

def download_with_retry(url, retries=5):
    for i in range(retries):
        try:
            print(f"Fetching {url} (Attempt {i+1}/{retries})...")
            req = urllib.request.Request(
                url, 
                headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'}
            )
            with urllib.request.urlopen(req, timeout=30) as response:
                return json.loads(response.read().decode('utf-8'))
        except Exception as e:
            print(f"Error: {e}")
            if i == retries - 1:
                raise e

try:
    print("Starting Quran data download...")
    arabic_data = download_with_retry("https://api.alquran.cloud/v1/quran/quran-uthmani")
    english_data = download_with_retry("https://api.alquran.cloud/v1/quran/en.sahih")
    urdu_data = download_with_retry("https://api.alquran.cloud/v1/quran/ur.jalandhry")

    print("Merging translations into a unified database...")
    merged_surahs = []
    
    for s_idx in range(114):
        ar_surah = arabic_data['data']['surahs'][s_idx]
        en_surah = english_data['data']['surahs'][s_idx]
        ur_surah = urdu_data['data']['surahs'][s_idx]
        
        surah_num = ar_surah['number']
        
        merged_ayahs = []
        for a_idx in range(len(ar_surah['ayahs'])):
            ar_ayah = ar_surah['ayahs'][a_idx]
            en_ayah = en_surah['ayahs'][a_idx]
            ur_ayah = ur_surah['ayahs'][a_idx]
            
            merged_ayahs.append({
                "numberInSurah": ar_ayah['numberInSurah'],
                "textArabic": ar_ayah['text'],
                "textEnglish": en_ayah['text'],
                "textUrdu": ur_ayah['text']
            })
            
        merged_surahs.append({
            "number": surah_num,
            "name": ar_surah['name'],
            "englishName": ar_surah['englishName'],
            "revelationType": ar_surah['revelationType'],
            "ayahs": merged_ayahs
        })

    output = {"surahs": merged_surahs}
    os.makedirs("/app/src/main/assets", exist_ok=True)
    with open("/app/src/main/assets/quran_offline.json", "w", encoding="utf-8") as f:
        json.dump(output, f, ensure_ascii=False, indent=2)

    print("Success! Created /app/src/main/assets/quran_offline.json")
    sys.exit(0)
except Exception as e:
    print(f"Fatal script execution error: {e}")
    sys.exit(1)
