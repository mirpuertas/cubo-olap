package olapcube.metricas;

import java.util.HashMap;
import java.util.Map;

public class RegistroMedida {
    private static Map<String, Medida> medidas;
    private static final Map<Character, Character> ACCENT_MAP;

    static {
        medidas = new HashMap<>();
        ACCENT_MAP = new HashMap<>();

        // Inicializar el mapa de acentos
        ACCENT_MAP.put('á', 'a');
        ACCENT_MAP.put('é', 'e');
        ACCENT_MAP.put('í', 'i');
        ACCENT_MAP.put('ó', 'o');
        ACCENT_MAP.put('ú', 'u');
        ACCENT_MAP.put('Á', 'A');
        ACCENT_MAP.put('É', 'E');
        ACCENT_MAP.put('Í', 'I');
        ACCENT_MAP.put('Ó', 'O');
        ACCENT_MAP.put('Ú', 'U');

        // Registrar las medidas
        registrarMedida(new Suma());
        registrarMedida(new Count());
        registrarMedida(new Max());
        registrarMedida(new Min());
    }

    private static void registrarMedida(Medida medida) {
        String nombreNormalizado = removeAccents(medida.getNombre().toLowerCase());
        medidas.put(nombreNormalizado, medida);
    }

    private static String removeAccents(String text) {
        if (text == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            sb.append(ACCENT_MAP.getOrDefault(c, c));
        }

        return sb.toString();
    }

    public static Medida getMedida(String nombre) {
        String nombreNormalizado = removeAccents(nombre.toLowerCase());
        return medidas.get(nombreNormalizado);
    }

    public static Map<String, Medida> getMedidas() {
        return new HashMap<>(medidas);
    }
}