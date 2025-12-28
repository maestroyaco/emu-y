import os

def corregir_codificacion_forzado(directorio_base):
    # Extensiones de archivo
    extensiones = ('.java', '.txt', '.properties')
    archivos_modificados = 0

    # Normalizamos la ruta para evitar problemas de Windows/Linux
    directorio_base = os.path.abspath(directorio_base)
    print(f"--- Buscando archivos en: {directorio_base} ---")

    for raiz, dirs, archivos in os.walk(directorio_base):
        for nombre_archivo in archivos:
            if nombre_archivo.endswith(extensiones):
                ruta_completa = os.path.join(raiz, nombre_archivo)
                
                try:
                    # LEER: Forzamos la lectura en ISO-8859-1 (el estándar antiguo)
                    # Este formato acepta cualquier byte, por lo que leerá las eñes rotas.
                    with open(ruta_completa, 'r', encoding='iso-8859-1') as f:
                        contenido = f.read()

                    # ESCRIBIR: Guardamos en UTF-8
                    with open(ruta_completa, 'w', encoding='utf-8') as f:
                        f.write(contenido)
                    
                    print(f"[OK] Re-codificado: {nombre_archivo}")
                    archivos_modificados += 1

                except Exception as e:
                    print(f"[ERROR] No se pudo procesar {nombre_archivo}: {e}")

    print(f"\n--- Finalizado ---")
    print(f"Total de archivos procesados y guardados en UTF-8: {archivos_modificados}")

if __name__ == "__main__":
    # Intentamos detectar la carpeta src
    ruta_objetivo = './src'
    
    if not os.path.exists(ruta_objetivo):
        # Si no la encuentra, intenta en el directorio actual .
        ruta_objetivo = '.'
        print("Aviso: Carpeta ./src no encontrada, procesando directorio actual.")
    
    corregir_codificacion_forzado(ruta_objetivo)