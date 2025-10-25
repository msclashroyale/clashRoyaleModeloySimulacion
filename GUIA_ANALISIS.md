# 🎮 Sistema de Análisis de Datos - Clash Royale Simulation

## 📋 Resumen Ejecutivo

Se ha creado un **sistema completo de análisis de datos** para tu simulador de Clash Royale que permite:

✅ Recolectar datos automáticamente de cada partida  
✅ Exportar a archivos CSV para análisis  
✅ Generar estadísticas detalladas  
✅ Crear visualizaciones profesionales  
✅ Analizar estrategias y balance del juego  

---

## 📦 Componentes Creados

### 1. Sistema Java (src/main/java/analisis/)

| Archivo | Propósito |
|---------|-----------|
| `RegistroPartida.java` | Almacena toda la información de una partida |
| `EstadisticasPartidaJugador.java` | Estadísticas detalladas por jugador |
| `EventoPartida.java` | Representa eventos del juego (ataques, despliegues, etc.) |
| `RecolectorDatos.java` | Escucha eventos y los registra en tiempo real |
| `ExportadorCSV.java` | Exporta datos a archivos CSV |
| `GestorAnalisis.java` | Analiza datos y genera reportes en texto |
| `EjemploAnalisis.java` | Ejemplo completo de uso (ejecuta 100 partidas) |

### 2. Scripts Python

| Archivo | Propósito |
|---------|-----------|
| `analizar_datos.py` | Script completo con 7 visualizaciones |
| `analisis_rapido.py` | Análisis rápido en consola (sin gráficos) |

### 3. Archivos CSV Generados (datos_analisis/)

| Archivo | Contenido |
|---------|-----------|
| `resumen_partidas.csv` | Resumen de cada partida (ganador, duración, torres, etc.) |
| `estadisticas_jugadores.csv` | Stats detalladas por jugador en cada partida |
| `eventos_partidas.csv` | Registro cronológico de todos los eventos |

### 4. Visualizaciones PNG Generadas

1. **tasa_victoria.png** - Gráfico de barras con tasa de victoria por estrategia
2. **comparacion_metricas.png** - 6 métricas comparativas entre estrategias
3. **matriz_enfrentamientos.png** - Heatmap de enfrentamientos directos
4. **distribucion_duracion.png** - Histograma de duración de partidas
5. **actividad_temporal.png** - Actividad del juego en el tiempo
6. **analisis_eficiencia.png** - Eficiencia de daño y elixir
7. **distribucion_eventos.png** - Gráfico de torta de tipos de eventos

---

## 🚀 Guía de Uso Rápida

### Opción 1: Usar el Ejemplo Completo

```bash
# 1. Compilar el proyecto
mvn clean compile

# 2. Ejecutar generador de datos (100 partidas)
java -cp target/classes analisis.EjemploAnalisis

# 3. Analizar con visualizaciones
python analizar_datos.py

# O análisis rápido sin gráficos
python analisis_rapido.py
```

### Opción 2: Integrar en Tu Código

```java
// En tu main o donde ejecutes partidas:

GestorAnalisis gestorAnalisis = new GestorAnalisis();

for (int i = 0; i < 100; i++) {
    Partida partida = new Partida(config);
    RecolectorDatos recolector = new RecolectorDatos(
        partida.getJugador1(), 
        partida.getJugador2(),
        partida.getTablero()
    );
    
    String id = "PARTIDA_" + i;
    recolector.iniciarNuevaPartida(id);
    partida.getEventManager().subscribe(GameEvent.class, recolector);
    
    partida.inicializar();
    while (!partida.isPartidaTerminada()) {
        partida.ejecutarTick();
        recolector.actualizarTick(partida.getTickActual());
    }
    
    gestorAnalisis.agregarRegistro(recolector.getRegistroActual());
}

// Exportar a CSV
ExportadorCSV.exportarResumenPartidas(
    gestorAnalisis.getRegistros(), 
    "datos_analisis/resumen_partidas.csv"
);
ExportadorCSV.exportarEstadisticasJugadores(
    gestorAnalisis.getRegistros(), 
    "datos_analisis/estadisticas_jugadores.csv"
);
ExportadorCSV.exportarEventos(
    gestorAnalisis.getRegistros(), 
    "datos_analisis/eventos_partidas.csv"
);
```

---

## 📊 Tipos de Análisis Disponibles

### 1. Análisis de Estrategias
- Tasa de victoria de cada estrategia
- Cartas jugadas promedio
- Elixir gastado
- Daño causado y recibido
- Torres destruidas

### 2. Análisis de Enfrentamientos
- Matriz de victorias entre estrategias
- Matchups favorables/desfavorables
- Patrones de dominancia

### 3. Análisis Temporal
- Duración promedio de partidas
- Distribución de duraciones
- Actividad por segundo
- Timing de cartas

### 4. Análisis de Eficiencia
- Daño por elixir gastado
- Ratio daño causado/recibido
- Costo promedio de cartas
- Eficiencia de recursos

### 5. Análisis de Eventos
- Distribución de tipos de eventos
- Frecuencia de ataques
- Tropas desplegadas vs muertas
- Torres destruidas

---

## 🎯 Preguntas que Puedes Responder

Con este sistema puedes contestar:

### Balance del Juego
- ¿Hay estrategias dominantes?
- ¿El juego está balanceado?
- ¿Qué estrategia es más fuerte?

### Comportamiento de Estrategias
- ¿Cómo se comporta cada estrategia?
- ¿Cuál es más agresiva/defensiva?
- ¿Cuál usa mejor el elixir?

### Dinámica de Partidas
- ¿Cuánto duran las partidas?
- ¿Cuándo se juegan las primeras cartas?
- ¿Qué eventos son más comunes?

### Optimización
- ¿Qué estrategia necesita buff/nerf?
- ¿Hay combos dominantes?
- ¿Cómo mejorar el balance?

---

## 📁 Estructura de Archivos

```
clashRoyaleModeloySimulacion/
│
├── src/main/java/analisis/          # Sistema de análisis Java
│   ├── RegistroPartida.java
│   ├── EstadisticasPartidaJugador.java
│   ├── EventoPartida.java
│   ├── RecolectorDatos.java
│   ├── ExportadorCSV.java
│   ├── GestorAnalisis.java
│   └── EjemploAnalisis.java
│
├── datos_analisis/                  # Datos generados
│   ├── resumen_partidas.csv
│   ├── estadisticas_jugadores.csv
│   ├── eventos_partidas.csv
│   ├── resumen_analisis.txt
│   └── *.png (7 gráficos)
│
├── analizar_datos.py                # Script Python completo
├── analisis_rapido.py               # Script Python rápido
├── README_ANALISIS.md               # Documentación detallada
└── GUIA_ANALISIS.md                 # Este archivo
```

---

## 🔧 Requisitos

### Java
- Java 11 o superior
- Maven
- Tu proyecto compilado

### Python (opcional, para visualizaciones)
```bash
pip install pandas matplotlib seaborn numpy
```

---

## 💡 Ejemplos de Uso Avanzado

### Filtrar por Estrategia Específica

```python
import pandas as pd

df = pd.read_csv('datos_analisis/estadisticas_jugadores.csv')

# Solo partidas de EstrategiaAgresiva
agresiva = df[df['estrategia'] == 'EstrategiaAgresiva']
tasa_victoria = (agresiva['resultado'] == 'VICTORIA').sum() / len(agresiva) * 100
print(f"Tasa de victoria: {tasa_victoria:.1f}%")
```

### Analizar Eventos por Tiempo

```python
df_eventos = pd.read_csv('datos_analisis/eventos_partidas.csv')

# Eventos en los primeros 60 segundos
inicio = df_eventos[df_eventos['segundo'] <= 60]
print(inicio['tipo_evento'].value_counts())
```

### Comparar Niveles

```python
df_partidas = pd.read_csv('datos_analisis/resumen_partidas.csv')

# Impacto del nivel en victorias
df_partidas['ventaja_nivel'] = df_partidas['nivel_j1'] - df_partidas['nivel_j2']
correlacion = df_partidas['ventaja_nivel'].corr(df_partidas['ganador'])
print(f"Correlación nivel-victoria: {correlacion:.3f}")
```

---

## 🎓 Para Tu Proyecto de Modelado y Simulación

Este sistema te permite:

1. **Validar tu modelo**: ¿Las estrategias se comportan como esperabas?
2. **Ajustar parámetros**: Identifica qué necesita balanceo
3. **Documentar resultados**: Genera gráficos para tu informe
4. **Análisis estadístico**: Datos cuantitativos para conclusiones
5. **Experimentación**: Prueba diferentes configuraciones fácilmente

---

## 📝 Próximos Pasos Sugeridos

1. ✅ **Ya hecho**: Sistema básico funcional
2. 🔄 **Opcional**: Agregar más métricas personalizadas
3. 🔄 **Opcional**: Análisis de secuencias de cartas
4. 🔄 **Opcional**: Machine Learning para predecir ganadores
5. 🔄 **Opcional**: Interfaz web para visualizar en tiempo real

---

## 🐛 Solución de Problemas Comunes

### "No se encuentran los CSV"
→ Ejecuta primero `EjemploAnalisis.java` para generar los datos

### "ModuleNotFoundError en Python"
→ Instala dependencias: `pip install pandas matplotlib seaborn numpy`

### "ClassNotFoundException"
→ Compila con `mvn clean compile` primero

### "Los gráficos se ven mal"
→ Aumenta el DPI en el código Python (busca `dpi=300`)

---

## 📧 Notas Finales

- Los CSV son compatibles con Excel, R, SPSS, etc.
- Puedes modificar fácilmente para agregar tus propias métricas
- El sistema es extensible y modular
- Todos los archivos están documentados con comentarios

**¡El sistema está listo para usar!** 🎉

Ejecuta `EjemploAnalisis.java` y luego `analizar_datos.py` para ver los resultados.
