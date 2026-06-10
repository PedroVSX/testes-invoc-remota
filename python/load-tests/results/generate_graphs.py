import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

# Base paths
CSV_STATS = "/home/mandreamorim/IdeaProjects/testes-invoc-remota-java/python/load-tests/results/consolidated_stats.csv"
CSV_HISTORY = "/home/mandreamorim/IdeaProjects/testes-invoc-remota-java/python/load-tests/results/consolidated_stats_history.csv"
OUTPUT_DIR = "/home/mandreamorim/IdeaProjects/testes-invoc-remota-java/python/load-tests/results/graphs"
STABILITY_DIR = os.path.join(OUTPUT_DIR, "stability")

# Global Config
JAVA_COLOR = '#f8981d'
PYTHON_COLOR = '#3776ab'
COLORS = {'Java': JAVA_COLOR, 'Python': PYTHON_COLOR}

def ensure_dirs():
    for d in [OUTPUT_DIR, STABILITY_DIR]:
        if not os.path.exists(d):
            os.makedirs(d)

def generate_performance_average():
    df = pd.read_csv(CSV_STATS)
    df_agg = df[df['Name'] == 'Aggregated'].copy()
    df_agg['Protocol'] = df_agg['Protocol'].str.upper()
    df_agg['Language'] = df_agg['Language'].str.capitalize()
    
    intensities = df_agg['Intensity'].unique()
    protocols = sorted(df_agg['Protocol'].unique())
    languages = ['Java', 'Python']

    for intensity in intensities:
        plt.figure(figsize=(10, 6))
        x = np.arange(len(protocols))
        width = 0.35
        intensity_data = df_agg[df_agg['Intensity'] == intensity]
        
        for i, lang in enumerate(languages):
            values = [intensity_data[(intensity_data['Language'] == lang) & (intensity_data['Protocol'] == proto)]['Average Response Time'].iloc[0] 
                      if not intensity_data[(intensity_data['Language'] == lang) & (intensity_data['Protocol'] == proto)].empty else 0 
                      for proto in protocols]
            
            offset = (i - 0.5) * width + width/2
            bars = plt.bar(x + offset, values, width, label=lang, color=COLORS[lang], edgecolor='black', alpha=0.8)
            for bar in bars:
                plt.text(bar.get_x() + bar.get_width()/2., bar.get_height() + 0.1, f'{bar.get_height():.2f}', ha='center', va='bottom', fontsize=9)

        plt.title(f'Tempo Médio de Resposta - Carga: {intensity.capitalize()}', fontsize=14)
        plt.ylabel('Tempo (ms)')
        plt.xticks(x, protocols)
        plt.legend()
        plt.grid(axis='y', linestyle='--', alpha=0.5)
        plt.tight_layout()
        plt.savefig(os.path.join(OUTPUT_DIR, f"performance_avg_{intensity}.png"), dpi=300)
        plt.close()

def generate_performance_p95():
    df = pd.read_csv(CSV_STATS)
    df_agg = df[df['Name'] == 'Aggregated'].copy()
    df_agg['Protocol'] = df_agg['Protocol'].str.upper()
    df_agg['Language'] = df_agg['Language'].str.capitalize()
    
    intensities = df_agg['Intensity'].unique()
    protocols = sorted(df_agg['Protocol'].unique())
    languages = ['Java', 'Python']

    for intensity in intensities:
        plt.figure(figsize=(10, 6))
        x = np.arange(len(protocols))
        width = 0.35
        intensity_data = df_agg[df_agg['Intensity'] == intensity]
        
        for i, lang in enumerate(languages):
            values = [intensity_data[(intensity_data['Language'] == lang) & (intensity_data['Protocol'] == proto)]['95%'].iloc[0] 
                      if not intensity_data[(intensity_data['Language'] == lang) & (intensity_data['Protocol'] == proto)].empty else 0 
                      for proto in protocols]
            
            offset = (i - 0.5) * width + width/2
            bars = plt.bar(x + offset, values, width, label=lang, color=COLORS[lang], edgecolor='black', alpha=0.8)
            for bar in bars:
                plt.text(bar.get_x() + bar.get_width()/2., bar.get_height() + 0.1, f'{bar.get_height():.0f}', ha='center', va='bottom', fontsize=9)

        plt.title(f'Latência P95 - Carga: {intensity.capitalize()}', fontsize=14)
        plt.ylabel('Tempo (ms)')
        plt.xticks(x, protocols)
        plt.legend()
        plt.grid(axis='y', linestyle='--', alpha=0.5)
        plt.tight_layout()
        plt.savefig(os.path.join(OUTPUT_DIR, f"performance_p95_{intensity}.png"), dpi=300)
        plt.close()

def generate_error_python():
    df = pd.read_csv(CSV_STATS)
    df_agg = df[df['Name'] == 'Aggregated'].copy()
    df_agg['Error Rate (%)'] = (df_agg['Failure Count'] / df_agg['Request Count']) * 100
    df_agg['Protocol'] = df_agg['Protocol'].str.upper()
    df_agg = df_agg[df_agg['Language'].str.capitalize() == 'Python']
    
    intensities = df_agg['Intensity'].unique()
    protocols = sorted(df_agg['Protocol'].unique())

    for intensity in intensities:
        plt.figure(figsize=(10, 6))
        intensity_data = df_agg[df_agg['Intensity'] == intensity]
        values = [intensity_data[intensity_data['Protocol'] == proto]['Error Rate (%)'].iloc[0] 
                  if not intensity_data[intensity_data['Protocol'] == proto].empty else 0 
                  for proto in protocols]
        
        bars = plt.bar(protocols, values, color=PYTHON_COLOR, edgecolor='black', alpha=0.8, width=0.5)
        for bar in bars:
            plt.text(bar.get_x() + bar.get_width()/2., bar.get_height() + 0.5, f'{bar.get_height():.2f}%', ha='center', va='bottom')

        plt.title(f'Taxa de Erro Python - Carga: {intensity.capitalize()}', fontsize=14)
        plt.ylabel('Falhas (%)')
        plt.grid(axis='y', linestyle='--', alpha=0.5)
        plt.ylim(0, max(10, max(values)*1.2 if values else 10))
        plt.tight_layout()
        plt.savefig(os.path.join(OUTPUT_DIR, f"error_python_{intensity}.png"), dpi=300)
        plt.close()

def generate_soap_trend():
    df = pd.read_csv(CSV_STATS)
    df_soap = df[(df['Name'] == 'Aggregated') & (df['Protocol'].str.upper() == 'SOAP') & (df['Language'].str.capitalize() == 'Python')].copy()
    df_soap['Error Rate (%)'] = (df_soap['Failure Count'] / df_soap['Request Count']) * 100
    
    load_map = {'leve': 1000, 'medio': 2000, 'pesado': 4000}
    df_soap['Users'] = df_soap['Intensity'].map(load_map)
    df_soap = df_soap.sort_values('Users')
    
    plt.figure(figsize=(10, 6))
    plt.plot(df_soap['Users'], df_soap['Error Rate (%)'], marker='o', linewidth=2, color=PYTHON_COLOR)
    for x, y in zip(df_soap['Users'], df_soap['Error Rate (%)']):
        plt.text(x, y + 1, f'{y:.2f}%', ha='center', fontweight='bold')

    plt.title('Tendência de Erro SOAP Python', fontsize=14)
    plt.xlabel('Usuários Simultâneos')
    plt.ylabel('Falhas (%)')
    plt.xticks([1000, 2000, 4000])
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "trend_error_soap_python.png"), dpi=300)
    plt.close()

def generate_content_size():
    df = pd.read_csv(CSV_STATS)
    # Using 'leve' as representative since size is constant
    df_size = df[(df['Name'] == 'Aggregated') & (df['Intensity'] == 'leve')].copy()
    df_size['Protocol'] = df_size['Protocol'].str.upper()
    df_size['Language'] = df_size['Language'].str.capitalize()
    
    protocols = sorted(df_size['Protocol'].unique())
    languages = ['Java', 'Python']

    plt.figure(figsize=(10, 6))
    x = np.arange(len(protocols))
    width = 0.35
    
    for i, lang in enumerate(languages):
        values = [df_size[(df_size['Language'] == lang) & (df_size['Protocol'] == proto)]['Average Content Size'].iloc[0]
                  if not df_size[(df_size['Language'] == lang) & (df_size['Protocol'] == proto)].empty else 0
                  for proto in protocols]
        
        offset = (i - 0.5) * width + width/2
        bars = plt.bar(x + offset, values, width, label=lang, color=COLORS[lang], edgecolor='black', alpha=0.8)
        for bar in bars:
            plt.text(bar.get_x() + bar.get_width()/2., bar.get_height() + 5, f'{bar.get_height():.0f}', ha='center', va='bottom', fontsize=9)

    plt.title('Tamanho Médio do Payload (Bytes)', fontsize=14)
    plt.ylabel('Bytes')
    plt.xticks(x, protocols)
    plt.legend()
    plt.grid(axis='y', linestyle='--', alpha=0.5)
    plt.tight_layout()
    plt.savefig(os.path.join(OUTPUT_DIR, "content_size_comparison.png"), dpi=300)
    plt.close()

def generate_stability():
    df = pd.read_csv(CSV_HISTORY)
    df_stress = df[(df['Name'] == 'Aggregated') & (df['Intensity'] == 'pesado')].copy()
    protocols = sorted(df_stress['Protocol'].unique())
    
    for proto in protocols:
        plt.figure(figsize=(12, 6))
        for lang in ['java', 'python']:
            data = df_stress[(df_stress['Protocol'] == proto) & (df_stress['Language'] == lang)].sort_values('Timestamp')
            if data.empty: continue
            
            relative_time = data['Timestamp'] - data['Timestamp'].min()
            plt.plot(relative_time, data['Total Average Response Time'], label=lang.capitalize(), color=COLORS[lang.capitalize()], linewidth=2)

        plt.title(f'Estabilidade de Resposta (Carga Pesada) - {proto.upper()}', fontsize=14)
        plt.xlabel('Tempo (s)')
        plt.ylabel('Tempo Resposta Médio (ms)')
        plt.legend()
        plt.grid(True, alpha=0.3)
        plt.tight_layout()
        plt.savefig(os.path.join(STABILITY_DIR, f"stability_{proto}.png"), dpi=300)
        plt.close()

if __name__ == "__main__":
    ensure_dirs()
    print("Gerando gráficos...")
    generate_performance_average()
    generate_performance_p95()
    generate_error_python()
    generate_soap_trend()
    generate_content_size()
    generate_stability()
    print("Todos os gráficos foram gerados com sucesso.")
