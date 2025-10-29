# 📊 Guía de Presentación Oral - 10 minutos

## Estructura Sugerida

### 1. Introducción (1 min)
- "Desarrollamos un sistema de análisis completo para validar nuestro simulador"
- 3 componentes: Recolección + Estadística + Machine Learning

### 2. Arquitectura (1 min)
- Patrón Observer (no invasivo)
- 51+ métricas capturadas automáticamente
- Export a CSV → Análisis Python

### 3. Análisis Estadístico (2 min)
- **Test χ²**: p=0.96 → Juego balanceado
- **ANOVA**: p<0.001 → Estrategias diferentes
- **IC 95%**: Cuantifica incertidumbre

### 4. Machine Learning (3 min)
- **Objetivo**: Predecir ganador
- **Features**: 19 variables (estrategias, daño, elixir, ratios)
- **Modelos**: Random Forest, Gradient Boosting, etc.
- **Resultado**: 100% accuracy, feature importance

### 5. Conclusiones (1 min)
- Juego balanceado ✅
- Daño es factor crítico ✅
- Sistema extensible ✅

### 6. Q&A (2 min)

## Puntos Clave a Enfatizar

✅ **Rigor metodológico** (tests formales, no solo gráficos)
✅ **Completitud** (51+ métricas, múltiples análisis)
✅ **Aplicabilidad** (valida el modelo, detecta bugs)

Ver `DOCUMENTACION_TECNICA_PROFESOR.md` para detalles completos.
