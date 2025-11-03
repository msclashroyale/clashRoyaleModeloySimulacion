"""
Predictor de Enfrentamientos - Machine Learning
Predice el ganador ANTES de jugar, solo con estrategias y niveles
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import LabelEncoder
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.metrics import classification_report, accuracy_score, confusion_matrix
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path

class PredictorEnfrentamientos:
    def __init__(self, carpeta_datos="datos_analisis"):
        self.carpeta = Path(carpeta_datos)
        self.df_partidas = None
        self.modelo = None
        self.le_estrategia = LabelEncoder()
        
    def cargar_datos(self):
        """Carga los datos de partidas"""
        print("Cargando datos...")
        self.df_partidas = pd.read_csv(self.carpeta / "resumen_partidas.csv", encoding='utf-8')
        print(f"✓ {len(self.df_partidas)} partidas cargadas\n")
        
    def preparar_features_minimas(self):
        """Prepara features que se conocen ANTES de jugar"""
        print("Preparando features pre-partida...")
        
        # Codificar estrategias
        estrategias_unicas = list(set(self.df_partidas['estrategia_j1'].unique()) | 
                                     set(self.df_partidas['estrategia_j2'].unique()))
        self.le_estrategia.fit(estrategias_unicas)
        
        # Features disponibles ANTES de jugar
        X = pd.DataFrame()
        X['estrategia_j1'] = self.le_estrategia.transform(self.df_partidas['estrategia_j1'])
        X['estrategia_j2'] = self.le_estrategia.transform(self.df_partidas['estrategia_j2'])
        X['nivel_j1'] = self.df_partidas['nivel_j1']
        X['nivel_j2'] = self.df_partidas['nivel_j2']
        X['diferencia_nivel'] = self.df_partidas['nivel_j1'] - self.df_partidas['nivel_j2']
        
        # Target
        y = self.df_partidas['ganador']
        
        print(f"✓ Features preparadas: {X.shape[1]} columnas")
        print(f"  - estrategia_j1, estrategia_j2")
        print(f"  - nivel_j1, nivel_j2")
        print(f"  - diferencia_nivel")
        print(f"\n✓ Distribución de ganadores:")
        print(y.value_counts().to_string())
        print()
        
        return X, y
    
    def entrenar_modelo(self, X, y):
        """Entrena el modelo predictivo"""
        print("="*70)
        print("ENTRENANDO PREDICTOR DE ENFRENTAMIENTOS")
        print("="*70)
        
        # Split
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42, stratify=y
        )
        
        # Probar Gradient Boosting
        print("\nEntrenando Gradient Boosting...")
        modelo = GradientBoostingClassifier(n_estimators=100, random_state=42)
        modelo.fit(X_train, y_train)
        
        # Evaluar
        y_pred = modelo.predict(X_test)
        accuracy = accuracy_score(y_test, y_pred)
        cv_scores = cross_val_score(modelo, X_train, y_train, cv=5)
        
        print(f"\n✓ Accuracy en test: {accuracy:.2%}")
        print(f"✓ Cross-validation: {cv_scores.mean():.2%} (+/- {cv_scores.std():.2%})")
        
        # Reporte detallado
        print("\n" + "-"*70)
        print("REPORTE DE CLASIFICACIÓN")
        print("-"*70)
        
        clases_unicas = sorted(y_test.unique())
        nombres_clases = [f'J{c}' if c != 0 else 'Empate' for c in clases_unicas]
        
        print(classification_report(y_test, y_pred, 
                                   target_names=nombres_clases,
                                   labels=clases_unicas))
        
        # Guardar modelo
        self.modelo = modelo
        
        # Matriz de confusión
        cm = confusion_matrix(y_test, y_pred)
        self._visualizar_confusion(cm, nombres_clases)
        
        return modelo, accuracy, cv_scores
    
    def _visualizar_confusion(self, cm, nombres):
        """Visualiza matriz de confusión"""
        plt.figure(figsize=(8, 6))
        sns.heatmap(cm, annot=True, fmt='d', cmap='Blues',
                   xticklabels=nombres, yticklabels=nombres)
        plt.title('Matriz de Confusión - Predictor de Enfrentamientos')
        plt.ylabel('Valor Real')
        plt.xlabel('Predicción')
        plt.tight_layout()
        plt.savefig(self.carpeta / 'predictor_enfrentamientos_confusion.png', dpi=300)
        plt.close()
        print("\n✓ Matriz de confusión guardada: predictor_enfrentamientos_confusion.png")
    
    def predecir_enfrentamiento(self, estrategia_j1, estrategia_j2, nivel_j1=10, nivel_j2=10, verbose=True):
        """
        Predice el ganador de un enfrentamiento específico
        
        Returns:
            dict con predicción, probabilidades y confianza
        """
        if self.modelo is None:
            raise ValueError("Debes entrenar el modelo primero")
        
        # Preparar input
        input_data = pd.DataFrame({
            'estrategia_j1': [self.le_estrategia.transform([estrategia_j1])[0]],
            'estrategia_j2': [self.le_estrategia.transform([estrategia_j2])[0]],
            'nivel_j1': [nivel_j1],
            'nivel_j2': [nivel_j2],
            'diferencia_nivel': [nivel_j1 - nivel_j2]
        })
        
        # Predecir
        prediccion = self.modelo.predict(input_data)[0]
        probabilidades = self.modelo.predict_proba(input_data)[0]
        
        # Mapear clases a índices de probabilidades
        clases = self.modelo.classes_
        prob_dict = {int(clase): prob for clase, prob in zip(clases, probabilidades)}
        
        resultado = {
            'prediccion': int(prediccion),
            'probabilidades': prob_dict,
            'confianza': max(probabilidades),
            'estrategia_j1': estrategia_j1,
            'estrategia_j2': estrategia_j2,
            'nivel_j1': nivel_j1,
            'nivel_j2': nivel_j2
        }
        
        if verbose:
            self._mostrar_prediccion(resultado)
        
        return resultado
    
    def _mostrar_prediccion(self, resultado):
        """Muestra la predicción de forma legible"""
        print("\n" + "="*70)
        print("PREDICCIÓN DE ENFRENTAMIENTO")
        print("="*70)
        print(f"\n🥊 {resultado['estrategia_j1']} (Nv.{resultado['nivel_j1']}) vs "
              f"{resultado['estrategia_j2']} (Nv.{resultado['nivel_j2']})")
        print("-"*70)
        
        pred = resultado['prediccion']
        if pred == 0:
            print(f"⚖️  Predicción: EMPATE")
        elif pred == 1:
            print(f"🏆 Predicción: GANA {resultado['estrategia_j1']} (Jugador 1)")
        else:
            print(f"🏆 Predicción: GANA {resultado['estrategia_j2']} (Jugador 2)")
        
        print(f"\n📊 Confianza: {resultado['confianza']:.1%}")
        print(f"\n📈 Probabilidades:")
        
        for clase, prob in sorted(resultado['probabilidades'].items()):
            nombre = "Empate" if clase == 0 else f"Jugador {clase}"
            barra = "█" * int(prob * 40)
            print(f"  {nombre:12s} │ {barra:40s} │ {prob:6.1%}")
        
        print("="*70)
    
    def rankear_estrategias(self):
        """Rankea las estrategias por winrate"""
        print("\n" + "="*70)
        print("RANKING DE ESTRATEGIAS")
        print("="*70)
        
        # Calcular winrate por estrategia
        stats = []
        estrategias = self.le_estrategia.classes_
        
        for estrategia in estrategias:
            # Partidas como J1
            como_j1 = self.df_partidas[self.df_partidas['estrategia_j1'] == estrategia]
            victorias_j1 = (como_j1['ganador'] == 1).sum()
            
            # Partidas como J2
            como_j2 = self.df_partidas[self.df_partidas['estrategia_j2'] == estrategia]
            victorias_j2 = (como_j2['ganador'] == 2).sum()
            
            total_partidas = len(como_j1) + len(como_j2)
            total_victorias = victorias_j1 + victorias_j2
            
            if total_partidas > 0:
                winrate = total_victorias / total_partidas
                stats.append({
                    'estrategia': estrategia,
                    'partidas': total_partidas,
                    'victorias': total_victorias,
                    'winrate': winrate
                })
        
        # Ordenar por winrate
        stats_df = pd.DataFrame(stats).sort_values('winrate', ascending=False)
        
        print("\n┌" + "─"*68 + "┐")
        print("│ Rank │ Estrategia              │ Partidas │ Victorias │ Winrate  │")
        print("├" + "─"*68 + "┤")
        
        for idx, row in stats_df.iterrows():
            rank = stats_df.index.get_loc(idx) + 1
            emoji = "🥇" if rank == 1 else "🥈" if rank == 2 else "🥉" if rank == 3 else "  "
            print(f"│ {emoji} {rank:2d} │ {row['estrategia']:23s} │ {row['partidas']:8d} │"
                  f" {row['victorias']:9d} │ {row['winrate']:7.1%} │")
        
        print("└" + "─"*68 + "┘")
        
        mejor = stats_df.iloc[0]
        print(f"\n🏆 Mejor estrategia: {mejor['estrategia']} ({mejor['winrate']:.1%} winrate)")
        
        return stats_df
    
    def matriz_enfrentamientos(self):
        """Muestra matriz de winrates entre estrategias"""
        print("\n" + "="*70)
        print("MATRIZ DE ENFRENTAMIENTOS")
        print("="*70)
        
        estrategias = sorted(self.le_estrategia.classes_)
        n = len(estrategias)
        
        # Crear matriz de winrates
        matriz = np.zeros((n, n))
        
        for i, est1 in enumerate(estrategias):
            for j, est2 in enumerate(estrategias):
                if i == j:
                    matriz[i, j] = 0.5  # Mismo vs mismo
                    continue
                
                # Enfrentamientos
                enf = self.df_partidas[
                    ((self.df_partidas['estrategia_j1'] == est1) & 
                     (self.df_partidas['estrategia_j2'] == est2)) |
                    ((self.df_partidas['estrategia_j1'] == est2) & 
                     (self.df_partidas['estrategia_j2'] == est1))
                ]
                
                if len(enf) == 0:
                    matriz[i, j] = 0.5  # No hay datos
                    continue
                
                # Contar victorias de est1
                victorias = 0
                for _, partida in enf.iterrows():
                    if (partida['estrategia_j1'] == est1 and partida['ganador'] == 1) or \
                       (partida['estrategia_j2'] == est1 and partida['ganador'] == 2):
                        victorias += 1
                
                matriz[i, j] = victorias / len(enf)
        
        # Visualizar
        plt.figure(figsize=(10, 8))
        sns.heatmap(matriz * 100, annot=True, fmt='.1f', cmap='RdYlGn',
                   center=50, vmin=0, vmax=100,
                   xticklabels=estrategias, yticklabels=estrategias,
                   cbar_kws={'label': 'Winrate (%)'})
        plt.title('Matriz de Winrates (Fila vs Columna)', fontsize=14, fontweight='bold')
        plt.ylabel('Estrategia', fontsize=12)
        plt.xlabel('vs Estrategia', fontsize=12)
        plt.tight_layout()
        plt.savefig(self.carpeta / 'matriz_winrates.png', dpi=300)
        plt.close()
        
        print("\n✓ Matriz guardada: matriz_winrates.png")
        
        # Mostrar en texto
        print("\nWinrate (%) - Fila vs Columna:")
        print("┌" + "─"*68 + "┐")
        print("│ Estrategia          ", end="")
        for est in estrategias:
            print(f"│ {est[:7]:7s} ", end="")
        print("│")
        print("├" + "─"*68 + "┤")
        
        for i, est1 in enumerate(estrategias):
            print(f"│ {est1:19s} ", end="")
            for j in range(n):
                val = matriz[i, j] * 100
                print(f"│ {val:6.1f}% ", end="")
            print("│")
        
        print("└" + "─"*68 + "┘")
    
    def modo_interactivo(self):
        """Modo interactivo para probar predicciones"""
        print("\n" + "="*70)
        print("MODO INTERACTIVO - PREDICTOR DE ENFRENTAMIENTOS")
        print("="*70)
        
        estrategias = list(self.le_estrategia.classes_)
        print(f"\nEstrategias disponibles:")
        for i, est in enumerate(estrategias, 1):
            print(f"  {i}. {est}")
        
        print(f"\n(Escribe 'salir' para terminar)")
        
        while True:
            print("\n" + "-"*70)
            
            # Selección J1
            try:
                resp = input(f"\nJugador 1 - Estrategia (1-{len(estrategias)}): ").strip()
                if resp.lower() == 'salir':
                    break
                idx1 = int(resp) - 1
                est1 = estrategias[idx1]
                
                nivel1 = int(input("Jugador 1 - Nivel (ej: 10): ").strip())
            except (ValueError, IndexError):
                print("❌ Entrada inválida")
                continue
            
            # Selección J2
            try:
                resp = input(f"\nJugador 2 - Estrategia (1-{len(estrategias)}): ").strip()
                if resp.lower() == 'salir':
                    break
                idx2 = int(resp) - 1
                est2 = estrategias[idx2]
                
                nivel2 = int(input("Jugador 2 - Nivel (ej: 10): ").strip())
            except (ValueError, IndexError):
                print("❌ Entrada inválida")
                continue
            
            # Predecir
            self.predecir_enfrentamiento(est1, est2, nivel1, nivel2)
        
        print("\n¡Hasta luego!")
    
    def ejecutar_completo(self):
        """Pipeline completo"""
        print("\n" + "="*70)
        print(" "*15 + "PREDICTOR DE ENFRENTAMIENTOS")
        print("="*70)
        
        # Cargar y entrenar
        self.cargar_datos()
        X, y = self.preparar_features_minimas()
        self.entrenar_modelo(X, y)
        
        # Análisis
        self.rankear_estrategias()
        self.matriz_enfrentamientos()
        
        # Ejemplos de predicción
        print("\n" + "="*70)
        print("EJEMPLOS DE PREDICCIÓN")
        print("="*70)
        
        estrategias = list(self.le_estrategia.classes_)
        if len(estrategias) >= 2:
            print("\nEjemplo 1: Enfrentamiento equilibrado")
            self.predecir_enfrentamiento(estrategias[0], estrategias[1], 10, 10)
            
            if len(estrategias) >= 3:
                print("\nEjemplo 2: Con ventaja de nivel")
                self.predecir_enfrentamiento(estrategias[1], estrategias[2], 12, 10)
        
        print("\n" + "="*70)
        print("ANÁLISIS COMPLETADO")
        print("="*70)
        print("\nArchivos generados:")
        print("  • predictor_enfrentamientos_confusion.png")
        print("  • matriz_winrates.png")


if __name__ == "__main__":
    predictor = PredictorEnfrentamientos()
    predictor.ejecutar_completo()
    
    # Modo interactivo (opcional)
    print("\n¿Quieres probar el modo interactivo? (s/n): ", end="")
    resp = input().strip().lower()
    if resp == 's':
        predictor.modo_interactivo()
