# Plan de continuación – Delcole

Después del primer commit. Desarrollo en paralelo backend + frontend.

---

## Nombres de las 4 áreas (pantalla principal)

| Botón | Ruta principal | Descripción breve |
|-------|----------------|-------------------|
| **Vender** | `/vender` | Carrito: escanear → total → cobrar → descuenta stock |
| **Stock** | `/stock` | Productos: crear, editar, eliminar, agregar stock |
| **Cierre de caja** | `/caja` | Resumen del día, control de precios |
| **Finanzas** | `/finanzas` | Gastos del local, ganancias por período, qué comprar |

---

## Fase 1 – Pantalla principal (4 botones)

**Objetivo:** El index muestra solo 4 botones grandes que ocupan bien la pantalla del celular.

### Backend
- [ ] Ajustar `HomeController`: que `/` y `/home` sirvan la nueva **index** (4 botones), sin listado de productos.
- [ ] Opcional: redirigir `/escanear` a `/vender` cuando exista la pantalla de venta.

### Frontend
- [ ] Crear o reemplazar `home.html` por una vista **mobile-first** con:
  - 4 botones grandes (grid/flex), mismo tamaño, textos:
    - **Vender** → `/vender`
    - **Stock** → `/stock`
    - **Cierre de caja** → `/caja`
    - **Finanzas** → `/finanzas`
  - Navbar simple: "Delcole" o "Kiosco Delcole".
- [ ] Reutilizar `layout.html` si ya existe; si no, definir estilos comunes (Bootstrap, botones grandes).

**Entregable:** Entrar a la app y ver solo los 4 botones. Las rutas `/stock`, `/caja`, `/finanzas` pueden devolver "En construcción" o una página mínima.

---

## Fase 2 – Vender (carrito)

**Objetivo:** Flujo completo: escanear → ítems en carrito → total en vivo → Cobrar → descuenta stock.

### Backend
- [ ] Modelo **Venta** (opcional para MVP: se puede solo descontar stock y no guardar cabecera de venta; si se quiere historial, crear `Venta` + `VentaItem`).
- [ ] **API:** `POST /api/ventas` — body: lista de ítems `[{ "productoId": 1, "cantidad": 2 }, ...]`. Validar stock, descontar todos, devolver total y/o resumen.
- [ ] Reutilizar: `GET /api/productos/codigo/{codigoBarra}` (ya existe), `ProductoService.restarStock` (ya existe); en el controller de ventas iterar y llamar a restar o un método `procesarVenta(List<VentaItem>)`.

### Frontend
- [ ] Vista **`vender.html`** (ruta `/vender`):
  - Botón grande **Escanear**: abre cámara, usa QuaggaJS o ZXing, al leer código llama a `GET /api/productos/codigo/{codigo}` y si existe agrega 1 unidad al carrito en memoria (JS); si no existe, redirigir a alta de producto con código prefilled o modal.
  - Lista de ítems en el carrito (nombre, cantidad, precio unitario, subtotal).
  - **Total** actualizado al instante (suma de precioVenta × cantidad).
  - Botón **Cobrar**: envía `POST /api/ventas` con el carrito; si OK, vaciar carrito y mostrar "Venta registrada. Total: $X"; si error (ej. stock insuficiente), mostrar mensaje sin vaciar carrito.
- [ ] Manejo de errores: código no encontrado, stock insuficiente, cámara no disponible.

**Entregable:** Poder escanear varios productos, ver el total y al cobrar descontar stock correctamente.

---

## Fase 3 – Stock (reorganizar)

**Objetivo:** Toda la gestión de productos bajo el botón **Stock**: listado, crear, editar, eliminar, agregar stock.

### Backend
- [ ] Opcional: agrupar rutas web bajo `/stock` (ej. `/stock`, `/stock/nuevo`, `/stock/{id}`) usando un `StockWebController` o renombrar `ProductoWebController` y mapear a `/stock`. Si se prefiere no tocar mucho, dejar `/productos` y que el botón **Stock** apunte a `/productos` o a una nueva página `/stock` que sea un "hub" con listado + link "Crear producto".
- [ ] API ya está; no obligatorio cambiar URLs.

### Frontend
- [ ] **Stock como hub:** Si se crea ruta `/stock`, página que muestre listado de productos (reutilizando lógica de listado) + botón "Crear producto" → `/productos/nuevo`. Cada fila con "Ver / Editar", "Agregar stock", "Eliminar".
- [ ] Ajustar links del home: el botón **Stock** debe llevar a este hub (`/stock` o `/productos`).
- [ ] Asegurar que en detalle/producto haya acciones claras: agregar stock, editar, dar de baja.

**Entregable:** Desde el botón Stock del index se accede al listado y a todas las acciones de productos.

---

## Fase 4 – Cierre de caja

**Objetivo:** Pantalla para ver resumen del día (ventas, total) y control de precios.

### Backend
- [ ] Si no existe entidad **Venta**, para "ventas del día" se puede en una primera versión usar solo movimientos de stock tipo VENTA (requiere modelo MovimientoStock + TipoMovimiento) o una tabla **Venta** que se alimente al confirmar en el carrito (Fase 2). Decisión: si en Fase 2 se guardó Venta, aquí se consulta; si no, implementar guardado de Venta en Fase 2 y luego aquí.
- [ ] **API:** `GET /api/caja/resumen?fecha=YYYY-MM-DD` (o "hoy" por defecto): total vendido, cantidad de operaciones. Opcional: `GET /api/caja/ventas?desde=&hasta=`.
- [ ] Control de precios: listado de productos con precios (reutilizar `GET /api/productos` o vista Thymeleaf).

### Frontend
- [ ] Vista **`caja.html`** en `/caja`:
  - Resumen del día: "Total vendido hoy: $X", "Cantidad de ventas: N".
  - Selector de fecha (opcional) para ver otro día.
  - Link o sección "Control de precios" → listado de productos con precio de venta (y opcional edición).
- [ ] Botón "Cerrar caja" opcional (puede ser solo informativo al principio).

**Entregable:** Pantalla Cierre de caja con total del día y acceso a precios.

---

## Fase 5 – Finanzas

**Objetivo:** Gastos del local (luz, insumos, canon), ganancias por fecha/mes, sugerencia de qué comprar.

### Backend
- [ ] Modelo **Gasto**: id, fecha, concepto (String), monto (BigDecimal), tipo (enum: INSUMO, SERVICIO, CANON, OTRO) u otro criterio; opcional categoría.
- [ ] Repository y Service de Gastos (CRUD).
- [ ] **API:**
  - `GET /api/gastos?desde=&hasta=` — listar gastos en período.
  - `POST /api/gastos` — crear gasto.
  - `GET /api/finanzas/resumen?desde=&hasta=` — ventas del período − gastos del período = ganancia (parcial/neta). Necesita ventas por período (tabla Venta o MovimientoStock).
  - `GET /api/finanzas/comprar-sugerido` — productos con stock bajo (stockActual < stockMinimo) o más vendidos; según regla de negocio.
- [ ] Ajustar según si ya existe Venta y MovimientoStock.

### Frontend
- [ ] Vista **`finanzas.html`** en `/finanzas`:
  - Filtro por período (fecha desde/hasta o mes).
  - Listado de gastos del período con opción de alta (formulario o modal).
  - Resumen: "Ventas: $X", "Gastos: $Y", "Ganancia neta: $Z".
  - Sección "Qué comprar": lista de productos con stock bajo o sugeridos.
- [ ] Formulario para cargar gasto (concepto, monto, fecha, tipo).

**Entregable:** Poder cargar gastos, ver ganancia por período y lista de productos a reponer.

---

## Orden sugerido de trabajo

1. **Fase 1** (pantalla principal) — poco tiempo, deja claro el mapa de la app.
2. **Fase 2** (Vender) — flujo crítico; definir si se guarda Venta para no rehacer después.
3. **Fase 3** (Stock) — reorganizar entradas y rutas.
4. **Fase 4** (Cierre de caja) — depende de tener ventas registradas (Fase 2).
5. **Fase 5** (Finanzas) — depende de Gastos y de ventas por período.

Dentro de cada fase se puede avanzar backend y frontend en paralelo (por ejemplo: un día API ventas, otro día pantalla vender).

---

## Resumen de archivos/clases a crear o tocar

| Fase | Backend | Frontend |
|------|---------|----------|
| 1 | `HomeController` (ajustar) | `home.html` (4 botones) |
| 2 | `VentaController` (API), opcional `Venta`/`VentaItem` | `vender.html`, JS escaneo + carrito |
| 3 | Opcional `StockWebController` o rutas bajo `/stock` | `stock.html` (hub) o reutilizar listado en `/productos` |
| 4 | `CajaController`, consultas ventas del día | `caja.html` |
| 5 | `Gasto` (model), `GastoRepository`, `GastoService`, `FinanzasController` / `GastoController` | `finanzas.html` |

---

## Notas

- **MovimientoStock:** El plan original lo tiene para Fase 2 del documento base; se puede introducir cuando se implemente el guardado de ventas (cada ítem vendido = movimiento tipo VENTA) para tener historial y reportes.
- **Páginas "En construcción":** Si se hace Fase 1 primero, `/caja` y `/finanzas` pueden apuntar a una misma plantilla `en-construccion.html` hasta sus respectivas fases.
- **Nombres:** Se usan **Vender**, **Stock**, **Cierre de caja**, **Finanzas** en toda la app (títulos, navbar, rutas).

---

## Resumen de lo implementado (changelog)

**Estado:** Las 5 fases del plan están implementadas. La app Delcole tiene las 4 áreas funcionales.

### Fase 1 – Pantalla principal
- [x] `HomeController`: `/` y `/home` sirven index con 4 botones; rutas `/vender`, `/stock`, `/caja`, `/finanzas`.
- [x] `home.html`: 4 botones grandes (Vender, Stock, Cierre de caja, Finanzas), mobile-first.
- [x] Páginas básicas: `vender.html`, `stock.html`, `caja.html`, `finanzas.html`.

### Fase 2 – Vender
- [x] Entidades **Venta** y **VentaItem**; persistencia al cobrar.
- [x] `VentaController`: `POST /api/ventas` (valida stock, descuenta, guarda venta).
- [x] `vender.html`: búsqueda por código de barras o por nombre; carrito en memoria; total en vivo; botón Cobrar; botón "Escanear con cámara" (Quagga2; requiere HTTPS en producción).
- [x] DTOs para evitar referencias circulares en APIs.

### Fase 3 – Stock
- [x] Hub `/stock` con links a "Crear producto" y "Ver todos los productos".
- [x] `ProductoWebController`: `GET /productos` (listado), `/productos/nuevo`, `/productos/{id}`.
- [x] `producto-listado.html`; navegación consistente (volver a Stock/Productos).

### Fase 4 – Cierre de caja
- [x] `CajaController`: `GET /api/caja/resumen?fecha=` (total vendido, cantidad de ventas); `GET /api/caja/ventas?fecha=` (lista de ventas del día en DTO).
- [x] `caja.html`: selector de fecha, resumen del día, botón "Ver detalle" con lista de ventas (cada venta con productos uno debajo del otro, precio unitario y subtotal, fecha/hora formateada), link a Control de precios (`/productos`).

### Fase 5 – Finanzas
- [x] Entidad **Gasto** (fecha, concepto, monto, tipo: INSUMO, SERVICIO, CANON, OTRO); enum **TipoGasto**.
- [x] `GastoRepository`, `GastoService`; `GastoController`: `GET/POST/DELETE /api/gastos` (listar por período, crear, eliminar).
- [x] `FinanzasController`: `GET /api/finanzas/resumen?desde=&hasta=` (ventas del período, gastos del período, ganancia neta). Si no se pasan fechas, se usa el mes actual.
- [x] `VentaService.sumarVentasEntre(desde, hasta)` para total de ventas en rango de fechas.
- [x] `finanzas.html`: filtros desde/hasta, botón "Mes actual"; resumen (Ventas, Gastos, Ganancia neta); formulario para agregar gasto; listado de gastos del período con opción de eliminar.

### Archivos y clases creados/modificados

| Tipo | Archivos |
|------|----------|
| Model | `Venta`, `VentaItem`, `Gasto`, `TipoGasto` (enum) |
| DTO | `VentaDetalleDto` (con `VentaItemDto`) |
| Repository | `VentaRepository`, `GastoRepository` |
| Service | `VentaService`, `GastoService` |
| Controller | `VentaController`, `CajaController`, `GastoController`, `FinanzasController`; `HomeController`, `ProductoWebController` (ajustes) |
| Templates | `home.html`, `vender.html`, `stock.html`, `caja.html`, `finanzas.html`, `producto-listado.html`; ajustes en `producto-detalle.html` |

### Pendiente / mejoras futuras
- Cámara en celular: requiere HTTPS (ngrok o similar) para que funcione fuera de localhost.
- Sección "Qué comprar" en Finanzas (productos con stock bajo).
- Login / usuarios (fase posterior del plan original).

---

## Sugerencia de commit y push

**Mensaje de commit sugerido:**

```
feat: implementar las 4 áreas (Vender, Stock, Cierre de caja, Finanzas)

- Pantalla principal con 4 botones grandes (mobile-first).
- Vender: carrito, búsqueda por código o nombre, escaneo con cámara (Quagga2), cobrar y persistir ventas.
- Stock: hub con listado y alta/edición de productos.
- Cierre de caja: resumen del día y detalle de ventas con productos y totales.
- Finanzas: gastos por período, resumen ventas/gastos/ganancia neta, alta y listado de gastos.
- Entidades: Venta, VentaItem, Gasto, TipoGasto; DTOs para APIs.
- Documentación: plan-continuacion.md actualizado con changelog.
```

**Comandos (en la raíz del proyecto):**

```bash
git add .
git status
git commit -m "feat: implementar las 4 áreas (Vender, Stock, Cierre de caja, Finanzas)"
git push origin main
```

Si la rama remota tiene otro nombre (por ejemplo `master`), reemplazá `main` por ese nombre.
