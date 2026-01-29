# Carpeta de herramientas (ngrok, etc.)

Colocá aquí el ejecutable **ngrok.exe** después de descargarlo desde [ngrok.com/download](https://ngrok.com/download) (Windows 64-bit, descomprimí el ZIP y copiá solo `ngrok.exe`).

## Agregar esta carpeta al PATH (para usar `ngrok` desde cualquier terminal)

1. Copiá la ruta completa de esta carpeta:
   ```
   C:\Users\LUCERO-PC\Desktop\APP\Delcole\tools
   ```

2. En Windows:
   - Buscá **"Variables de entorno"** (o **"Editar las variables de entorno del sistema"**).
   - Clic en **"Variables de entorno"**.
   - En **Variables del usuario**, seleccioná **Path** → **Editar**.
   - **Nuevo** → pegá la ruta `C:\Users\LUCERO-PC\Desktop\APP\Delcole\tools`.
   - Aceptar en todas las ventanas.

3. Cerrá y volvé a abrir PowerShell o CMD. Probá:
   ```
   ngrok version
   ```

Si preferís una carpeta fuera del proyecto (por ejemplo `C:\Users\LUCERO-PC\bin`), creala manualmente, mové ahí `ngrok.exe` y agregá **esa** ruta al PATH.
