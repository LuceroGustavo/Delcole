# Estado actual del proyecto y resumen de cambios

Documento que describe el estado actual de la aplicación **Kiosco Delcole** y los cambios implementados hasta la fecha.

---

## 1. Módulos principales (pantalla de inicio)

La aplicación tiene **4 módulos** accesibles desde el inicio:

| Módulo | Ruta | Descripción |
|--------|------|-------------|
| **Vender** | `/vender` | Carrito de ventas, escaneo por código de barras o búsqueda por nombre, cobro y descuento de stock. |
| **Stock** | `/stock` | Hub para crear producto o ver listado. Desde el listado se gestionan productos (crear, editar, ver, filtrar). |
| **Cierre de caja** | `/caja` | Resumen del día (total vendido, cantidad de ventas) y detalle de ventas por fecha. |
| **Finanzas** | `/finanzas` | Resumen por período (ventas, gastos, ganancia neta), alta de gastos y listado de gastos. |

---

## 2. Pantalla de inicio (home / index)

- **Diseño inspirado en referencia Stitch** (`referencias/code.html`, `referencias/screen.png`).
- **Header:** logo circular (imagen `logo sin fondo.png`), título "Del Cole", subtítulo "Gestión de Kiosco", botones de notificaciones y ajustes (placeholders).
- **Grid 2×2:** cuatro botones que ocupan la pantalla (Vender, Stock, Caja, Finanzas) con gradientes y iconos Material Symbols (shopping_cart, inventory_2, payments, query_stats).
- **Barra inferior:** navegación con Inicio, Vender, Stock, Caja, Finanzas; "Inicio" resaltado en teal.
- **Estilos:** fuente Manrope, color primario #13ecda, fondo #f6f8f8, Material Symbols Outlined.
- **Responsive:** pensado para móvil; en pantallas más grandes el grid se centra con ancho máximo.

---

## 3. Navegación y layout

- **Navbar minimalista** (fragmento `fragments/navbar.html`): logo + enlaces Inicio, Vender, Stock, Caja, Finanzas. Usado en Stock, Vender, Caja, Finanzas, listado de productos, formulario y detalle de producto.
- **CSS global** (`static/css/app.css`): variables de color, estilos de navbar, cards con bordes redondeados y sombras suaves, formularios y listas unificados.
- **Logo:** `static/img/logo sin fondo.png` (referenciado en navbar y en home).

---

## 4. Productos y stock

### Entidad Producto
- Campos: id, codigoBarra, nombre, marca, rubro, precioCompra, precioVenta, stockActual, stockMinimo, activo, fechaAlta, fechaModificacion.
- Método `isStockBajo()`: `stockActual < stockMinimo`.

### Formulario de producto (nuevo / editar)
- Campos: código de barras (con botón **Escanear** y Quagga2), nombre, **Marca**, **Rubro**, costo, precio de venta, stock, stock mínimo.
- **Marca y Rubro:** menús desplegables cargados desde la API (`/api/productos/marcas`, `/api/productos/rubros`), con opción "Nueva marca..." / "Nuevo rubro..." para escribir un valor nuevo.

### Listado de productos
- **Búsqueda y filtros:** nombre, código de barras (con botón **Escanear**), filtro por Marca, filtro por Rubro, checkbox "Solo stock bajo".
- **Búsqueda solo por código:** si se ingresa solo el código y se busca, redirige al detalle del producto si existe, o muestra "Código no encontrado".
- Enlaces "← Stock" y "← Volver a productos" donde corresponde.

### API de productos
- `GET /api/productos` – listar (opcional: ?nombre=).
- `GET /api/productos/marcas` – marcas distintas.
- `GET /api/productos/rubros` – rubros distintos.
- `GET /api/productos/codigo/{codigoBarra}` – por código de barras.
- `GET /api/productos/{id}` – por ID.
- `POST /api/productos` – crear/actualizar.
- `POST /api/productos/{id}/stock/sumar`, `.../restar`, `.../baja`.

### Backend filtros
- `ProductoRepository.findConFiltros(nombre, marca, rubro, soloStockBajo)` con `@Query` JPQL.
- `ProductoService.listarConFiltros(...)` y `ProductoWebController.listar(...)` con parámetros de búsqueda.

---

## 5. Vender (carrito)

- Entrada por código de barras (manual o escáner Quagga2) o por nombre (búsqueda).
- Carrito en memoria en el front; al cobrar se envía `POST /api/ventas` con items y se descuenta stock.
- DTOs para evitar referencias circulares en JSON (VentaDetalleDto, VentaItemDto).

---

## 6. Cierre de caja

- `GET /api/caja/resumen?fecha=` – total vendido y cantidad de ventas.
- `GET /api/caja/ventas?fecha=` – lista de ventas del día con detalle.
- Vista `caja.html`: selector de fecha, resumen y listado desplegable de ventas.

---

## 7. Finanzas

- Entidad **Gasto** (fecha, concepto, monto, tipo: INSUMO, SERVICIO, CANON, OTRO).
- `GET /api/finanzas/resumen?desde=&hasta=` – ventas, gastos, ganancia neta.
- `GET/POST /api/gastos` – CRUD de gastos.
- Vista `finanzas.html`: filtros por período, resumen, formulario de gastos, listado.

---

## 8. Escaneo de códigos de barras

- **Quagga2** (`@ericblade/quagga2`) en:
  - Formulario de producto (código de barras).
  - Listado de productos (campo código de barras + botón Escanear).
  - Pantalla Vender (código o nombre + botón escanear).
- Requiere HTTPS o localhost para acceso a cámara.
- Documentación adicional: `documentacion/escanear-codigos-barras.md`, `documentacion/ngrok-https.md`.

---

## 9. Archivos y estructura relevante

```
src/main/resources/
├── static/
│   ├── css/app.css          # Estilos globales y navbar
│   └── img/
│       ├── logo sin fondo.png
│       └── logo.png
├── templates/
│   ├── fragments/navbar.html  # Navbar con logo y enlaces
│   ├── home.html              # Inicio (diseño referencia Stitch)
│   ├── stock.html
│   ├── vender.html
│   ├── caja.html
│   ├── finanzas.html
│   ├── producto-listado.html
│   ├── producto-form.html
│   └── producto-detalle.html
documentacion/
├── ESTADO-ACTUAL-Y-CAMBIOS.md  # Este archivo
├── plan-desarrollo-kiosco.md
├── plan-offline-y-migracion.md
├── escanear-codigos-barras.md
└── ngrok-https.md
referencias/
├── code.html   # Referencia HTML generada en Stitch
└── screen.png  # Captura del diseño
```

---

## 10. Cómo ejecutar

- Base de datos MySQL configurada en `application.properties` (o `application-local.properties`).
- `./mvnw spring-boot:run` (o `mvnw.cmd` en Windows).
- Aplicación en `http://localhost:8080`. Desde otro dispositivo en la red: `http://<IP>:8080` o túnel (ngrok) con HTTPS.

---

## 11. Próximos pasos sugeridos (según plan)

- Seguridad / login (Spring Security).
- PWA y modo offline (plan en `plan-offline-y-migracion.md`).
- Posible página de “Ajustes” y “Notificaciones” (hoy placeholders en el home).

---

*Última actualización: según implementación hasta integración del diseño de inicio inspirado en Stitch, navbar global, filtros y menús desplegables en productos, y documentación del estado actual.*
