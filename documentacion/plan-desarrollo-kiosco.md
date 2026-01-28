# 📦 Plan de desarrollo – Web App de Gestión de Stock para Kiosco

## 🎯 Objetivo del proyecto

Desarrollar una web app responsive para la gestión de mercadería y stock de un kiosco, accesible desde el celular, que permita escanear productos por código de barras, administrar precios y stock, y registrar ventas.

En una etapa posterior, la aplicación podrá evolucionar a PWA sin reescritura del sistema.

---

## 🧱 Stack tecnológico

### Backend
- **Java 17+**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **MySQL**
- **Spring Security** (fase posterior, login simple)

### Frontend
- **Thymeleaf**
- **Bootstrap** (mobile-first)
- **JavaScript**
- **Librería de escaneo de código de barras** (QuaggaJS o ZXing)

### Infraestructura
- App empaquetada como `.jar`
- Hosting compatible (Render / Railway / VPS)
- **HTTPS** (necesario para cámara y futura PWA)

---

## 📁 Estructura base del proyecto

**Nota:** El proyecto real se llama **Delcole** y el paquete base es `com.kiosco.Delcole`. La estructura se implementa dentro de ese paquete.

```
com.kiosco.Delcole
│
├── controller
│   ├── ProductoController
│   ├── StockController
│   └── (VentaController en fase carrito)
│
├── service
│   ├── ProductoService
│   ├── StockService
│
├── repository
│   ├── ProductoRepository
│   ├── MovimientoStockRepository
│
├── model
│   ├── Producto
│   ├── MovimientoStock
│   └── (enums: TipoMovimiento)
│
├── dto (opcional, recomendado para APIs)
│
├── config
│
└── DelcoleApplication
```

---

## 🗄️ Modelo de datos (fase inicial)

### Producto
| Campo          | Tipo     | Descripción |
|----------------|----------|-------------|
| id             | Long PK  |             |
| codigoBarra    | String   | único (puede ser null si no tiene código) |
| nombre         | String   |             |
| precioCompra   | BigDecimal |         |
| precioVenta    | BigDecimal |         |
| stockActual    | Integer  |             |
| stockMinimo    | Integer  | para alertas |
| activo         | boolean  | baja lógica |
| fechaAlta      | LocalDateTime | *(recomendado)* auditoría |
| fechaModificacion | LocalDateTime | *(recomendado)* auditoría |

### MovimientoStock (fase 2 – recomendado)
| Campo          | Descripción |
|----------------|-------------|
| id             | PK |
| producto       | relación @ManyToOne |
| tipoMovimiento | ENUM: ENTRADA / SALIDA / VENTA / AJUSTE |
| cantidad       | Integer (positivo; en SALIDA/VENTA se descontará) |
| precioUnitario | BigDecimal *(recomendado)* para historial de ventas |
| fechaHora      | LocalDateTime |
| observacion    | String opcional (ajustes, devoluciones) |

---

## 🔄 Flujo funcional principal

### 1️⃣ Escaneo de producto
- El usuario abre la app desde el celular
- Presiona **“Escanear producto”**
- Se activa la cámara
- Se lee el código de barras
- **Resultado:**
  - Si el producto **existe** → se muestra el detalle
  - Si **no existe** → formulario rápido de alta

### 2️⃣ Alta / edición de producto
Cargar o modificar:
- Nombre
- Precio de compra
- Precio de venta
- Stock inicial / ajuste
- Stock mínimo

### 3️⃣ Gestión de stock
Acciones disponibles:
- ➕ Agregar stock
- ➖ Registrar venta
- ❌ Dar de baja producto
- ⚠️ Visualizar alertas de stock bajo

### 4️⃣ Venta con varios productos (carrito / cobro)
- Escanear o agregar varios ítems a una “venta en curso”.
- Ver listado de ítems y **total a pagar**.
- **Confirmar venta** → descontar stock de todos los productos en una sola operación.
- Validar que no se venda más de lo que hay en stock antes de confirmar.

---

## 🔌 API / Backend para el frontend

Para que el escaneo y el carrito funcionen sin recargar toda la página, conviene exponer **endpoints REST (JSON)** además de las vistas Thymeleaf:

| Uso | Método | Ejemplo |
|-----|--------|---------|
| Buscar producto por código | GET | `/api/productos/codigo/{codigoBarra}` |
| Actualizar stock | PATCH/PUT | `/api/productos/{id}/stock` |
| Listar productos (búsqueda) | GET | `/api/productos?nombre=...` |
| Confirmar venta (carrito) | POST | `/api/ventas` (body: lista de ítems) |

Las pantallas pueden ser Thymeleaf y el JavaScript llamar a estos endpoints con `fetch` para mostrar resultados al instante.

---

## 📱 Diseño UX (clave para kiosco)

- **Mobile first**
- **Botones grandes**
- **Pocos pasos**
- **Pensado para uso con una mano**
- Pantallas simples:
  - Escanear
  - Producto
  - Vender
  - Agregar stock

---

## 🧪 MVP – Versión 1 (lo mínimo viable)

| Incluido | No incluido (fase posterior) |
|----------|------------------------------|
| ✔ Escanear código de barras | ❌ Reportes |
| ✔ Alta de producto si no existe | ❌ Usuarios |
| ✔ Ver producto y stock | ❌ Offline |
| ✔ Sumar / restar stock | |
| ✔ Listado simple de productos | |
| ✔ Flujo de venta con total (varios productos) | |
| ✔ Validar stock antes de vender | |

---

## 🚀 Evolución futura

### Fase 2
- Historial de movimientos
- Reporte diario de ventas
- Login básico
- Alertas de stock bajo

### Fase 3 (PWA)
- `manifest.json`
- Service worker
- Instalación en el celular
- Cache de recursos
- Funcionamiento offline parcial

---

## 📌 Decisiones clave del proyecto

- Comenzar como **Web App**
- **No** desarrollar app Android nativa
- Evolucionar a **PWA** sin reescritura
- Priorizar **simplicidad** y **usabilidad real**

---

## 🧠 Nota para IA / documentación

Este proyecto está pensado para crecer por etapas.

Toda nueva funcionalidad debe:
- Mantener compatibilidad **mobile**
- No romper el **flujo de escaneo**
- Evitar **complejidad innecesaria**

---

## 🔧 Revisión técnica y mejoras sugeridas

*(Añadido tras revisar el plan con el proyecto inicial Delcole.)*

### 1. Alineación con el proyecto real
- **Nombre del proyecto:** Delcole (no KioscoStock).
- **Paquete base:** `com.kiosco.Delcole` — toda la estructura (controller, service, repository, model) va dentro de este paquete.
- **Clase principal:** `DelcoleApplication`.

### 2. Modelo de datos
- **Producto:** Usar `BigDecimal` para precios (no `double`) para evitar errores de redondeo en dinero.
- **Producto:** `codigoBarra` puede ser opcional (nullable) para productos sin código (ej. golosinas sueltas); en ese caso el alta puede ser manual por nombre.
- **Producto:** Campos `fechaAlta` y `fechaModificacion` ayudan a auditoría y a reportes futuros sin complicar el MVP.
- **MovimientoStock:** Incluir `precioUnitario` desde el diseño para que en Fase 2 el historial de ventas tenga a qué precio se vendió cada ítem.

### 3. Flujo de venta (carrito)
- El objetivo “sacar cuenta de cuanto se debe pagar” y “varios productos” implica un **carrito**: escanear varios → ver total → confirmar → descontar todo. Este flujo se añadió explícitamente al plan (punto 4 del flujo funcional y en el MVP).
- **Validación:** No permitir confirmar venta si algún ítem supera el stock actual; mostrar mensaje claro (“No hay stock suficiente de X”).

### 4. API para el frontend
- Thymeleaf sirve para las pantallas; el escaneo y el carrito suelen requerir respuestas rápidas sin recargar la página. Por eso se recomienda exponer **endpoints REST (JSON)** para:
  - Buscar producto por código.
  - Actualizar stock.
  - Confirmar venta (enviando lista de ítems).
- El frontend puede usar `fetch()` desde JavaScript; opcionalmente usar **fragmentos Thymeleaf** para reemplazar solo una parte de la página.

### 5. Base de datos y perfiles – ¿H2 o MySQL? ¿Persisten al cerrar?

| Motor | Cómo se usa | ¿Persiste si cerramos la app? |
|-------|-------------|-------------------------------|
| **H2 en memoria** | `jdbc:h2:mem:...` | **No.** Al cerrar la app se pierde todo. Sirve para pruebas rápidas. |
| **H2 en archivo** | `jdbc:h2:file:./data/kiosco` | **Sí.** Los datos quedan en un archivo en disco. Al abrir de nuevo, se lee ese archivo. |
| **MySQL** | Servidor aparte (local o en la nube) | **Sí.** Los datos viven en el servidor MySQL. La app solo se conecta; al cerrar la app, MySQL sigue con los datos. |

**Resumen:**
- **Desarrollo en tu PC:** Podés usar **H2 en archivo** (persiste, no tenés que instalar MySQL) o **MySQL** si ya lo tenés. Si usás H2 en memoria, no persiste.
- **Producción (kiosco real / servidor):** Conviene **MySQL** (más robusto, backups, varios usuarios). Los datos persisten en el servidor.
- **Para que persista siempre:** No usar H2 en memoria; usar H2 en archivo o MySQL.

**Este proyecto:** Se usa **MySQL desde el inicio** (Local instance MySQL82, localhost:3306). El esquema se llama **`delcole`**; la app lo crea si no existe (`createDatabaseIfNotExist=true`). Podés abrir MySQL Workbench en cualquier momento, conectar a *Local instance MySQL82* y revisar en *Schemas* la base **delcole** para ver qué tablas y datos persisten.

### 6. Migraciones de esquema
- Recomendado: **Flyway** (o Liquibase) desde el inicio. Permite versionar cambios de tablas y desplegar en cualquier entorno sin errores de “tabla ya existe” o “columna faltante”. Se puede añadir al `pom.xml` en la fase de entidades + BD.

### 7. Testing
- El proyecto usa dependencias de test que no son las estándar de Spring Boot. Lo habitual es **`spring-boot-starter-test`** (JUnit 5 + Mockito). Conviene unificar a eso y escribir al menos tests para `ProductoService` y `StockService` (búsqueda por código, sumar/restar stock, validación de stock negativo).

### 8. Manejo de errores y UX
- Definir qué verá el usuario cuando:
  - El código escaneado **no existe** → formulario de alta (ya está en el plan).
  - El código es **inválido** o la cámara falla → mensaje claro “No se pudo leer el código, intentá de nuevo”.
  - **Sin stock** al vender → “Stock insuficiente de [producto]. Actual: X.”
  - Error de **red/servidor** → mensaje genérico y opción de reintentar.
- Evitar pantallas de error técnicas (stack trace) al usuario final.

### 9. Códigos de barras
- MVP: un solo código por producto está bien. Si más adelante se necesitan varios códigos para el mismo producto (distintos formatos o presentaciones), se puede agregar una tabla “CódigoBarra” asociada a Producto; no es necesario para la versión 1.

### 10. Resumen de cambios aplicados al plan
- Estructura de paquetes actualizada a `com.kiosco.Delcole` y `DelcoleApplication`.
- Modelo Producto: tipos sugeridos (BigDecimal, LocalDateTime), codigoBarra opcional, campos de auditoría recomendados.
- Modelo MovimientoStock: precioUnitario y observación recomendados.
- Nuevo flujo 4: venta con varios productos (carrito) y validación de stock.
- Nueva sección: API/endpoints recomendados para el frontend.
- MVP ampliado: flujo de venta con total y validación de stock.
- Esta sección de revisión técnica como referencia para desarrollo e IA.

---

*Documento base creado para el proyecto Kiosco Stock – Web App. Actualizado con revisión técnica y alineación al proyecto Delcole.*
