# Sistema de Análisis de Datos - Clash Royale Simulation

Este sistema permite recolectar, analizar y visualizar datos de las partidas del simulador de Clash Royale.

## 📁 Estructura del Proyecto

```
src/main/java/analisis/
├── RegistroPartida.java              # Contenedor de datos de una partida
├── EstadisticasPartidaJugador.java   # Estadísticas por jugador
├── EventoPartida.java                # Representa eventos del juego
├── RecolectorDatos.java              # Escucha y registra eventos
├── ExportadorCSV.java                # Exporta datos a CSV
├── GestorAnalisis.java               # Analiza y genera reportes
└── EjemploAnalisis.java              # Ejemplo de uso completo

datos_analisis/                        # Carpeta de salida (se crea automáticamente)
├── resumen_partidas.csv              # Resumen general
├── estadisticas_jugadores.csv        # Stats detalladas
├── eventos_partidas.csv              # Todos los eventos
└── *.png                             # Gráficos generados

analizar_datos.py                      # Script de análisis en Python
```

## 🚀 Uso Básico

### Paso 1: Generar Datos

Ejecuta el ejemplo de análisis para generar datos de múltiples partidas:

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar el generador de datos
java -cp target/classes analisis.EjemploAnalisis
```

Esto ejecutará 100 partidas y generará 3 archivos CSV en `datos_analisis/`.

### Paso 2: Analizar con Python

Instala las dependencias de Python:

```bash
pip install pandas matplotlib seaborn numpy
```

Ejecuta el script de análisis:

```bash
python analizar_datos.py
```

Esto generará:
- 7 gráficos PNG con visualizaciones
- Análisis estadístico impreso en consola

## 📊 Datos Recolectados

### Archivo: resumen_partidas.csv

Contiene un resumen de cada partida:
- **id_partida**: Identificador único
- **fecha_hora**: Timestamp de la partida
- **estrategia_j1/j2**: Estrategias usadas
- **nivel_j1/j2**: Niveles de jugadores
- **ganador**: 0=empate, 1=J1, 2=J2
- **motivo_victoria**: Razón del resultado
- **duracion_segundos**: Duración total
- **torres_destruidas_j1/j2**: Torres derribadas
- **vida_final_j1/j2**: Vida restante en torres
- **cartas_jugadas_j1/j2**: Cartas utilizadas
- **elixir_gastado_j1/j2**: Elixir consumido
- **tropas_invocadas_j1/j2**: Tropas desplegadas
- **danio_causado/recibido_j1/j2**: Daño total
- **ataques_j1/j2**: Ataques realizados

### Archivo: estadisticas_jugadores.csv

Estadísticas detalladas por jugador (2 filas por partida):
- Todas las métricas del resumen
- **promedio_elixir_carta**: Costo promedio de cartas
- **ratio_danio**: Relación daño causado/recibido
- **primer_carta_segundo**: Momento de primera carta
- **ultima_carta_segundo**: Momento de última carta
- **tropas_muertas**: Tropas eliminadas
- **danio_a_torres**: Daño específico a torres

### Archivo: eventos_partidas.csv

Registro cronológico de todos los eventos:
- **id_partida**: Partida correspondiente
- **segundo**: Momento del evento
- **jugador_id**: Jugador que ejecutó la acción
- **tipo_evento**: CARTA_JUGADA, TROPA_DESPLEGADA, ATAQUE_REALIZADO, etc.
- **detalles**: Información específica del evento

## 📈 Visualizaciones Generadas

1. **tasa_victoria.png**: Tasa de victoria por estrategia
2. **comparacion_metricas.png**: 6 métricas comparativas
3. **matriz_enfrentamientos.png**: Heatmap de enfrentamientos
4. **distribucion_duracion.png**: Histograma de duración
5. **actividad_temporal.png**: Eventos a lo largo del tiempo
6. **analisis_eficiencia.png**: Daño/Elixir y otros ratios
7. **distribucion_eventos.png**: Gráfico de torta de eventos

## 🔧 Integración en Código Personalizado

```java
// 1. Crear gestor de análisis
GestorAnalisis gestorAnalisis = new GestorAnalisis();

// 2. Por cada partida:
Partida partida = new Partida(config);
RecolectorDatos recolector = new RecolectorDatos(
    partida.getJugador1(), 
    partida.getJugador2(),
    partida.getTablero()
);

// 3. Iniciar recolección
String idPartida = "PARTIDA_" + UUID.randomUUID();
recolector.iniciarNuevaPartida(idPartida);

// 4. Suscribir a eventos
partida.getEventManager().subscribe(GameEvent.class, recolector);

// 5. Ejecutar partida
partida.inicializar();
while (!partida.isPartidaTerminada()) {
    partida.ejecutarTick();
    recolector.actualizarTick(partida.getTickActual());
}

// 6. Guardar registro
gestorAnalisis.agregarRegistro(recolector.getRegistroActual());

// 7. Al final, exportar
ExportadorCSV.exportarResumenPartidas(
    gestorAnalisis.getRegistros(), 
    "datos_analisis/resumen_partidas.csv"
);
```

## 📊 Análisis Personalizado en Python

```python
import pandas as pd
from analizar_datos import AnalizadorClashRoyale

# Crear analizador
analizador = AnalizadorClashRoyale()
analizador.cargar_datos()

# Acceder a los datos
df_partidas = analizador.df_partidas
df_jugadores = analizador.df_jugadores
df_eventos = analizador.df_eventos

# Análisis personalizado
# Ejemplo: Mejores estrategias por nivel
por_nivel = df_jugadores.groupby(['nivel', 'estrategia']).agg({
    'resultado': lambda x: (x == 'VICTORIA').sum()
}).reset_index()

print(por_nivel)
```

## 🔍 Preguntas que Puede Responder

El sistema permite analizar:

1. **Estrategias**
   - ¿Qué estrategia tiene mayor tasa de victoria?
   - ¿Cómo se desempeñan en diferentes enfrentamientos?
   - ¿Cuál es más eficiente con el elixir?

2. **Balance del Juego**
   - ¿Hay estrategias dominantes?
   - ¿Las partidas son equilibradas?
   - ¿Qué tan predecible es el resultado?

3. **Comportamiento Temporal**
   - ¿Cuánto duran las partidas típicamente?
   - ¿En qué momentos hay más actividad?
   - ¿Cuándo se juegan las primeras cartas?

4. **Eficiencia**
   - ¿Qué estrategia causa más daño por elixir?
   - ¿Cuál tiene mejor ratio de daño?
   - ¿Cuál despliega más tropas?

5. **Eventos**
   - ¿Cuál es la distribución de tipos de eventos?
   - ¿Cuántos ataques hay por partida?
   - ¿Cuántas tropas se destruyen?

## 💡 Tips

- Ejecuta al menos 100 partidas para obtener resultados estadísticamente significativos
- Modifica `EjemploAnalisis.java` para cambiar el número de partidas
- Los CSVs son compatibles con Excel, Google Sheets, R, etc.
- Puedes crear tus propios scripts de análisis en cualquier lenguaje

## 🐛 Solución de Problemas

**Error: No se encuentran los archivos CSV**
- Asegúrate de haber ejecutado primero `EjemploAnalisis.java`
- Verifica que la carpeta `datos_analisis/` existe

**Error: Módulo no encontrado (Python)**
- Instala las dependencias: `pip install pandas matplotlib seaborn numpy`

**La compilación falla**
- Verifica que tienes Java 11 o superior
- Ejecuta `mvn clean install` primero

## 📝 Extendiendo el Sistema

### Agregar Nuevas Métricas

1. Añade campos en `EstadisticasPartidaJugador.java`
2. Actualiza `RecolectorDatos.java` para calcularlas
3. Modifica `ExportadorCSV.java` para exportarlas
4. Actualiza el script Python para visualizarlas

### Agregar Nuevos Tipos de Eventos

1. Crea el evento en `juego/events/`
2. Añade el tipo en `EventoPartida.TipoEvento`
3. Procesa en `RecolectorDatos.onGameEvent()`

## 📧 Soporte

Para preguntas o problemas, revisa el código de ejemplo en `EjemploAnalisis.java`.
