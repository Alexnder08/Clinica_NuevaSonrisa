package pe.nuevasonrisa.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public final class FechaSistema {

    public static final ZoneId ZONA = ZoneId.of("America/Lima");

    private FechaSistema() {
    }

    public static LocalDate hoy() {
        return LocalDate.now(ZONA);
    }

    public static LocalTime ahora() {
        return LocalTime.now(ZONA);
    }

    public static LocalTime ahoraSinSegundos() {
        return ahora().withSecond(0).withNano(0);
    }
}
