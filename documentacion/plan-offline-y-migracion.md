# Plan: App 100% offline y base local en el celular con migración a PC

Objetivo: que la app sea **productiva y eficaz**, y evolucione hacia **100% offline** en el celular con **base de datos local**, con posibilidad de **migrar/sincronizar** esa base con la app en PC.

---

## Arquitectura objetivo

| Lugar | Rol | Base de datos |
|-------|-----|----------------|
| **Celular** | Uso diario: vender, stock, caja, finanzas. Funciona **sin internet**. | Base local en el dispositivo (IndexedDB en PWA o SQLite si se pasa a híbrido). |
| **PC** | Administración, reportes, respaldo, posible segundo punto de venta. | MySQL (actual). |
| **Migración / sincronización** | Exportar desde el celular → importar en PC (o sincronización bidireccional). | Mismo esquema de datos; archivo de export (JSON/SQLite) o protocolo de sync. |

---

## Enfoque técnico propuesto

1. **PWA (Progressive Web App)**  
   - Instalable en el celular, funciona en el navegador.  
   - Service worker + caché para que la app cargue offline.  
   - Base local en el **navegador**: **IndexedDB** (o librería tipo Dexie.js / idb).  
   - No requiere reescribir todo: el frontend actual (Thymeleaf + JS) puede ir pasando a consumir una “capa local” cuando no haya red.

2. **Backend en PC**  
   - Sigue siendo Spring Boot + MySQL.  
   - Sirve para: respaldo, reportes, y como destino de la **migración** (export desde celular → import en PC).  
   - Opcional después: sincronización bidireccional (celular ↔ PC).

3. **Migración de la base**  
   - **Exportar desde el celular:** generar un archivo (JSON o SQLite) con productos, ventas, gastos, etc.  
   - **Importar en la PC:** en la app Spring Boot, pantalla o endpoint que lea ese archivo y cargue/actualice MySQL.  
   - Así la “base del celular” se puede migrar al “app en PC” cuando quieras.

---

## Fases sugeridas

### Fase 1 – Mejorar métodos y preparar datos para sync (ahora)
- Unificar y clarificar **APIs REST** (respuestas consistentes, DTOs, códigos HTTP).
- Agregar **marcas de tiempo** en entidades (`fechaAlta`, `fechaModificacion`, `version` o `updatedAt`) para poder después detectar cambios en una sync.
- Revisar **transacciones** y **validaciones** en servicios para que sean eficaces y reutilizables.
- Documentar contrato de los endpoints (qué reciben, qué devuelven) para la futura “capa local” en el celular.

### Fase 2 – PWA y base local en el celular
- `manifest.json` + Service Worker para que la app sea instalable y funcione offline (al menos la shell).
- Capa **IndexedDB** en el frontend: mismo esquema lógico (productos, ventas, gastos) que el backend.
- Lógica **offline-first**: leer/escribir primero en IndexedDB; cuando haya red, opción de enviar cambios al servidor (sync) o solo exportar.

### Fase 3 – Export / Import (migración celular → PC)
- En el celular: botón **“Exportar base”** → descarga un archivo (JSON o SQLite) con todos los datos locales.
- En la app en PC: pantalla **“Importar base del celular”** → sube ese archivo y el backend actualiza MySQL (merge por ID o por fecha, según reglas que definamos).
- Con esto la app es “100% offline” en el celular y la base del celular se puede migrar al app en PC.

### Fase 4 (opcional) – Sincronización bidireccional
- Definir protocolo de sync (última modificación, conflictos).
- Celular y PC pueden intercambiar cambios cuando haya conexión.

---

## Primeros pasos concretos (Fase 1)

Para que sea más **productivo y eficaz** y quede listo para offline y migración:

1. **Auditar entidades**  
   - Que Producto, Venta, VentaItem, Gasto tengan `fechaAlta` / `fechaModificacion` (o `updatedAt`) donde aplique.  
   - Opcional: campo `version` o `updatedAt` para sync futuro.

2. **Unificar respuestas de la API**  
   - Respuestas JSON con estructura similar (ej. `{ "data": ..., "ok": true }` o estándar que elijamos).  
   - Códigos HTTP y mensajes de error consistentes.

3. **Endpoints de export/import (esqueleto)**  
   - `GET /api/export/datos` → devuelve JSON con productos, ventas, gastos (para que el frontend pueda “guardar como archivo” y luego importar en PC).  
   - `POST /api/import/datos` → recibe ese JSON y actualiza/inserta en MySQL (reglas de merge a definir).  
   - Así definimos el “formato de migración” desde ya.

4. **Servicios**  
   - Revisar ProductoService, VentaService, GastoService: transacciones, validaciones, y métodos que puedan reutilizarse tanto para API como para un futuro import.

---

## Resumen

- **Objetivo:** app 100% offline en el celular, base local en el celular, migración de esa base al app en PC.
- **Enfoque:** PWA + IndexedDB en el celular; Spring Boot + MySQL en PC; export/import (y luego opcionalmente sync) entre ambos.
- **Siguiente paso práctico:** Fase 1 – mejorar métodos, agregar timestamps donde falten, y definir endpoints de export/import para la migración.

Cuando quieras, seguimos por la Fase 1 (por ejemplo: timestamps en entidades y diseño del `GET /api/export/datos` y `POST /api/import/datos`).
