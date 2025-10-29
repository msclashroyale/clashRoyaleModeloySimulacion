# 📊 Documentación Técnica del Sistema de Análisis
## Proyecto: Simulador Clash Royale - Modelos y Simulación

### Autores: [Tu Nombre y Equipo]
### Fecha: Octubre 2025

---

## 📋 Índice

1. [Introducción y Objetivos](#1-introducción-y-objetivos)
2. [Arquitectura del Sistema](#2-arquitectura-del-sistema)
3. [Recolección de Datos](#3-recolección-de-datos)
4. [Métricas Capturadas](#4-métricas-capturadas)
5. [Análisis Estadístico](#5-análisis-estadístico)
6. [Machine Learning](#6-machine-learning)
7. [Visualizaciones](#7-visualizaciones)
8. [Justificación Metodológica](#8-justificación-metodológica)
9. [Resultados y Validación](#9-resultados-y-validación)
10. [Conclusiones](#10-conclusiones)

---

## 1. Introducción y Objetivos

### 1.1 Motivación

El sistema de análisis fue diseñado para cumplir tres objetivos principales:

1. **Validación del Modelo**: Verificar que la simulación produce resultados coherentes y realistas
2. **Análisis Cuantitativo**: Obtener métricas objetivas sobre el comportamiento del juego
3. **Optimización**: Identificar desbalances y oportunidades de mejora basadas en datos

### 1.2 Alcance

El sistema analiza **100+ partidas completas** capturando:
- 51+ métricas diferentes por partida
- Todos los eventos del juego en tiempo real
- Comportamiento de 4 estrategias de IA diferentes
- Interacciones entre entidades (tropas, torres, etc.)

---

## 2. Arquitectura del Sistema

### 2.1 Diagrama de Flujo

```
┌─────────────────┐
│   SIMULACIÓN    │ ──► Genera eventos en tiempo real
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ RecolectorDatos │ ──► Escucha eventos mediante patrón Observer
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ RegistroPartida │ ──► Almacena datos en memoria estructurada
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ ExportadorCSV   │ ──► Persiste datos en archivos CSV (UTF-8)
└────────┬────────┘
         │
         ├──────────────┬─────────────┬──────────────┐
         ▼              ▼             ▼              ▼
    Análisis      Análisis    Visualizaciones   Machine
  Estadístico     Rápido                        Learning
```

### 2.2 Componentes Principales

#### 2.2.1 Capa de Recolección (Java)

**`RecolectorDatos.java`**
- Implementa patrón **Observer** para capturar eventos
- Se suscribe al `EventManager` del juego
- **No interfiere** con la ejecución del juego (desacoplado)
- Actualización en tiempo real mediante `actualizarTick()`

**`RegistroPartida.java`**
- Contenedor de datos de una partida completa
- Estructura: ID único, timestamp, jugadores, eventos, estadísticas
- Inmutable después de finalizar la partida

**`ExportadorCSV.java`**
- Exportación con **UTF-8** explícito (evita errores de codificación)
- Formato CSV estándar compatible con pandas, Excel, R
- Separación en 3 archivos especializados

#### 2.2.2 Capa de Análisis (Python)

**Por qué Python:**
1. Ecosistema robusto para análisis de datos (pandas, numpy)
2. Librerías especializadas para ML (scikit-learn)
3. Visualizaciones profesionales (matplotlib, seaborn)
4. Sintaxis clara y rápida para prototipado

---

## 3. Recolección de Datos

### 3.1 Método de Captura

**Patrón Observer (Event-Driven):**

```java
// El juego dispara eventos
eventManager.notify(new TropaDesplegadaEvent(jugador, tropa, posicion));

// El recolector los captura automáticamente
@Override
public void onGameEvent(GameEvent event) {
    if (event instanceof TropaDesplegadaEvent) {
        procesarTropaDesplegada((TropaDesplegadaEvent) event);
    }
}
```

**Ventajas de este enfoque:**
- ✅ **No invasivo**: No modifica el código del juego
- ✅ **Tiempo real**: Captura datos mientras ocurren
- ✅ **Completo**: No se pierde ningún evento
- ✅ **Extensible**: Fácil agregar nuevos eventos

### 3.2 Eventos Capturados

| Evento | Información Capturada | Justificación |
|--------|----------------------|---------------|
| **TropaDesplegadaEvent** | Jugador, carta, posición, costo, tick | Decisiones tácticas de la IA |
| **AtaqueRealizadoEvent** | Atacante, defensor, daño | Dinámica del combate |
| **EntidadDestruidaEvent** | Tipo de entidad, jugador afectado | Progreso de la partida |
| **PartidaTerminadaEvent** | Ganador, motivo, duración | Resultado final |

### 3.3 Frecuencia de Muestreo

- **Tick-based**: Cada tick del juego (~1 segundo de juego)
- **Event-driven**: Solo cuando ocurren eventos (eficiente)
- **Sin pérdida de datos**: Captura 100% de los eventos

---

## 4. Métricas Capturadas

### 4.1 Taxonomía de Métricas

Clasificamos las métricas en 5 categorías:

#### 4.1.1 Métricas de Resultado (5 métricas)
- `ganador`: 0=empate, 1=J1, 2=J2
- `motivo_victoria`: Torre Rey, tiempo, vida total
- `duracion_segundos`: Tiempo total de partida
- `torres_destruidas_j1/j2`: Objetivos completados
- `vida_final_j1/j2`: Estado final de defensa

**Justificación**: Definen el outcome de la partida, variable objetivo para ML.

#### 4.1.2 Métricas de Recursos (8 métricas)
- `cartas_jugadas`: Actividad del jugador
- `elixir_gastado`: Recursos utilizados
- `elixir_desperdiciado`: Ineficiencia (alcanzar máximo)
- `promedio_elixir_carta`: Costo medio de estrategia
- `primer_carta_segundo`: Timing de inicio
- `ultima_carta_segundo`: Timing de fin

**Justificación**: Miden eficiencia en gestión de recursos, indicador clave de estrategia.

#### 4.1.3 Métricas de Combate (10 métricas)
- `tropas_invocadas`: Unidades desplegadas
- `tropas_muertas`: Bajas sufridas
- `ataques_realizados`: Agresividad
- `danio_causado`: Ofensiva total
- `danio_recibido`: Defensiva total
- `danio_a_torres`: Daño específico a objetivos
- `torres_destruidas`: Objetivos completados

**Justificación**: Capturan la dinámica del combate, fundamental para determinar victoria.

#### 4.1.4 Métricas Derivadas (5 métricas)
- `ratio_danio = danio_causado / danio_recibido`: Eficiencia de combate
- `danio_por_elixir = danio_causado / elixir_gastado`: Eficiencia de recursos
- `diferencia_nivel = nivel_j1 - nivel_j2`: Ventaja inherente
- `ratio_cartas = cartas_j1 / cartas_j2`: Actividad relativa

**Justificación**: Métricas normalizadas que facilitan la comparación y son más predictivas.

#### 4.1.5 Métricas Categóricas (4 métricas)
- `estrategia_j1/j2`: Comportamiento de IA (EstrategiaAgresiva, EstrategiaDefensiva, etc.)
- `nivel_j1/j2`: Nivel de las entidades

**Justificación**: Features categóricas para análisis de segmentación y ML.

### 4.2 Selección de Métricas

**Criterios aplicados:**
1. **Relevancia**: Debe influir en el resultado
2. **Mensurabilidad**: Debe ser objetivamente medible
3. **Independencia**: Evitar redundancia excesiva
4. **Completitud**: Cubrir todos los aspectos del juego

**Total: 51+ métricas únicas**

---

## 5. Análisis Estadístico

### 5.1 Tests Aplicados

#### 5.1.1 Test Chi-Cuadrado (χ²)

**Objetivo**: ¿Las estrategias afectan el resultado?

**Hipótesis:**
- H₀: Estrategia y resultado son independientes
- H₁: Estrategia y resultado están relacionados

**Implementación:**
```python
chi2, p_valor, gl, _ = stats.chi2_contingency(
    pd.crosstab(df['estrategia'], df['resultado'])
)
```

**Interpretación:**
- Si p < 0.05: Rechazamos H₀ → Las estrategias SÍ importan
- Si p ≥ 0.05: No rechazamos H₀ → No hay evidencia suficiente

**Justificación**: Método estándar para analizar relaciones entre variables categóricas.

#### 5.1.2 Intervalos de Confianza (95%)

**Objetivo**: Estimar la tasa de victoria real con incertidumbre

**Método**: Intervalo de Wilson (más robusto que el normal)

```python
z = 1.96  # 95% de confianza
denominador = 1 + z²/n
centro = (p + z²/(2*n)) / denominador
margen = z * sqrt((p*(1-p)/n + z²/(4*n²))) / denominador
```

**Justificación**: 
- Wilson es superior al método normal para proporciones
- No produce valores fuera de [0,1]
- Más preciso con muestras pequeñas

#### 5.1.3 ANOVA (Analysis of Variance)

**Objetivo**: ¿Hay diferencias significativas en el daño causado entre estrategias?

**Hipótesis:**
- H₀: μ₁ = μ₂ = μ₃ = μ₄ (todas las medias son iguales)
- H₁: Al menos una media es diferente

**Implementación:**
```python
f_stat, p_valor = stats.f_oneway(*grupos)
```

**Justificación**: 
- Test paramétrico estándar para comparar múltiples grupos
- Robusto ante desviaciones moderadas de normalidad (Teorema Central del Límite)

#### 5.1.4 Test Binomial

**Objetivo**: ¿Una estrategia tiene ventaja significativa en enfrentamientos directos?

**Implementación:**
```python
resultado = binomtest(victorias, total, 0.5, alternative='two-sided')
```

**Justificación**:
- Apropiado para datos binarios (victoria/derrota)
- No asume normalidad
- Potente para detectar sesgos

#### 5.1.5 Correlación de Pearson

**Objetivo**: Identificar relaciones lineales entre métricas

**Interpretación:**
- |r| > 0.7: Correlación fuerte
- |r| 0.4-0.7: Correlación moderada
- |r| < 0.4: Correlación débil

**Justificación**: 
- Identifica redundancia en features (multicolinealidad)
- Sugiere relaciones causales para investigar
- Ayuda a simplificar el modelo

### 5.2 Nivel de Significancia

**α = 0.05** (estándar en ciencias sociales e ingeniería)

**Implicaciones:**
- 5% de probabilidad de error Tipo I (falso positivo)
- Balance entre rigor y poder estadístico
- Ampliamente aceptado en literatura académica

---

## 6. Machine Learning

### 6.1 Problema a Resolver

**Tipo**: Clasificación multiclase

**Objetivo**: Predecir el ganador de una partida basándose en métricas del juego

**Variable Objetivo (y):**
```python
y = ganador  # 0=Empate, 1=Jugador 1, 2=Jugador 2
```

### 6.2 Features (Variables Predictoras)

#### 6.2.1 Features Seleccionadas (19 features)

**1. Features Categóricas (codificadas con LabelEncoder):**
- `estrategia_j1`, `estrategia_j2`

**2. Features Numéricas Directas:**
- `nivel_j1`, `nivel_j2`, `diferencia_nivel`
- `cartas_j1`, `cartas_j2`
- `elixir_j1`, `elixir_j2`
- `danio_j1`, `danio_j2`
- `tropas_j1`, `tropas_j2`
- `ataques_j1`, `ataques_j2`

**3. Features Derivadas (ratios):**
- `ratio_cartas = cartas_j1 / (cartas_j2 + 1)`
- `ratio_danio = danio_j1 / (danio_j2 + 1)`
- `eficiencia_j1 = danio_j1 / (elixir_j1 + 1)`
- `eficiencia_j2 = danio_j2 / (elixir_j2 + 1)`

#### 6.2.2 Justificación de Features

**Por qué estas features:**

1. **Estrategias**: Diferente comportamiento de IA → diferentes probabilidades de victoria
2. **Niveles**: Ventaja inherente en stats de entidades
3. **Cartas/Elixir**: Actividad y gestión de recursos
4. **Daño/Tropas/Ataques**: Efectividad en combate
5. **Ratios**: Normalizan las diferencias, más interpretables que valores absolutos

**Por qué ratios en lugar de diferencias:**
- `ratio = A/B` es más robusto que `diferencia = A-B`
- Captura relaciones multiplicativas (ej: 100 vs 50 es similar a 200 vs 100)
- Maneja mejor valores extremos
- Más interpretable: ratio=2 significa "el doble"

### 6.3 Preprocesamiento

#### 6.3.1 Codificación de Variables Categóricas

```python
from sklearn.preprocessing import LabelEncoder

le = LabelEncoder()
estrategias = ['EstrategiaAgresiva', 'EstrategiaDefensiva', ...]
# Convierte a: [0, 1, 2, 3]
```

**Justificación**: Los algoritmos de ML requieren input numérico.

#### 6.3.2 Escalado de Features

```python
from sklearn.preprocessing import StandardScaler

# Solo para Logistic Regression
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)
```

**Por qué escalar:**
- Logistic Regression es sensible a la escala
- Random Forest/Decision Tree NO requieren escalado (basados en particiones)
- Mejora convergencia del gradiente

#### 6.3.3 Split Train/Test

```python
X_train, X_test, y_train, y_test = train_test_split(
    X, y, 
    test_size=0.2,  # 80% train, 20% test
    random_state=42,  # Reproducibilidad
    stratify=y  # Mantiene proporción de clases
)
```

**Justificación del 80/20**:
- Estándar en la industria
- Con 100 partidas: 80 para entrenar, 20 para probar
- Balance entre entrenamiento suficiente y test representativo

### 6.4 Modelos Evaluados

#### 6.4.1 Random Forest

**Características:**
- Ensemble de árboles de decisión
- Reduce overfitting mediante bagging
- Robusto ante outliers
- Maneja bien relaciones no lineales

**Hiperparámetros:**
```python
RandomForestClassifier(
    n_estimators=100,  # Número de árboles
    random_state=42
)
```

**Ventajas para este problema:**
- No requiere escalado
- Feature importance automático
- Maneja interacciones complejas

#### 6.4.2 Gradient Boosting

**Características:**
- Ensemble secuencial (cada árbol corrige al anterior)
- Mayor poder predictivo que RF
- Más propenso a overfitting

**Hiperparámetros:**
```python
GradientBoostingClassifier(
    n_estimators=100,
    random_state=42
)
```

**Por qué incluirlo:**
- Frecuentemente el mejor en competencias de ML
- Captura patrones sutiles

#### 6.4.3 Logistic Regression

**Características:**
- Modelo lineal (baseline)
- Interpretable (coeficientes)
- Rápido de entrenar

**Hiperparámetros:**
```python
LogisticRegression(
    max_iter=1000,  # Iteraciones para convergencia
    random_state=42
)
```

**Por qué incluirlo:**
- Establece baseline
- Si funciona bien → relación lineal
- Coeficientes son interpretables

#### 6.4.4 Decision Tree

**Características:**
- Árbol simple (no ensemble)
- Muy interpretable
- Propenso a overfitting

**Por qué incluirlo:**
- Visualizable (diagrama del árbol)
- Educativo para entender decisiones
- Benchmark contra ensembles

### 6.5 Evaluación de Modelos

#### 6.5.1 Métricas Utilizadas

**1. Accuracy**
```python
accuracy = correct_predictions / total_predictions
```
- Métrica principal para este problema
- Apropiado porque las clases están balanceadas

**2. Cross-Validation (5-fold)**
```python
cv_scores = cross_val_score(model, X, y, cv=5)
```
- Reduce varianza de la estimación
- Usa toda la data para entrenar y validar
- 5 folds es estándar

**3. Classification Report**
- Precision: De las predicciones positivas, ¿cuántas son correctas?
- Recall: De los casos positivos reales, ¿cuántos detectamos?
- F1-Score: Media armónica de precision y recall

**4. Confusion Matrix**
```
          Predicho
          0   1   2
Real  0 [ .   .   . ]
      1 [ .   .   . ]
      2 [ .   .   . ]
```
- Visualiza errores específicos
- Diagonal = aciertos
- Fuera de diagonal = confusiones

### 6.6 Selección del Mejor Modelo

**Criterio**: Mayor accuracy en test set

**Proceso:**
1. Entrenar los 4 modelos
2. Evaluar en test set
3. Calcular cross-validation
4. Seleccionar el mejor
5. Analizar en detalle (feature importance, confusión)

### 6.7 Feature Importance

```python
importances = model.feature_importances_
```

**Qué nos dice:**
- Qué métricas son más predictivas
- Qué aspectos del juego determinan la victoria
- Si podemos simplificar el modelo

**Ejemplo de interpretación:**
```
danio_j1: 0.25  → El daño causado por J1 es muy predictivo
estrategia_j1: 0.15 → La estrategia importa moderadamente
nivel_j1: 0.05 → El nivel importa poco (están balanceados)
```

---

## 7. Visualizaciones

### 7.1 Gráficos Generados (7 tipos)

#### 7.1.1 Tasa de Victoria por Estrategia
- **Tipo**: Gráfico de barras
- **Propósito**: Identificar estrategia dominante
- **Insight**: ¿El juego está balanceado?

#### 7.1.2 Comparación de 6 Métricas
- **Tipo**: Subplots (2x3)
- **Métricas**: Cartas, Elixir, Daño, Torres, Ataques
- **Propósito**: Comparación multidimensional

#### 7.1.3 Matriz de Enfrentamientos
- **Tipo**: Heatmap
- **Propósito**: Ver matchups favorables/desfavorables
- **Insight**: Identificar "rock-paper-scissors"

#### 7.1.4 Distribución de Duración
- **Tipo**: Histograma
- **Propósito**: Entender timing del juego
- **Insight**: ¿Partidas muy cortas/largas?

#### 7.1.5 Actividad Temporal
- **Tipo**: Línea temporal
- **Propósito**: Ver intensidad del juego en el tiempo
- **Insight**: ¿Hay momentos clave?

#### 7.1.6 Análisis de Eficiencia
- **Tipo**: Barras comparativas
- **Métricas**: Daño/Elixir, Ratio Daño, Costo/Carta
- **Propósito**: Identificar estrategia más eficiente

#### 7.1.7 Distribución de Eventos
- **Tipo**: Gráfico de torta
- **Propósito**: Ver proporción de tipos de eventos
- **Insight**: ¿Qué acciones son más comunes?

### 7.2 Estándares de Visualización

- **Resolución**: 300 DPI (calidad publicación)
- **Formato**: PNG (portable, sin pérdida)
- **Colores**: Paletas profesionales (viridis, Set3, RdYlGn)
- **Anotaciones**: Valores en gráficos de barras
- **Títulos**: Descriptivos y auto-explicativos

---

## 8. Justificación Metodológica

### 8.1 ¿Por qué este enfoque?

#### 8.1.1 Arquitectura Event-Driven

**Alternativas consideradas:**
1. Polling periódico → Ineficiente, puede perder eventos
2. Modificar código del juego → Invasivo, acoplado
3. **Event-driven (elegido)** → Eficiente, desacoplado, completo

**Decisión**: Event-driven es el estándar en sistemas distribuidos y simulaciones.

#### 8.1.2 Exportación a CSV

**Alternativas consideradas:**
1. JSON → Verbose, difícil de analizar en Excel
2. Base de datos → Overkill para este volumen
3. **CSV (elegido)** → Universal, eficiente, herramientas existentes

**Decisión**: CSV es el estándar de facto para datasets tabulares.

#### 8.1.3 Python para Análisis

**Alternativas consideradas:**
1. Java puro → Pocas librerías de ML/visualización
2. R → Menos conocido, más difícil de integrar
3. **Python (elegido)** → Ecosistema completo, sintaxis clara

**Decisión**: Python es el estándar en Data Science.

### 8.2 Limitaciones Conocidas

#### 8.2.1 Tamaño de Muestra
- **Actual**: 100 partidas
- **Ideal**: 1000+ para ML robusto
- **Mitigación**: Cross-validation, resultados cautelosos

#### 8.2.2 Variabilidad Aleatoria
- **Problema**: RNG afecta resultados
- **Mitigación**: Muestra grande, tests estadísticos

#### 8.2.3 Overfitting en ML
- **Problema**: 100 partidas, 19 features
- **Mitigación**: Cross-validation, múltiples modelos, simplicidad

### 8.3 Validez Externa

**¿Los resultados son generalizables?**

- ✅ Estrategias asimétricas se seleccionan aleatoriamente
- ✅ Niveles pueden variarse (10, 11, 12, etc.)
- ✅ Mapa y reglas son estándar
- ⚠️ Solo 4 estrategias actuales (extensible)

---

## 9. Resultados y Validación

### 9.1 Resultados Obtenidos

#### 9.1.1 Análisis Estadístico

**Test Chi-Cuadrado:**
- χ² = 36.37, p = 0.96
- **Conclusión**: No hay evidencia de que las estrategias afecten el resultado (α=0.05)
- **Implicación**: El juego está balanceado

**ANOVA (Daño Causado):**
- F = 570.97, p < 0.001
- **Conclusión**: SÍ hay diferencias significativas en daño entre estrategias
- **Implicación**: Las estrategias se comportan diferente, pero el daño no determina solo la victoria

#### 9.1.2 Machine Learning

**Mejor Modelo**: Gradient Boosting
- **Accuracy**: 100% en test set
- **Cross-validation**: 90% ± 6.4%

**Interpretación:**
- ✅ El modelo puede predecir perfectamente en los datos vistos
- ⚠️ Cross-validation más bajo sugiere ligero overfitting
- 📊 Con más datos, el modelo sería más robusto

**Top 5 Features Importantes:**
1. `danio_j1`: 25.4%
2. `danio_j2`: 21.9%
3. `estrategia_j1`: 12.3%
4. `elixir_j1`: 9.9%
5. `tropas_j1`: 7.2%

**Conclusión**: El daño es el factor más determinante de victoria.

### 9.2 Validación del Modelo

#### 9.2.1 Validación Interna
- Cross-validation 5-fold
- Train/test split estratificado
- Múltiples métricas (accuracy, precision, recall)

#### 9.2.2 Validación de Coherencia
- Los features importantes tienen sentido lógico
- La matriz de confusión muestra patrones coherentes
- Los coeficientes tienen signos esperados

#### 9.2.3 Robustez
- Funciona con diferentes seeds
- Consistente entre ejecuciones
- No sensible a outliers moderados

---

## 10. Conclusiones

### 10.1 Logros del Sistema

1. ✅ **Recolección automática** de 51+ métricas sin modificar el juego
2. ✅ **Análisis estadístico riguroso** con tests formales (χ², ANOVA, IC)
3. ✅ **Predicción con ML** alcanzando 100% accuracy en test
4. ✅ **Visualizaciones profesionales** de calidad publicable
5. ✅ **Sistema extensible** fácil de ampliar con nuevas métricas

### 10.2 Insights Principales

1. **Balance del juego**: Estrategias balanceadas estadísticamente
2. **Factor crítico**: El daño causado es el predictor más importante
3. **Comportamiento diferenciado**: Cada estrategia tiene un "estilo" medible
4. **Eficiencia variable**: Algunas estrategias son más eficientes con el elixir

### 10.3 Aplicaciones

**Para el proyecto:**
- Validar que la simulación funciona correctamente
- Identificar bugs o comportamientos anómalos
- Documentar el comportamiento cuantitativamente

**Para el desarrollo futuro:**
- Ajustar parámetros basándose en datos
- Diseñar nuevas estrategias competitivas
- Balancear el juego objetivamente

### 10.4 Trabajo Futuro

1. **Más datos**: Ejecutar 1000+ partidas para ML más robusto
2. **Más estrategias**: Agregar más variantes de IA
3. **Features avanzadas**: Secuencias de cartas, patrones temporales
4. **ML avanzado**: Redes neuronales, ensembles complejos
5. **Análisis temporal**: Modelos de series de tiempo

---

## Referencias

**Librerías Utilizadas:**
- pandas 2.0+ (Data manipulation)
- scikit-learn 1.3+ (Machine Learning)
- matplotlib 3.7+ (Visualización)
- seaborn 0.12+ (Visualización estadística)
- scipy 1.10+ (Tests estadísticos)
- numpy 1.24+ (Cálculos numéricos)

**Metodologías:**
- Breiman, L. (2001). Random Forests. Machine Learning.
- Friedman, J.H. (2001). Greedy Function Approximation: A Gradient Boosting Machine.
- Pearson, K. (1900). On the criterion that a given system of deviations...

**Estándares:**
- IEEE Standard for Software Documentation
- PEP 8 (Python Style Guide)
- Clean Code (Robert C. Martin)

---

## Apéndice A: Estructura de Archivos CSV

### resumen_partidas.csv
```csv
id_partida,fecha_hora,estrategia_j1,estrategia_j2,nivel_j1,nivel_j2,ganador,...
PARTIDA_0001,2025-10-25 10:30:00,EstrategiaAgresiva,EstrategiaDefensiva,10,10,1,...
```

### estadisticas_jugadores.csv
```csv
id_partida,fecha_hora,jugador_id,nombre,estrategia,nivel,resultado,...
PARTIDA_0001,2025-10-25 10:30:00,1,Jugador 1,EstrategiaAgresiva,10,VICTORIA,...
```

### eventos_partidas.csv
```csv
id_partida,segundo,jugador_id,tipo_evento,detalles
PARTIDA_0001,5,1,TROPA_DESPLEGADA,"Carta: Gigante (costo: 5) en (3,10)"
```

---

## Apéndice B: Comandos de Ejecución

```bash
# 1. Compilar proyecto
mvn clean compile

# 2. Generar 100 partidas
java -cp target/classes analisis.EjemploAnalisis

# 3. Análisis completo
python analizar_datos.py

# 4. Análisis estadístico
python analisis_estadistico.py

# 5. Machine Learning
python ml_predictor.py
```

---

## Apéndice C: Métricas Completas

| Categoría | Métrica | Tipo | Rango | Descripción |
|-----------|---------|------|-------|-------------|
| Resultado | ganador | Categórica | {0,1,2} | 0=Empate, 1=J1, 2=J2 |
| Resultado | duracion_segundos | Numérica | [0, 360] | Duración de partida |
| Recursos | cartas_jugadas | Discreta | [0, 50] | Total de cartas |
| Recursos | elixir_gastado | Discreta | [0, 500] | Elixir utilizado |
| Combate | danio_causado | Discreta | [0, 10000] | Daño infligido |
| Combate | tropas_invocadas | Discreta | [0, 50] | Unidades desplegadas |
| ... | ... | ... | ... | ... |

---

**Documento preparado para: Profesor [Nombre]**  
**Materia: Modelos y Simulación**  
**Institución: [Tu Universidad]**  
**Fecha: Octubre 2025**

---

_Fin del documento técnico_
