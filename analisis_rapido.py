"""
Script simple de análisis rápido
Para generar estadísticas básicas sin gráficos
"""

import pandas as pd
from pathlib import Path

def analisis_rapido():
    """Análisis rápido sin generar gráficos"""
    carpeta = Path("datos_analisis")
    
    print("\n" + "="*70)
    print(" "*20 + "ANÁLISIS RÁPIDO DE DATOS")
    print("="*70)
    
    # Cargar datos
    try:
        df_partidas = pd.read_csv(carpeta / "resumen_partidas.csv", encoding='utf-8')
        df_jugadores = pd.read_csv(carpeta / "estadisticas_jugadores.csv", encoding='utf-8')
        df_eventos = pd.read_csv(carpeta / "eventos_partidas.csv", encoding='utf-8')
    except FileNotFoundError:
        print("\n❌ ERROR: No se encontraron los archivos CSV")
        print("   Ejecuta primero el programa Java para generar los datos\n")
        return
    
    print(f"\n📊 Total de partidas analizadas: {len(df_partidas)}")
    print(f"📊 Total de eventos registrados: {len(df_eventos)}")
    
    # ========== RESUMEN GENERAL ==========
    print("\n" + "─"*70)
    print("📈 RESUMEN GENERAL")
    print("─"*70)
    
    victorias_j1 = (df_partidas['ganador'] == 1).sum()
    victorias_j2 = (df_partidas['ganador'] == 2).sum()
    empates = (df_partidas['ganador'] == 0).sum()
    
    print(f"\nResultados:")
    print(f"  🏆 Victorias Jugador 1: {victorias_j1} ({victorias_j1/len(df_partidas)*100:.1f}%)")
    print(f"  🏆 Victorias Jugador 2: {victorias_j2} ({victorias_j2/len(df_partidas)*100:.1f}%)")
    print(f"  🤝 Empates: {empates} ({empates/len(df_partidas)*100:.1f}%)")
    
    print(f"\nDuración:")
    print(f"  ⏱️  Promedio: {df_partidas['duracion_segundos'].mean():.1f} segundos ({df_partidas['duracion_segundos'].mean()/60:.1f} min)")
    print(f"  ⏱️  Mínima: {df_partidas['duracion_segundos'].min()} segundos")
    print(f"  ⏱️  Máxima: {df_partidas['duracion_segundos'].max()} segundos")
    
    # ========== POR ESTRATEGIA ==========
    print("\n" + "─"*70)
    print("🎯 ANÁLISIS POR ESTRATEGIA")
    print("─"*70)
    
    # Calcular estadísticas
    stats = df_jugadores.groupby('estrategia').agg({
        'id_partida': 'count',
        'resultado': lambda x: (x == 'VICTORIA').sum(),
        'cartas_jugadas': 'mean',
        'elixir_gastado': 'mean',
        'danio_causado': 'mean',
        'torres_destruidas': 'mean'
    }).round(2)
    
    stats['tasa_victoria'] = (stats['resultado'] / stats['id_partida'] * 100).round(1)
    
    print("\n" + "┌" + "─"*68 + "┐")
    print("│ Estrategia          │ Part. │ Vict. │ Tasa │ Cartas │ Elixir │ Daño  │ Torres │")
    print("├" + "─"*68 + "┤")
    
    for estrategia in stats.index:
        row = stats.loc[estrategia]
        print(f"│ {estrategia:19s} │ {row['id_partida']:5.0f} │ {row['resultado']:5.0f} │ {row['tasa_victoria']:4.1f}% │"
              f" {row['cartas_jugadas']:6.1f} │ {row['elixir_gastado']:6.1f} │ {row['danio_causado']:5.0f} │"
              f" {row['torres_destruidas']:6.2f} │")
    
    print("└" + "─"*68 + "┘")
    
    # Mejor estrategia
    mejor = stats['tasa_victoria'].idxmax()
    peor = stats['tasa_victoria'].idxmin()
    
    print(f"\n🏆 Mejor estrategia: {mejor} ({stats.loc[mejor, 'tasa_victoria']:.1f}% victorias)")
    print(f"📉 Peor estrategia: {peor} ({stats.loc[peor, 'tasa_victoria']:.1f}% victorias)")
    
    # ========== ENFRENTAMIENTOS ==========
    print("\n" + "─"*70)
    print("⚔️  ENFRENTAMIENTOS ENTRE ESTRATEGIAS")
    print("─"*70)
    
    estrategias = sorted(df_partidas['estrategia_j1'].unique())
    
    print("\nMatriz de victorias (Fila gana a Columna):\n")
    
    # Crear matriz
    matriz = pd.DataFrame(0, index=estrategias, columns=estrategias)
    
    for _, p in df_partidas.iterrows():
        if p['ganador'] == 1:
            matriz.loc[p['estrategia_j1'], p['estrategia_j2']] += 1
        elif p['ganador'] == 2:
            matriz.loc[p['estrategia_j2'], p['estrategia_j1']] += 1
    
    # Imprimir matriz
    col_width = max(len(e) for e in estrategias) + 2
    
    print(" " * col_width + " ".join(f"{e[:8]:>8s}" for e in estrategias))
    print("─" * (col_width + len(estrategias) * 9))
    
    for estrategia in estrategias:
        valores = " ".join(f"{matriz.loc[estrategia, col]:8.0f}" for col in estrategias)
        print(f"{estrategia:{col_width}s}{valores}")
    
    # ========== EFICIENCIA ==========
    print("\n" + "─"*70)
    print("💪 ANÁLISIS DE EFICIENCIA")
    print("─"*70)
    
    df_jugadores['danio_por_elixir'] = (df_jugadores['danio_causado'] / df_jugadores['elixir_gastado']).round(2)
    df_jugadores['ratio_danio'] = (df_jugadores['danio_causado'] / df_jugadores['danio_recibido']).round(2)
    
    eficiencia = df_jugadores.groupby('estrategia').agg({
        'danio_por_elixir': 'mean',
        'ratio_danio': 'mean',
        'promedio_elixir_carta': 'mean'
    }).round(2)
    
    print("\n" + "┌" + "─"*62 + "┐")
    print("│ Estrategia          │ Daño/Elixir │ Ratio Daño │ Elixir/Carta │")
    print("├" + "─"*62 + "┤")
    
    for estrategia in eficiencia.index:
        row = eficiencia.loc[estrategia]
        print(f"│ {estrategia:19s} │ {row['danio_por_elixir']:11.2f} │ {row['ratio_danio']:10.2f} │"
              f" {row['promedio_elixir_carta']:12.2f} │")
    
    print("└" + "─"*62 + "┘")
    
    # ========== EVENTOS ==========
    print("\n" + "─"*70)
    print("📋 DISTRIBUCIÓN DE EVENTOS")
    print("─"*70)
    
    eventos_tipo = df_eventos['tipo_evento'].value_counts()
    total_eventos = len(df_eventos)
    
    print("")
    for tipo, cantidad in eventos_tipo.items():
        porcentaje = cantidad / total_eventos * 100
        barra = "█" * int(porcentaje / 2)
        print(f"{tipo:25s} │ {barra:50s} │ {cantidad:6d} ({porcentaje:5.1f}%)")
    
    # ========== MEJORES PARTIDAS ==========
    print("\n" + "─"*70)
    print("🌟 TOP 5 PARTIDAS MÁS LARGAS")
    print("─"*70)
    
    top_largas = df_partidas.nlargest(5, 'duracion_segundos')[
        ['id_partida', 'estrategia_j1', 'estrategia_j2', 'duracion_segundos', 'ganador']
    ]
    
    print("")
    for idx, row in top_largas.iterrows():
        duracion_min = row['duracion_segundos'] / 60
        ganador_str = f"J{row['ganador']}" if row['ganador'] > 0 else "Empate"
        print(f"  {row['id_partida']:20s} │ {row['estrategia_j1']:15s} vs {row['estrategia_j2']:15s} │"
              f" {duracion_min:5.1f} min │ {ganador_str}")
    
    print("\n" + "─"*70)
    print("🌟 TOP 5 PARTIDAS MÁS CORTAS")
    print("─"*70)
    
    top_cortas = df_partidas.nsmallest(5, 'duracion_segundos')[
        ['id_partida', 'estrategia_j1', 'estrategia_j2', 'duracion_segundos', 'ganador']
    ]
    
    print("")
    for idx, row in top_cortas.iterrows():
        duracion_min = row['duracion_segundos'] / 60
        ganador_str = f"J{row['ganador']}" if row['ganador'] > 0 else "Empate"
        print(f"  {row['id_partida']:20s} │ {row['estrategia_j1']:15s} vs {row['estrategia_j2']:15s} │"
              f" {duracion_min:5.1f} min │ {ganador_str}")
    
    # ========== CONCLUSIONES ==========
    print("\n" + "="*70)
    print("📊 CONCLUSIONES")
    print("="*70)
    
    mejor_estrategia = stats['tasa_victoria'].idxmax()
    mas_eficiente = eficiencia['danio_por_elixir'].idxmax()
    mas_agresiva = df_jugadores.groupby('estrategia')['danio_causado'].mean().idxmax()
    mas_defensiva = df_jugadores.groupby('estrategia')['danio_recibido'].mean().idxmin()
    
    print(f"""
  🏆 Estrategia con mayor tasa de victoria: {mejor_estrategia}
  ⚡ Estrategia más eficiente (daño/elixir): {mas_eficiente}
  ⚔️  Estrategia más agresiva (más daño): {mas_agresiva}
  🛡️  Estrategia más defensiva (menos daño recibido): {mas_defensiva}
  
  📈 El juego está {'BALANCEADO' if stats['tasa_victoria'].std() < 10 else 'DESBALANCEADO'}
     (Desviación estándar de tasas de victoria: {stats['tasa_victoria'].std():.1f}%)
  
  ⏱️  Duración promedio de partidas: {df_partidas['duracion_segundos'].mean()/60:.1f} minutos
  🎯 Total de ataques registrados: {(df_eventos['tipo_evento'] == 'ATAQUE_REALIZADO').sum()}
  🏰 Total de torres destruidas: {(df_eventos['tipo_evento'] == 'TORRE_DESTRUIDA').sum()}
    """)
    
    print("="*70)
    print("✅ Análisis completado")
    print("="*70 + "\n")
    
    # Guardar resumen en texto
    with open(carpeta / "resumen_analisis.txt", "w", encoding="utf-8") as f:
        f.write("="*70 + "\n")
        f.write("RESUMEN DE ANÁLISIS - CLASH ROYALE SIMULATION\n")
        f.write("="*70 + "\n\n")
        f.write(f"Total de partidas: {len(df_partidas)}\n")
        f.write(f"Mejor estrategia: {mejor_estrategia} ({stats.loc[mejor_estrategia, 'tasa_victoria']:.1f}%)\n")
        f.write(f"Estrategia más eficiente: {mas_eficiente}\n")
        f.write(f"Duración promedio: {df_partidas['duracion_segundos'].mean()/60:.1f} min\n")
    
    print("💾 Resumen guardado en: datos_analisis/resumen_analisis.txt\n")

if __name__ == "__main__":
    analisis_rapido()
