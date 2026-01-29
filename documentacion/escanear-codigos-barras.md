# Escaneo de códigos de barras en la app

La app usa **Quagga2** en el navegador para leer códigos de barras (EAN, UPC, Code 128, etc.). En celulares el rendimiento puede ser menor que el de apps nativas; esta guía resume cómo está afinado y qué alternativas tenés.

---

## Mejoras aplicadas en la app

- **Resolución de cámara:** Se pide 1280×720 (ideal) para mejorar la lectura en móviles.
- **Frecuencia de decodificación:** `frequency: 20` para intentar leer más seguido.
- **Varios workers:** Se usan hasta 4 hilos (`numOfWorkers`) si el dispositivo lo permite.
- **Tip en pantalla:** En Vender hay un enlace a una app de escaneo (ej. [Barcode Scanner en Play Store](https://play.google.com/store/apps/details?id=com.mobileappsshop.barcode)) con la sugerencia de escanear ahí, copiar el código y pegarlo en el campo.

---

## Si el escáner de la web no lee bien

1. **Usar una app de escaneo en el celular**  
   Instalá una app de códigos de barras (por ejemplo “Barcode Scanner - Price Finder” u otra que prefieras). Escanear el producto ahí, copiar el código (o compartir) y **pegarlo en el campo** de la web (Vender o en “Agregar producto”). Así evitás depender del escáner integrado.

2. **Buena luz y código enfocado**  
   Apoyá el código plano, sin reflejos, y acercá/alejá el celular hasta que se vea nítido. Algunos teléfonos con varias cámaras no enfocan bien de cerca; en esos casos suele funcionar mejor la app externa + pegar.

3. **HTTPS**  
   La cámara en el celular solo funciona si la página se abre por **HTTPS** (por ejemplo con ngrok) o por `http://localhost` en la PC.

---

## Referencias

- [Quagga2](https://github.com/ericblade/quagga2) – Librería usada en la app.
- [Comparativa de librerías (Strich vs ZXing vs Quagga)](https://strich.io/strich-compared-to-zxing-js-and-quagga.html) – Contexto sobre limitaciones de las opciones gratuitas en web.
- Apps nativas (p. ej. Barcode Scanner en Play Store) suelen usar el mismo tipo de APIs nativas que las apps de escaneo que probaste y por eso pueden sentirse más rápidas y estables; la opción “escanear en la app y pegar en la web” combina lo mejor de ambos.
