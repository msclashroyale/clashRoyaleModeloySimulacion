"""
Machine Learning - Predicción de Ganadores
Entrena modelos para predecir el resultado de partidas
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path

class PredictorPartidas:
    def __init__(self, carpeta_datos="datos_analisis"):
        self.carpeta = Path(carpeta_datos)
        self.df_partidas = None
        self.df_jugadores = None
        self.modelo = None
        self.scaler = StandardScaler()
        self.le_estrategia = LabelEncoder()
        
    def cargar_datos(self):
        """Carga los datos de partidas"""
        print("Cargando datos...")
        self.df_partidas = pd.read_csv(self.carpeta / "resumen_partidas.csv")
        self.df_jugadores = pd.read_csv(self.carpeta / "estadisticas_jugadores.csv")
        print(f"✓ {len(self.df_partidas)} partidas cargadas")
        
    def preparar_features(self):
        """Prepara las features para el modelo"""
        print("\nPreparando features...")
        
        # Codificar estrategias
        estrategias_unicas = list(set(self.df_partidas['estrategia_j1'].unique()) | 
                                     set(self.df_partidas['estrategia_j2'].unique()))
        self.le_estrategia.fit(estrategias_unicas)
        
        # Crear dataset
        X = pd.DataFrame()
        
        # Features de estrategia (codificadas)
        X['estrategia_j1'] = self.le_estrategia.transform(self.df_partidas['estrategia_j1'])
        X['estrategia_j2'] = self.le_estrategia.transform(self.df_partidas['estrategia_j2'])
        
        # Features de nivel
        X['nivel_j1'] = self.df_partidas['nivel_j1']
        X['nivel_j2'] = self.df_partidas['nivel_j2']
        X['diferencia_nivel'] = self.df_partidas['nivel_j1'] - self.df_partidas['nivel_j2']
        
        # Features de cartas y elixir
        X['cartas_j1'] = self.df_partidas['cartas_jugadas_j1']
        X['cartas_j2'] = self.df_partidas['cartas_jugadas_j2']
        X['elixir_j1'] = self.df_partidas['elixir_gastado_j1']
        X['elixir_j2'] = self.df_partidas['elixir_gastado_j2']
        
        # Features de combate
        X['danio_j1'] = self.df_partidas['danio_causado_j1']
        X['danio_j2'] = self.df_partidas['danio_causado_j2']
        X['tropas_j1'] = self.df_partidas['tropas_invocadas_j1']
        X['tropas_j2'] = self.df_partidas['tropas_invocadas_j2']
        X['ataques_j1'] = self.df_partidas['ataques_j1']
        X['ataques_j2'] = self.df_partidas['ataques_j2']
        
        # Features derivadas (ratios)
        X['ratio_cartas'] = X['cartas_j1'] / (X['cartas_j2'] + 1)
        X['ratio_danio'] = X['danio_j1'] / (X['danio_j2'] + 1)
        X['eficiencia_j1'] = X['danio_j1'] / (X['elixir_j1'] + 1)
        X['eficiencia_j2'] = X['danio_j2'] / (X['elixir_j2'] + 1)
        
        # Target
        y = self.df_partidas['ganador']
        
        print(f"✓ Features preparadas: {X.shape[1]} columnas")
        print(f"✓ Distribución de clases:")
        print(y.value_counts())
        
        return X, y
    
    def entrenar_modelos(self, X, y):
        """Entrena y compara múltiples modelos"""
        print("\n" + "="*70)
        print("ENTRENANDO Y COMPARANDO MODELOS")
        print("="*70)
        
        # Split train/test
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42, stratify=y
        )
        
        # Escalar features
        X_train_scaled = self.scaler.fit_transform(X_train)
        X_test_scaled = self.scaler.transform(X_test)
        
        # Modelos a probar
        modelos = {
            'Random Forest': RandomForestClassifier(n_estimators=100, random_state=42),
            'Gradient Boosting': GradientBoostingClassifier(n_estimators=100, random_state=42),
            'Logistic Regression': LogisticRegression(max_iter=1000, random_state=42),
            'Decision Tree': DecisionTreeClassifier(random_state=42)
        }
        
        resultados = {}
        
        for nombre, modelo in modelos.items():
            print(f"\n{'─'*70}")
            print(f"Entrenando: {nombre}")
            print(f"{'─'*70}")
            
            # Entrenar
            if nombre == 'Logistic Regression':
                modelo.fit(X_train_scaled, y_train)
                y_pred = modelo.predict(X_test_scaled)
            else:
                modelo.fit(X_train, y_train)
                y_pred = modelo.predict(X_test)
            
            # Evaluar
            accuracy = accuracy_score(y_test, y_pred)
            
            # Cross-validation
            if nombre == 'Logistic Regression':
                cv_scores = cross_val_score(modelo, X_train_scaled, y_train, cv=5)
            else:
                cv_scores = cross_val_score(modelo, X_train, y_train, cv=5)
            
            resultados[nombre] = {
                'modelo': modelo,
                'accuracy': accuracy,
                'cv_mean': cv_scores.mean(),
                'cv_std': cv_scores.std(),
                'y_pred': y_pred
            }
            
            print(f"Accuracy en test: {accuracy:.4f}")
            print(f"Cross-validation: {cv_scores.mean():.4f} (+/- {cv_scores.std():.4f})")
        
        # Seleccionar mejor modelo
        mejor_nombre = max(resultados, key=lambda x: resultados[x]['accuracy'])
        self.modelo = resultados[mejor_nombre]['modelo']
        
        print(f"\n{'='*70}")
        print(f"MEJOR MODELO: {mejor_nombre}")
        print(f"Accuracy: {resultados[mejor_nombre]['accuracy']:.4f}")
        print(f"{'='*70}")
        
        return resultados, mejor_nombre, X_test, y_test
    
    def analizar_mejor_modelo(self, resultados, mejor_nombre, X_test, y_test):
        """Analiza en detalle el mejor modelo"""
        print(f"\n{'='*70}")
        print(f"ANÁLISIS DETALLADO: {mejor_nombre}")
        print(f"{'='*70}")
        
        y_pred = resultados[mejor_nombre]['y_pred']
        
        # Reporte de clasificación
        print("\nReporte de Clasificación:")
        
        # Determinar las clases presentes
        clases_unicas = sorted(y_test.unique())
        nombres_clases = []
        for clase in clases_unicas:
            if clase == 0:
                nombres_clases.append('Empate')
            elif clase == 1:
                nombres_clases.append('Jugador 1')
            else:
                nombres_clases.append('Jugador 2')
        
        print(classification_report(y_test, y_pred, 
                                   target_names=nombres_clases,
                                   labels=clases_unicas))
        
        # Matriz de confusión
        cm = confusion_matrix(y_test, y_pred)
        print("\nMatriz de Confusión:")
        print(cm)
        
        # Nombres para la visualización
        etiquetas_viz = [f'Clase {c}' if c not in [0,1,2] else 
                        ('Empate' if c == 0 else f'J{c}') 
                        for c in clases_unicas]
        
        # Visualizar matriz de confusión
        plt.figure(figsize=(8, 6))
        sns.heatmap(cm, annot=True, fmt='d', cmap='Blues',
                   xticklabels=etiquetas_viz,
                   yticklabels=etiquetas_viz)
        plt.title(f'Matriz de Confusión - {mejor_nombre}')
        plt.ylabel('Valor Real')
        plt.xlabel('Predicción')
        plt.tight_layout()
        plt.savefig(self.carpeta / 'matriz_confusion.png', dpi=300)
        plt.close()
        print("✓ Matriz de confusión guardada: matriz_confusion.png")
        
        # Feature importance (si aplica)
        if hasattr(self.modelo, 'feature_importances_'):
            self.visualizar_importancia_features(X_test)
    
    def visualizar_importancia_features(self, X):
        """Visualiza la importancia de las features"""
        importancias = self.modelo.feature_importances_
        indices = np.argsort(importancias)[::-1][:15]  # Top 15
        
        plt.figure(figsize=(12, 6))
        plt.bar(range(len(indices)), importancias[indices])
        plt.xticks(range(len(indices)), X.columns[indices], rotation=45, ha='right')
        plt.title('Importancia de Features (Top 15)')
        plt.xlabel('Feature')
        plt.ylabel('Importancia')
        plt.tight_layout()
        plt.savefig(self.carpeta / 'importancia_features.png', dpi=300)
        plt.close()
        print("✓ Importancia de features guardada: importancia_features.png")
        
        print("\nTop 10 Features más importantes:")
        for i in range(min(10, len(indices))):
            idx = indices[i]
            print(f"  {i+1}. {X.columns[idx]}: {importancias[idx]:.4f}")
    
    def comparar_modelos_visual(self, resultados):
        """Compara visualmente todos los modelos"""
        nombres = list(resultados.keys())
        accuracies = [resultados[n]['accuracy'] for n in nombres]
        cv_means = [resultados[n]['cv_mean'] for n in nombres]
        
        x = np.arange(len(nombres))
        width = 0.35
        
        fig, ax = plt.subplots(figsize=(12, 6))
        ax.bar(x - width/2, accuracies, width, label='Test Accuracy', alpha=0.8)
        ax.bar(x + width/2, cv_means, width, label='CV Mean', alpha=0.8)
        
        ax.set_xlabel('Modelo')
        ax.set_ylabel('Accuracy')
        ax.set_title('Comparación de Modelos')
        ax.set_xticks(x)
        ax.set_xticklabels(nombres, rotation=45, ha='right')
        ax.legend()
        ax.grid(axis='y', alpha=0.3)
        
        plt.tight_layout()
        plt.savefig(self.carpeta / 'comparacion_modelos.png', dpi=300)
        plt.close()
        print("✓ Comparación de modelos guardada: comparacion_modelos.png")
    
    def predecir_nueva_partida(self, estrategia_j1, estrategia_j2, nivel_j1=10, nivel_j2=10):
        """Predice el resultado de una nueva partida"""
        if self.modelo is None:
            print("Error: Debes entrenar el modelo primero")
            return
        
        # Crear features para nueva partida (valores promedio como baseline)
        nueva_partida = pd.DataFrame({
            'estrategia_j1': [self.le_estrategia.transform([estrategia_j1])[0]],
            'estrategia_j2': [self.le_estrategia.transform([estrategia_j2])[0]],
            'nivel_j1': [nivel_j1],
            'nivel_j2': [nivel_j2],
            'diferencia_nivel': [nivel_j1 - nivel_j2],
            'cartas_j1': [15],  # Valores promedio
            'cartas_j2': [15],
            'elixir_j1': [50],
            'elixir_j2': [50],
            'danio_j1': [1500],
            'danio_j2': [1500],
            'tropas_j1': [15],
            'tropas_j2': [15],
            'ataques_j1': [50],
            'ataques_j2': [50],
            'ratio_cartas': [1.0],
            'ratio_danio': [1.0],
            'eficiencia_j1': [30],
            'eficiencia_j2': [30]
        })
        
        prediccion = self.modelo.predict(nueva_partida)[0]
        probabilidades = self.modelo.predict_proba(nueva_partida)[0] if hasattr(self.modelo, 'predict_proba') else None
        
        print(f"\nPredicción para: {estrategia_j1} vs {estrategia_j2}")
        print(f"Ganador predicho: {'Empate' if prediccion == 0 else f'Jugador {prediccion}'}")
        
        if probabilidades is not None:
            print(f"Probabilidades:")
            print(f"  Empate: {probabilidades[0]:.2%}")
            print(f"  Jugador 1: {probabilidades[1]:.2%}")
            print(f"  Jugador 2: {probabilidades[2]:.2%}")
    
    def entrenar_completo(self):
        """Ejecuta el pipeline completo"""
        print("\n" + "="*70)
        print(" "*15 + "MACHINE LEARNING - CLASH ROYALE")
        print("="*70)
        
        # Cargar datos
        self.cargar_datos()
        
        # Preparar features
        X, y = self.preparar_features()
        
        # Entrenar modelos
        resultados, mejor_nombre, X_test, y_test = self.entrenar_modelos(X, y)
        
        # Analizar mejor modelo
        self.analizar_mejor_modelo(resultados, mejor_nombre, X_test, y_test)
        
        # Comparar modelos visualmente
        self.comparar_modelos_visual(resultados)
        
        print("\n" + "="*70)
        print("ENTRENAMIENTO COMPLETADO")
        print("="*70)
        print("\nArchivos generados:")
        print("  • matriz_confusion.png")
        print("  • importancia_features.png")
        print("  • comparacion_modelos.png")
        print("\n" + "="*70)


if __name__ == "__main__":
    predictor = PredictorPartidas()
    predictor.entrenar_completo()
    
    # Ejemplo de predicción
    print("\n" + "="*70)
    print("EJEMPLOS DE PREDICCIÓN")
    print("="*70)
    
    # Obtener estrategias disponibles
    estrategias = predictor.le_estrategia.classes_
    print(f"\nEstrategias disponibles: {list(estrategias)}")
    
    if len(estrategias) >= 2:
        # Hacer algunas predicciones de ejemplo
        predictor.predecir_nueva_partida(estrategias[0], estrategias[1])
        if len(estrategias) >= 3:
            predictor.predecir_nueva_partida(estrategias[1], estrategias[2])
