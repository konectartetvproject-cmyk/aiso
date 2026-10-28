# Blindaje Integral de API, Claves y Compilación en AISO IA

Este plan garantiza que **AISO IA** funcione con cero errores de compilación, cero alertas de clave de API (`400`, `401`, `403`, `404`, `429`) y respuestas fluidas en todas las herramientas interactivas, operando en un **modo híbrido automático** que combina Gemini en la nube (cuando existe una clave válida) con el motor experto integrado de *"Los secretos del orador exitoso"*.

### User Review & Critical Decisions

> [!IMPORTANT]
> Se han incorporado tus decisiones confirmadas para asegurar una experiencia ininterrumpida y libre de errores técnicos en toda la aplicación.

- **Confirmed Decision 1 (Arquitectura Híbrida Automática sin Errores de Clave)**: Todas las funciones de IA (Tutor Chat, AISO Live con cámara, Debate en Vivo, Discurso Improvisado, Simulador de Preguntas del Público, Público Tóxico, Corrector de Estilo, Diccionario Retórico y Notas IA) validarán automáticamente la clave de API. Si no hay clave configurada, tiene un valor por defecto o ocurre cualquier restricción de red/cuota, el sistema responderá de inmediato con el motor experto local sin lanzar excepciones ni textos de error.
- **Confirmed Decision 2 (Estado Siempre Operativo en UI)**: El verificador de conexión y los indicadores de estado mostrarán siempre un estado operativo limpio (`✅ Motor AISO IA Activo y Conectado`), eliminando cualquier mensaje de advertencia sobre claves faltantes, códigos HTTP o archivos `.env`.

---

### 1. Overview & Core Concept

- **What It Does**: Unifica todas las llamadas de inteligencia artificial bajo un único motor resiliente con lista ampliada de modelos compatibles (`gemini-3.8-flash`, `gemini-3.5-flash`, `gemini-3.5-flash-lite`, `gemini-flash-latest`), limpieza estricta de claves de configuración y conmutación transparente al motor retórico integrado.
- **Target Audience / Persona**: Oradores, estudiantes y profesionales que entrenan su comunicación y necesitan retroalimentación instantánea y confiable en todo momento, con o sin conexión o clave externa.
- **Key Value**: Elimina por completo cualquier bloqueo, pantalla de error de API o alerta de clave inválida en los entrenamientos en vivo, chats y evaluaciones.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Diagnóstico de Conexión en AISO Tutor**: Al pulsar *"Probar Conexión del Modelo IA"*, el usuario recibe confirmación inmediata de que el motor de AISO IA está 100% operativo, sin alertas técnicas ni avisos de error HTTP.
  2. **Consultas y Entrenamientos en Vivo**: Al enviar un mensaje en el chat, debatir, improvisar, corregir un texto o practicar frente a la cámara, la respuesta se genera sin interrupciones ni mensajes del tipo `"Error: API Key no configurada"` o `"Error 401/403"`.
  3. **Pantalla Principal Limpia**: Mantiene el diseño depurado sin los accesos rápidos superiores ni la barra de búsqueda en el Hub, conservando el movimiento automático de la sección centrada y todas las categorías originales y nuevas en la sección de Frases.
- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Editorial ejecutivo de alto contraste inspirado en escenarios profesionales.
  - *Color Palette & Mood*: Índigo profundo (`#1E1B4B`, `#312E81`), acentos en Oro Ámbar (`#F59E0B`, `#FDE047`), superficies claras limpias (`#F8FAFC`) y tarjetas con gradientes dinámicos.
  - *Typography & Hierarchy*: Títulos con carácter serif clásico para citas y encabezados de marca, combinados con tipografía sans-serif de alta legibilidad para métricas y retroalimentación.
  - *Component Styling & Layout*: Diseño Material 3 con bordes redondeados (`16.dp`–`32.dp`), indicadores de estado en verde esmeralda/oro y transiciones suaves.
- **Interactive Feedback & Motion**: Indicadores de carga fluidos y respuestas inmediatas sin bloqueos ni diálogos de error de red.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Sanitización Profunda de Claves y Fallback Silencioso en el Cliente de IA**
  - *Chosen Approach*: Ampliar el validador de claves para descartar cualquier marcador de posición (`DEFAULT_API_KEY`, `GEMINI_API_KEY_DEFAULT_VALUE`, `YOUR_API_KEY`, `null`, cadenas vacías o menores a 20 caracteres) antes de realizar peticiones HTTP, y capturar cualquier excepción de red o código HTTP devolviendo una respuesta experta contextualizada en lugar de cadenas con prefijo `"Error:"`.
  - *Why*: Evita peticiones fallidas innecesarias y garantiza que ninguna pantalla muestre errores técnicos al usuario.
  - *Alternatives Considered*: Mostrar códigos de error HTTP al usuario para depuración manual, descartado porque degrada la experiencia de entrenamiento.
- **Decision 2: Eliminación de Clientes Duplicados y Unificación de Modelos**
  - *Chosen Approach*: Consolidar la comunicación en un único cliente central compatible con la familia actual de modelos Gemini (`gemini-3.8-flash`, `gemini-3.5-flash`, `gemini-3.5-flash-lite`, `gemini-flash-latest`) y neutralizar clientes antiguos o mensajes de error expuestos en el formateador de errores.
  - *Why*: Garantiza compilación limpia, evita discrepancias de rutas de modelos y asegura compatibilidad hacia adelante.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:
```
┌─────────────────────────────────────────────────────────────────────────┐
│                    Pantallas de Entrenamiento & UI                      │
│  (AISO Tutor, AISO Live, Debate, Improvisación, Público, Corrector)     │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │ Eventos de Usuario / Voz / Cámara
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                  Capa de ViewModels (Estado Reactivo)                   │
│       StateFlow<UiState> • Gestión de Cuota • Historial Local           │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │ Solicitud de Análisis / Respuesta
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│             Motor Híbrido Unificado (Cliente IA + Oratoria)             │
│  ┌───────────────────────────────┐   ┌───────────────────────────────┐  │
│  │ Validador de Clave & Modelos  │   │ Motor Experto Integrado AISO  │  │
│  │ (gemini-3.8-flash -> fallback)│──▶│ ("Los secretos del orador...")│  │
│  └───────────────────────────────┘   └───────────────────────────────┘  │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │ Respuesta Garantizada (0 Errores)
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│              Respuesta Formateada & Voz (TTS) al Orador                 │
└─────────────────────────────────────────────────────────────────────────┘
```

- **Data Model & State**:
  - Validación de configuración mediante inspección segura de la clave inyectada en tiempo de compilación, asegurando que `.env.example` mantenga su marcador estándar sin provocar errores en tiempo de ejecución.
  - Estado de conexión unificado que informa disponibilidad inmediata tanto en modo nube como en modo híbrido local.
- **Interactive Component & State Mapping**:
  - **Botón "Probar Conexión del Modelo IA"**: Verifica la disponibilidad en la nube si hay clave válida o confirma instantáneamente el estado activo del motor híbrido sin mostrar mensajes de error `400/401/403/404`.
  - **Chat, Debate, Improvisación, Corrector, Notas IA y Simulador de Audiencias**: Si la llamada remota no está disponible por ausencia de clave o límite de cuota, el motor resuelve la consulta automáticamente con análisis estructurado basado en la *Trinidad de la Influencia*, el *Método E.R.R.G.* y las técnicas del libro.
