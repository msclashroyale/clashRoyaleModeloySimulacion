"""
Análisis Estadístico Avanzado
Incluye pruebas de hipótesis, intervalos de confianza y análisis de significancia
"""

import pandas as pd
import numpy as np
from scipy import stats
from pathlib import Path

class AnalisisEstadisticoAvanzado:
    def __init__(self, carpeta_datos="datos_analisis"):
        self.carpeta = Path(carpeta_datos)
        
    def cargar_datos(self):
        """Carga los datos"""
        self.df_partidas = pd.read_csv(self.carpeta / "resumen_partidas.csv", encoding='utf-8')
        self.df_jugadores = pd.read_csv(self.carpeta / "estadisticas_jugadores.csv", encoding='utf-8')
        print(f"✓ Datos cargados: {len(self.df_partidas)} partidas")
    
    def test_chi_cuadrado_estrategias(self):
        """Test chi-cuadrado para independencia entre estrategia y resultado"""
        print("\n" + "="*70)
        print("TEST CHI-CUADRADO: ¿Las estrategias afectan el resultado?")
        print("="*70)
        
        # Crear tabla de contingencia
        contingencia = pd.crosstab(
            self.df_jugadores['estrategia'], 
            self.df_jugadores['resultado']
        )
        
        print("\nTabla de Contingencia:")
        print(contingencia)
        
        # Realizar test
        chi2, p_valor, grados_libertad, frecuencias_esperadas = stats.chi2_contingency(contingencia)
        
        print(f"\nResultados del Test:")
        print(f"  Chi-cuadrado: {chi2:.4f}")
        print(f"  p-valor: {p_valor:.4f}")
        print(f"  Grados de libertad: {grados_libertad}")
        
        if p_valor < 0.05:
            print(f"\n  ✓ Conclusión: Las estrategias SÍ afectan significativamente el resultado")
            print(f"    (p < 0.05, rechazamos H0)")
        else:
            print(f"\n  ✗ Conclusión: No hay evidencia suficiente de que las estrategias")
            print(f"    afecten el resultado (p >= 0.05, no rechazamos H0)")
        
        return chi2, p_valor
    
    def intervalos_confianza_tasas_victoria(self):
        """Calcula intervalos de confianza para tasas de victoria"""
        print("\n" + "="*70)
        print("INTERVALOS DE CONFIANZA (95%) - TASAS DE VICTORIA")
        print("="*70)
        
        estrategias = self.df_jugadores['estrategia'].unique()
        
        print("\n" + "┌" + "─"*68 + "┐")
        print("│ Estrategia          │  Tasa  │   IC 95%   │ Margen Error │ n    │")
        print("├" + "─"*68 + "┤")
        
        resultados = {}
        
        for estrategia in estrategias:
            datos = self.df_jugadores[self.df_jugadores['estrategia'] == estrategia]
            n = len(datos)
            victorias = (datos['resultado'] == 'VICTORIA').sum()
            p = victorias / n
            
            # Intervalo de confianza Wilson
            z = 1.96  # Para 95% de confianza
            denominador = 1 + z**2/n
            centro = (p + z**2/(2*n)) / denominador
            margen = z * np.sqrt((p*(1-p)/n + z**2/(4*n**2))) / denominador
            
            ic_inf = centro - margen
            ic_sup = centro + margen
            
            resultados[estrategia] = {
                'tasa': p * 100,
                'ic_inf': ic_inf * 100,
                'ic_sup': ic_sup * 100,
                'margen': margen * 100,
                'n': n
            }
            
            print(f"│ {estrategia:19s} │ {p*100:5.1f}% │ [{ic_inf*100:4.1f}%, {ic_sup*100:4.1f}%] │"
                  f"    ±{margen*100:4.1f}%   │ {n:4d} │")
        
        print("└" + "─"*68 + "┘")
        
        return resultados
    
    def test_anova_danio_por_estrategia(self):
        """ANOVA para comparar daño causado entre estrategias"""
        print("\n" + "="*70)
        print("ANOVA: Comparación de daño causado entre estrategias")
        print("="*70)
        
        # Preparar grupos
        grupos = []
        estrategias = self.df_jugadores['estrategia'].unique()
        
        for estrategia in estrategias:
            datos = self.df_jugadores[
                self.df_jugadores['estrategia'] == estrategia
            ]['danio_causado'].values
            grupos.append(datos)
        
        # Realizar ANOVA
        f_stat, p_valor = stats.f_oneway(*grupos)
        
        print(f"\nEstadístico F: {f_stat:.4f}")
        print(f"p-valor: {p_valor:.4f}")
        
        if p_valor < 0.05:
            print(f"\n✓ Conclusión: Existe diferencia significativa en el daño causado")
            print(f"  entre las estrategias (p < 0.05)")
        else:
            print(f"\n✗ Conclusión: No hay diferencia significativa en el daño causado")
            print(f"  entre las estrategias (p >= 0.05)")
        
        # Estadísticas descriptivas por estrategia
        print("\nEstadísticas Descriptivas:")
        print("┌" + "─"*70 + "┐")
        print("│ Estrategia          │  Media  │  Mediana │  Desv.Est │  Min  │  Max   │")
        print("├" + "─"*70 + "┤")
        
        for estrategia in estrategias:
            datos = self.df_jugadores[
                self.df_jugadores['estrategia'] == estrategia
            ]['danio_causado']
            
            print(f"│ {estrategia:19s} │ {datos.mean():7.1f} │ {datos.median():8.1f} │"
                  f" {datos.std():9.1f} │ {datos.min():5.0f} │ {datos.max():6.0f} │")
        
        print("└" + "─"*70 + "┘")
        
        return f_stat, p_valor
    
    def test_t_pareado_enfrentamientos(self):
        """Test t pareado para enfrentamientos directos"""
        print("\n" + "="*70)
        print("TESTS T PARA ENFRENTAMIENTOS DIRECTOS")
        print("="*70)
        
        estrategias = sorted(self.df_partidas['estrategia_j1'].unique())
        
        print("\nComparaciones significativas (p < 0.05):\n")
        
        comparaciones_significativas = []
        
        for i, e1 in enumerate(estrategias):
            for e2 in estrategias[i+1:]:
                # Filtrar partidas donde se enfrentaron estas estrategias
                enfrentamientos = self.df_partidas[
                    ((self.df_partidas['estrategia_j1'] == e1) & 
                     (self.df_partidas['estrategia_j2'] == e2)) |
                    ((self.df_partidas['estrategia_j1'] == e2) & 
                     (self.df_partidas['estrategia_j2'] == e1))
                ]
                
                if len(enfrentamientos) < 10:  # Mínimo 10 partidas
                    continue
                
                # Calcular victorias de e1
                victorias_e1 = 0
                for _, partida in enfrentamientos.iterrows():
                    if (partida['estrategia_j1'] == e1 and partida['ganador'] == 1) or \
                       (partida['estrategia_j2'] == e1 and partida['ganador'] == 2):
                        victorias_e1 += 1
                
                tasa_e1 = victorias_e1 / len(enfrentamientos)
                
                # Test binomial
                from scipy.stats import binomtest
                resultado = binomtest(victorias_e1, len(enfrentamientos), 0.5, alternative='two-sided')
                p_valor = resultado.pvalue
                
                if p_valor < 0.05:
                    ventaja = "VENTAJA" if tasa_e1 > 0.5 else "DESVENTAJA"
                    comparaciones_significativas.append({
                        'e1': e1,
                        'e2': e2,
                        'tasa': tasa_e1,
                        'p_valor': p_valor,
                        'n': len(enfrentamientos),
                        'ventaja': ventaja
                    })
        
        if comparaciones_significativas:
            for comp in sorted(comparaciones_significativas, key=lambda x: x['p_valor']):
                print(f"  • {comp['e1']} vs {comp['e2']}")
                print(f"    Tasa victoria {comp['e1']}: {comp['tasa']*100:.1f}%")
                print(f"    p-valor: {comp['p_valor']:.4f} ({comp['ventaja']})")
                print(f"    Partidas: {comp['n']}\n")
        else:
            print("  No se encontraron diferencias significativas")
        
        return comparaciones_significativas
    
    def correlaciones(self):
        """Análisis de correlaciones entre variables"""
        print("\n" + "="*70)
        print("ANÁLISIS DE CORRELACIONES")
        print("="*70)
        
        # Variables de interés
        variables = [
            'cartas_jugadas', 'elixir_gastado', 'danio_causado', 
            'danio_recibido', 'torres_destruidas', 'ataques_realizados'
        ]
        
        # Matriz de correlación
        correlacion = self.df_jugadores[variables].corr()
        
        print("\nMatriz de Correlación de Pearson:")
        print("\n" + correlacion.round(3).to_string())
        
        # Identificar correlaciones fuertes
        print("\nCorrelaciones Fuertes (|r| > 0.7):")
        
        encontrado = False
        for i, var1 in enumerate(variables):
            for var2 in variables[i+1:]:
                r = correlacion.loc[var1, var2]
                if abs(r) > 0.7:
                    encontrado = True
                    tipo = "positiva" if r > 0 else "negativa"
                    print(f"  • {var1} ↔ {var2}: r = {r:.3f} ({tipo})")
        
        if not encontrado:
            print("  No se encontraron correlaciones fuertes")
        
        return correlacion
    
    def analisis_normalidad(self):
        """Test de normalidad para variables clave"""
        print("\n" + "="*70)
        print("TEST DE NORMALIDAD (Shapiro-Wilk)")
        print("="*70)
        
        variables = ['cartas_jugadas', 'elixir_gastado', 'danio_causado', 'duracion_segundos']
        
        print("\nVariable                 │ Estadístico │ p-valor │ ¿Normal?")
        print("─────────────────────────┼─────────────┼─────────┼──────────")
        
        for var in variables:
            if var in self.df_jugadores.columns:
                datos = self.df_jugadores[var].values
            else:
                datos = self.df_partidas[var].values
            
            stat, p_valor = stats.shapiro(datos[:5000])  # Máximo 5000 muestras
            es_normal = "Sí" if p_valor > 0.05 else "No"
            
            print(f"{var:24s} │ {stat:11.4f} │ {p_valor:7.4f} │ {es_normal:8s}")
    
    def generar_reporte_completo(self):
        """Genera reporte estadístico completo"""
        print("\n" + "="*70)
        print(" "*15 + "ANÁLISIS ESTADÍSTICO AVANZADO")
        print("="*70)
        
        self.cargar_datos()
        
        # Realizar todos los análisis
        self.test_chi_cuadrado_estrategias()
        self.intervalos_confianza_tasas_victoria()
        self.test_anova_danio_por_estrategia()
        self.test_t_pareado_enfrentamientos()
        self.correlaciones()
        self.analisis_normalidad()
        
        print("\n" + "="*70)
        print("NOTAS METODOLÓGICAS")
        print("="*70)
        print("""
• Nivel de significancia: α = 0.05 (95% de confianza)
• Test Chi-cuadrado: Evalúa independencia entre variables categóricas
• ANOVA: Compara medias de múltiples grupos
• Test t: Compara dos grupos
• Correlación de Pearson: Mide asociación lineal (-1 a 1)
• Test Shapiro-Wilk: Evalúa normalidad de datos

Interpretación de p-valores:
  p < 0.05  → Resultado estadísticamente significativo
  p >= 0.05 → No hay evidencia suficiente de diferencia
        """)
        
        print("="*70)
        print("✅ Análisis estadístico completado")
        print("="*70 + "\n")

if __name__ == "__main__":
    analizador = AnalisisEstadisticoAvanzado()
    analizador.generar_reporte_completo()
