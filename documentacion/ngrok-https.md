# Usar ngrok para HTTPS (cámara en el celular)

Para que la cámara del celular funcione en la pantalla **Vender**, la app debe servirse por **HTTPS**. En local (`http://localhost:8080`) la cámara funciona en la PC, pero desde el celular (`http://192.168.0.44:8080`) muchos navegadores la bloquean. Con ngrok exponés tu `localhost` por una URL pública HTTPS.

---

## 1. Instalar ngrok en Windows

### Opción A – Winget (recomendado)

Abrí PowerShell o CMD y ejecutá:

```powershell
winget install ngrok.ngrok
```

### Opción B – Microsoft Store

Buscá **ngrok** en Microsoft Store e instalalo.

### Opción C – Descarga manual

1. Entrá a [https://ngrok.com/download](https://ngrok.com/download).
2. Descargá la versión **Windows (64-bit)**.
3. Descomprimí el ZIP y copiá **solo** `ngrok.exe` en la carpeta del proyecto **`tools`**:
   - Ruta: `Delcole\tools\ngrok.exe`
   - En esa carpeta hay un **README** con instrucciones para agregar `tools` al PATH y poder usar `ngrok` desde cualquier terminal.

---

## 2. Cuenta y authtoken (gratis)

1. Creá una cuenta en [https://ngrok.com](https://ngrok.com) (gratis).
2. En el dashboard copiá tu **authtoken**.
3. En PowerShell o CMD ejecutá (reemplazá `TU_AUTHTOKEN` por el token):

```powershell
ngrok config add-authtoken TU_AUTHTOKEN
```

Solo hace falta hacerlo una vez.

---

## 3. Levantar la app y ngrok

1. **Arrancá tu app Delcole** en el puerto 8080:
   ```powershell
   cd C:\Users\LUCERO-PC\Desktop\APP\Delcole
   mvn spring-boot:run
   ```

2. **En otra terminal**, ejecutá ngrok apuntando al puerto 8080 **con reescritura del Host** (necesario para que Spring Boot acepte las peticiones):
   ```powershell
   ngrok http --host-header=localhost:8080 8080
   ```
   Sin `--host-header=localhost:8080`, Spring Boot puede rechazar el acceso con "Invalid hostname" o "no se puede acceder al sitio" desde el celular.

3. En la consola de ngrok vas a ver algo como:
   ```
   Forwarding   https://abc123.ngrok-free.app -> http://localhost:8080
   ```

4. **En el celular** (o en cualquier navegador), entrá a esa URL HTTPS (ej: `https://abc123.ngrok-free.app/vender`) y probá **Escanear con cámara**. La cámara debería habilitarse porque la página es HTTPS.

---

## 4. Carpeta `tools` y PATH

En el proyecto existe la carpeta **`tools`**. Conviene usarla así:

- **Poné** `ngrok.exe` dentro de **`Delcole\tools`** (descomprimí el ZIP de ngrok y copiá solo el .exe ahí).
- **Agregá esa carpeta al PATH** de Windows para poder ejecutar `ngrok` desde cualquier terminal:
  1. Ruta completa: `C:\Users\LUCERO-PC\Desktop\APP\Delcole\tools` (ajustá si tu proyecto está en otra ruta).
  2. Windows → buscar **"Variables de entorno"** → **Variables de entorno** → en **Path** (usuario) → **Editar** → **Nuevo** → pegar la ruta de `tools` → Aceptar.
  3. Cerrar y reabrir la terminal; probar con `ngrok version`.

Así `ngrok` queda en un solo lugar y podés usarlo desde cualquier carpeta. En **`tools\README.md`** están los mismos pasos.

---

## 5. Resumen de comandos

| Paso | Comando |
|------|---------|
| Instalar (Winget) | `winget install ngrok.ngrok` |
| Configurar token | `ngrok config add-authtoken TU_AUTHTOKEN` |
| Exponer puerto 8080 (con Host correcto para Spring Boot) | `ngrok http --host-header=localhost:8080 8080` |

La URL HTTPS que muestra ngrok cambia en la versión gratuita cada vez que reiniciás ngrok. Si necesitás una URL fija, ngrok ofrece planes de pago.

---

## 6. Seguridad

- No compartas tu authtoken.
- ngrok hace público tu `localhost` mientras esté corriendo; usalo para desarrollo/pruebas, no dejés la app expuesta sin necesidad.
