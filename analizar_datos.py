"""
Script de análisis de datos del juego Clash Royale Simulation
Analiza los archivos CSV generados y crea visualizaciones
"""

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from pathlib import Path

# Configuración de estilo
sns.set_style("whitegrid")
plt.rcParams['figure.figsize'] = (12, 6)

class AnalizadorClashRoyale:
    def __init__(self, carpeta_datos="datos_analisis"):
        self.carpeta = Path(carpeta_datos)
        self.df_partidas = None
        self.df_jugadores = None
        self.df_eventos = None
        
    def cargar_datos(self):
        """Carga todos los archivos CSV"""
        print("Cargando datos...")
        
        try:
            self.df_partidas = pd.read_csv(self.carpeta / "resumen_partidas.csv")
            self.df_jugadores = pd.read_csv(self.carpeta / "estadisticas_jugadores.csv")
            self.df_eventos = pd.read_csv(self.carpeta / "eventos_partidas.csv")
            
            print(f"✓ Partidas cargadas: {len(self.df_partidas)}")
            print(f"✓ Registros de jugadores: {len(self.df_jugadores)}")
            print(f"✓ Eventos registrados: {len(self.df_eventos)}")
            return True
        except FileNotFoundError as e:
            print(f"✗ Error: No se encontraron los archivos CSV en {self.carpeta}")
            print(f"  Ejecuta primero el programa Java para generar los datos")
            return False
    
    def analisis_estrategias(self):
        """Analiza el rendimiento de cada estrategia"""
        print("\n" + "="*60)
        print("ANÁLISIS DE ESTRATEGIAS")
        print("="*60)
        
        # Calcular estadísticas por estrategia
        stats_estrategia = self.df_jugadores.groupby('estrategia').agg({
            'resultado': lambda x: (x == 'VICTORIA').sum(),
            'cartas_jugadas': 'mean',
            'elixir_gastado': 'mean',
            'danio_causado': 'mean',
            'danio_recibido': 'mean',
            'torres_destruidas': 'mean',
            'ataques_realizados': 'mean'
        }).round(2)
        
        # Calcular tasa de victoria
        total_partidas = self.df_jugadores.groupby('estrategia').size()
        stats_estrategia['tasa_victoria'] = (stats_estrategia['resultado'] / total_partidas * 100).round(2)
        stats_estrategia['partidas'] = total_partidas
        
        # Renombrar columnas
        stats_estrategia.columns = ['Victorias', 'Cartas/Partida', 'Elixir/Partida', 
                                     'Daño Causado', 'Daño Recibido', 'Torres Destruidas',
                                     'Ataques/Partida', 'Tasa Victoria %', 'Partidas']
        
        print("\nEstadísticas por Estrategia:")
        print(stats_estrategia.to_string())
        
        return stats_estrategia
    
    def graficar_tasa_victoria(self, stats_estrategia):
        """Gráfico de tasa de victoria por estrategia"""
        plt.figure(figsize=(10, 6))
        
        estrategias = stats_estrategia.index
        tasas = stats_estrategia['Tasa Victoria %']
        colores = plt.cm.viridis(np.linspace(0, 1, len(estrategias)))
        
        bars = plt.bar(estrategias, tasas, color=colores, edgecolor='black', linewidth=1.5)
        
        # Añadir valores sobre las barras
        for bar in bars:
            height = bar.get_height()
            plt.text(bar.get_x() + bar.get_width()/2., height,
                    f'{height:.1f}%',
                    ha='center', va='bottom', fontsize=12, fontweight='bold')
        
        plt.title('Tasa de Victoria por Estrategia', fontsize=16, fontweight='bold', pad=20)
        plt.ylabel('Tasa de Victoria (%)', fontsize=12)
        plt.xlabel('Estrategia', fontsize=12)
        plt.ylim(0, max(tasas) * 1.15)
        plt.xticks(rotation=45, ha='right')
        plt.tight_layout()
        plt.savefig(self.carpeta / 'tasa_victoria.png', dpi=300, bbox_inches='tight')
        plt.close()
        print("✓ Gráfico guardado: tasa_victoria.png")
    
    def graficar_comparacion_metricas(self, stats_estrategia):
        """Gráfico comparativo de múltiples métricas"""
        fig, axes = plt.subplots(2, 3, figsize=(18, 10))
        fig.suptitle('Comparación de Métricas por Estrategia', fontsize=16, fontweight='bold')
        
        metricas = [
            ('Cartas/Partida', 'Cartas Jugadas Promedio'),
            ('Elixir/Partida', 'Elixir Gastado Promedio'),
            ('Daño Causado', 'Daño Causado Promedio'),
            ('Daño Recibido', 'Daño Recibido Promedio'),
            ('Torres Destruidas', 'Torres Destruidas Promedio'),
            ('Ataques/Partida', 'Ataques Realizados Promedio')
        ]
        
        for idx, (columna, titulo) in enumerate(metricas):
            ax = axes[idx // 3, idx % 3]
            
            estrategias = stats_estrategia.index
            valores = stats_estrategia[columna]
            
            bars = ax.bar(estrategias, valores, color=plt.cm.Set3(np.linspace(0, 1, len(estrategias))))
            
            # Añadir valores
            for bar in bars:
                height = bar.get_height()
                ax.text(bar.get_x() + bar.get_width()/2., height,
                       f'{height:.1f}',
                       ha='center', va='bottom', fontsize=9)
            
            ax.set_title(titulo, fontsize=11, fontweight='bold')
            ax.set_xticklabels(estrategias, rotation=45, ha='right', fontsize=9)
            ax.grid(axis='y', alpha=0.3)
        
        plt.tight_layout()
        plt.savefig(self.carpeta / 'comparacion_metricas.png', dpi=300, bbox_inches='tight')
        plt.close()
        print("✓ Gráfico guardado: comparacion_metricas.png")
    
    def analisis_enfrentamientos(self):
        """Matriz de enfrentamientos entre estrategias"""
        print("\n" + "="*60)
        print("ANÁLISIS DE ENFRENTAMIENTOS")
        print("="*60)
        
        # Crear matriz de enfrentamientos
        estrategias = sorted(self.df_partidas['estrategia_j1'].unique())
        matriz = pd.DataFrame(0, index=estrategias, columns=estrategias)
        
        for _, partida in self.df_partidas.iterrows():
            e1 = partida['estrategia_j1']
            e2 = partida['estrategia_j2']
            
            if partida['ganador'] == 1:
                matriz.loc[e1, e2] += 1
            elif partida['ganador'] == 2:
                matriz.loc[e2, e1] += 1
        
        # Calcular totales
        total_enfrentamientos = matriz + matriz.T
        tasa_victoria = (matriz / total_enfrentamientos * 100).fillna(0).round(1)
        
        print("\nMatriz de Victorias (Fila vs Columna):")
        print(matriz.to_string())
        
        return matriz, tasa_victoria
    
    def graficar_matriz_enfrentamientos(self, tasa_victoria):
        """Heatmap de tasas de victoria en enfrentamientos"""
        plt.figure(figsize=(10, 8))
        
        sns.heatmap(tasa_victoria, annot=True, fmt='.1f', cmap='RdYlGn', 
                    center=50, vmin=0, vmax=100,
                    cbar_kws={'label': 'Tasa de Victoria (%)'})
        
        plt.title('Matriz de Enfrentamientos - Tasa de Victoria (%)\n(Fila vs Columna)', 
                 fontsize=14, fontweight='bold', pad=20)
        plt.ylabel('Estrategia Atacante', fontsize=12)
        plt.xlabel('Estrategia Defensora', fontsize=12)
        plt.tight_layout()
        plt.savefig(self.carpeta / 'matriz_enfrentamientos.png', dpi=300, bbox_inches='tight')
        plt.close()
        print("✓ Gráfico guardado: matriz_enfrentamientos.png")
    
    def analisis_temporal(self):
        """Análisis de la evolución temporal de las partidas"""
        print("\n" + "="*60)
        print("ANÁLISIS TEMPORAL")
        print("="*60)
        
        # Duración de partidas
        duracion_promedio = self.df_partidas['duracion_segundos'].mean()
        duracion_std = self.df_partidas['duracion_segundos'].std()
        
        print(f"\nDuración de partidas:")
        print(f"  Promedio: {duracion_promedio:.1f} segundos ({duracion_promedio/60:.1f} minutos)")
        print(f"  Desviación estándar: {duracion_std:.1f} segundos")
        print(f"  Mínima: {self.df_partidas['duracion_segundos'].min()} segundos")
        print(f"  Máxima: {self.df_partidas['duracion_segundos'].max()} segundos")
        
        # Análisis de eventos por segundo
        eventos_por_segundo = self.df_eventos.groupby('segundo').size()
        
        return eventos_por_segundo
    
    def graficar_distribucion_duracion(self):
        """Histograma de duración de partidas"""
        plt.figure(figsize=(12, 6))
        
        duraciones = self.df_partidas['duracion_segundos']
        
        plt.hist(duraciones, bins=30, color='skyblue', edgecolor='black', alpha=0.7)
        plt.axvline(duraciones.mean(), color='red', linestyle='--', linewidth=2, 
                   label=f'Media: {duraciones.mean():.1f}s')
        plt.axvline(duraciones.median(), color='green', linestyle='--', linewidth=2,
                   label=f'Mediana: {duraciones.median():.1f}s')
        
        plt.title('Distribución de Duración de Partidas', fontsize=16, fontweight='bold')
        plt.xlabel('Duración (segundos)', fontsize=12)
        plt.ylabel('Frecuencia', fontsize=12)
        plt.legend(fontsize=11)
        plt.grid(axis='y', alpha=0.3)
        plt.tight_layout()
        plt.savefig(self.carpeta / 'distribucion_duracion.png', dpi=300, bbox_inches='tight')
        plt.close()
        print("✓ Gráfico guardado: distribucion_duracion.png")
    
    def graficar_actividad_temporal(self, eventos_por_segundo):
        """Gráfico de actividad promedio por segundo"""
        plt.figure(figsize=(14, 6))
        
        plt.plot(eventos_por_segundo.index, eventos_por_segundo.values, 
                linewidth=2, color='darkblue', alpha=0.7)
        plt.fill_between(eventos_por_segundo.index, eventos_por_segundo.values, 
                         alpha=0.3, color='lightblue')
        
        plt.title('Actividad del Juego a lo Largo del Tiempo', fontsize=16, fontweight='bold')
        plt.xlabel('Tiempo (segundos)', fontsize=12)
        plt.ylabel('Número de Eventos', fontsize=12)
        plt.grid(True, alpha=0.3)
        plt.tight_layout()
        plt.savefig(self.carpeta / 'actividad_temporal.png', dpi=300, bbox_inches='tight')
        plt.close()
        print("✓ Gráfico guardado: actividad_temporal.png")
    
    def analisis_eficiencia(self):
        """Análisis de eficiencia de elixir y daño"""
        print("\n" + "="*60)
        print("ANÁLISIS DE EFICIENCIA")
        print("="*60)
        
        # Calcular métricas de eficiencia
        self.df_jugadores['danio_por_elixir'] = (
            self.df_jugadores['danio_causado'] / self.df_jugadores['elixir_gastado']
        ).round(2)
        
        self.df_jugadores['ratio_danio'] = (
            self.df_jugadores['danio_causado'] / self.df_jugadores['danio_recibido']
        ).round(2)
        
        # Eficiencia por estrategia
        eficiencia = self.df_jugadores.groupby('estrategia').agg({
            'danio_por_elixir': 'mean',
            'ratio_danio': 'mean',
            'promedio_elixir_carta': 'mean'
        }).round(2)
        
        eficiencia.columns = ['Daño/Elixir', 'Ratio Daño (C/R)', 'Elixir/Carta']
        
        print("\nEficiencia por Estrategia:")
        print(eficiencia.to_string())
        
        return eficiencia
    
    def graficar_eficiencia(self, eficiencia):
        """Gráfico de barras de eficiencia"""
        fig, axes = plt.subplots(1, 3, figsize=(16, 5))
        fig.suptitle('Análisis de Eficiencia por Estrategia', fontsize=16, fontweight='bold')
        
        metricas = [
            ('Daño/Elixir', 'Daño por Elixir Gastado'),
            ('Ratio Daño (C/R)', 'Ratio Daño Causado/Recibido'),
            ('Elixir/Carta', 'Costo Promedio de Cartas')
        ]
        
        for idx, (columna, titulo) in enumerate(metricas):
            ax = axes[idx]
            
            estrategias = eficiencia.index
            valores = eficiencia[columna]
            
            bars = ax.bar(estrategias, valores, 
                         color=plt.cm.coolwarm(np.linspace(0.2, 0.8, len(estrategias))))
            
            # Añadir valores
            for bar in bars:
                height = bar.get_height()
                ax.text(bar.get_x() + bar.get_width()/2., height,
                       f'{height:.2f}',
                       ha='center', va='bottom', fontsize=10, fontweight='bold')
            
            ax.set_title(titulo, fontsize=12, fontweight='bold')
            ax.set_xticklabels(estrategias, rotation=45, ha='right')
            ax.grid(axis='y', alpha=0.3)
        
        plt.tight_layout()
        plt.savefig(self.carpeta / 'analisis_eficiencia.png', dpi=300, bbox_inches='tight')
        plt.close()
        print("✓ Gráfico guardado: analisis_eficiencia.png")
    
    def analisis_eventos(self):
        """Análisis de tipos de eventos"""
        print("\n" + "="*60)
        print("ANÁLISIS DE EVENTOS")
        print("="*60)
        
        # Contar eventos por tipo
        eventos_tipo = self.df_eventos['tipo_evento'].value_counts()
        
        print("\nDistribución de Eventos:")
        for tipo, cantidad in eventos_tipo.items():
            porcentaje = (cantidad / len(self.df_eventos) * 100)
            print(f"  {tipo}: {cantidad} ({porcentaje:.1f}%)")
        
        return eventos_tipo
    
    def graficar_eventos(self, eventos_tipo):
        """Gráfico de torta de tipos de eventos"""
        plt.figure(figsize=(10, 8))
        
        colores = plt.cm.Set3(np.linspace(0, 1, len(eventos_tipo)))
        explode = [0.05 if i == 0 else 0 for i in range(len(eventos_tipo))]
        
        wedges, texts, autotexts = plt.pie(eventos_tipo.values, 
                                            labels=eventos_tipo.index,
                                            autopct='%1.1f%%',
                                            colors=colores,
                                            explode=explode,
                                            shadow=True,
                                            startangle=90)
        
        # Mejorar texto
        for autotext in autotexts:
            autotext.set_color('white')
            autotext.set_fontsize(11)
            autotext.set_fontweight('bold')
        
        for text in texts:
            text.set_fontsize(12)
        
        plt.title('Distribución de Tipos de Eventos', fontsize=16, fontweight='bold', pad=20)
        plt.tight_layout()
        plt.savefig(self.carpeta / 'distribucion_eventos.png', dpi=300, bbox_inches='tight')
        plt.close()
        print("✓ Gráfico guardado: distribucion_eventos.png")
    
    def generar_reporte_completo(self):
        """Genera un reporte completo con todos los análisis"""
        print("\n" + "="*60)
        print("GENERANDO REPORTE COMPLETO")
        print("="*60 + "\n")
        
        if not self.cargar_datos():
            return
        
        # Realizar análisis
        stats_estrategia = self.analisis_estrategias()
        self.graficar_tasa_victoria(stats_estrategia)
        self.graficar_comparacion_metricas(stats_estrategia)
        
        matriz, tasa_victoria = self.analisis_enfrentamientos()
        self.graficar_matriz_enfrentamientos(tasa_victoria)
        
        eventos_por_segundo = self.analisis_temporal()
        self.graficar_distribucion_duracion()
        self.graficar_actividad_temporal(eventos_por_segundo)
        
        eficiencia = self.analisis_eficiencia()
        self.graficar_eficiencia(eficiencia)
        
        eventos_tipo = self.analisis_eventos()
        self.graficar_eventos(eventos_tipo)
        
        print("\n" + "="*60)
        print("REPORTE COMPLETADO")
        print("="*60)
        print(f"\nTodos los gráficos han sido guardados en: {self.carpeta}")
        print("\nArchivos generados:")
        print("  1. tasa_victoria.png")
        print("  2. comparacion_metricas.png")
        print("  3. matriz_enfrentamientos.png")
        print("  4. distribucion_duracion.png")
        print("  5. actividad_temporal.png")
        print("  6. analisis_eficiencia.png")
        print("  7. distribucion_eventos.png")

if __name__ == "__main__":
    analizador = AnalizadorClashRoyale()
    analizador.generar_reporte_completo()
