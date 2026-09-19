package util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Generador de archivos Excel (.xlsx) usando SOLO la libreria estandar de Java.
 */
public class ExcelBuilder {

    // ── Indices de estilo definidos en styles.xml (ver crearStyles) ──
    private static final int ESTILO_NORMAL   = 0;
    private static final int ESTILO_CABECERA = 1;
    private static final int ESTILO_MONEDA   = 2;
    private static final int ESTILO_BORDE    = 3;
    private static final int ESTILO_TITULO   = 4;
    private static final int ESTILO_DECIMAL  = 5;

    private final List<Hoja> hojas = new ArrayList<>();

    /** Crea una hoja nueva dentro del libro y la devuelve para llenarla. */
    public Hoja nuevaHoja(String nombre) {
        Hoja h = new Hoja(nombre);
        hojas.add(h);
        return h;
    }

    /** Envuelve un numero para que Excel lo muestre como "S/ 1,234.50". */
    public static Soles soles(double valor) {
        return new Soles(valor);
    }

    /** Marcador de importe en soles. */
    public static class Soles {
        final double valor;
        Soles(double valor) { this.valor = valor; }
    }

    // ════════════════════════════════════════════════════════════
    //  CELDA
    // ════════════════════════════════════════════════════════════
    private static class Celda {
        String texto;      // si es texto
        Double numero;     // si es numero
        int estilo;

        static Celda texto(String t, int estilo) {
            Celda c = new Celda();
            c.texto = t == null ? "" : t;
            c.estilo = estilo;
            return c;
        }

        static Celda numero(double n, int estilo) {
            Celda c = new Celda();
            c.numero = n;
            c.estilo = estilo;
            return c;
        }
    }

    // ════════════════════════════════════════════════════════════
    //  HOJA
    // ════════════════════════════════════════════════════════════
    public static class Hoja {

        private final String nombre;
        private final List<List<Celda>> filas = new ArrayList<>();
        private int[] anchos = new int[0];

        Hoja(String nombre) {
            // Excel prohibe : \ / ? * [ ] en el nombre y lo limita a 31 caracteres
            String n = nombre.replaceAll("[:\\\\/?*\\[\\]]", " ");
            this.nombre = n.length() > 31 ? n.substring(0, 31) : n;
        }

        /** Ancho de cada columna, en caracteres. */
        public Hoja anchos(int... ancho) {
            this.anchos = ancho;
            return this;
        }

        /** Fila de titulo, en grande. */
        public Hoja titulo(String texto) {
            List<Celda> fila = new ArrayList<>();
            fila.add(Celda.texto(texto, ESTILO_TITULO));
            filas.add(fila);
            return this;
        }

        /** Fila suelta de texto sin formato (subtitulos, notas al pie). */
        public Hoja linea(String texto) {
            List<Celda> fila = new ArrayList<>();
            fila.add(Celda.texto(texto, ESTILO_NORMAL));
            filas.add(fila);
            return this;
        }

        /** Fila vacia separadora. */
        public Hoja blanco() {
            filas.add(new ArrayList<Celda>());
            return this;
        }

        /** Fila de encabezados de tabla (fondo oscuro, letra blanca). */
        public Hoja cabecera(String... titulos) {
            List<Celda> fila = new ArrayList<>();
            for (String t : titulos) fila.add(Celda.texto(t, ESTILO_CABECERA));
            filas.add(fila);
            return this;
        }

        /**
         * Fila de datos. El tipo de cada valor decide el formato:
         *   String            -> texto
         *   Integer / Long    -> numero entero
         *   Double / Float    -> numero con 2 decimales
         *   ExcelBuilder.soles(x) -> importe "S/ x"
         *   null              -> celda vacia
         */
        public Hoja fila(Object... valores) {
            List<Celda> fila = new ArrayList<>();
            for (Object v : valores) {
                if (v == null) {
                    fila.add(Celda.texto("", ESTILO_BORDE));
                } else if (v instanceof Soles) {
                    fila.add(Celda.numero(((Soles) v).valor, ESTILO_MONEDA));
                } else if (v instanceof Integer || v instanceof Long) {
                    fila.add(Celda.numero(((Number) v).doubleValue(), ESTILO_BORDE));
                } else if (v instanceof Number) {
                    fila.add(Celda.numero(((Number) v).doubleValue(), ESTILO_DECIMAL));
                } else {
                    fila.add(Celda.texto(v.toString(), ESTILO_BORDE));
                }
            }
            filas.add(fila);
            return this;
        }
    }

    // ════════════════════════════════════════════════════════════
    //  ESCRITURA DEL ZIP
    // ════════════════════════════════════════════════════════════

    /** Vuelca el libro completo al flujo de salida */
    public void escribir(OutputStream salida) throws IOException {
        if (hojas.isEmpty()) nuevaHoja("Hoja1");

        ZipOutputStream zip = new ZipOutputStream(salida);
        try {
            agregar(zip, "[Content_Types].xml",        crearContentTypes());
            agregar(zip, "_rels/.rels",                crearRelsRaiz());
            agregar(zip, "xl/workbook.xml",            crearWorkbook());
            agregar(zip, "xl/_rels/workbook.xml.rels", crearWorkbookRels());
            agregar(zip, "xl/styles.xml",              crearStyles());
            for (int i = 0; i < hojas.size(); i++) {
                agregar(zip, "xl/worksheets/sheet" + (i + 1) + ".xml",
                        crearHojaXml(hojas.get(i)));
            }
        } finally {
            zip.finish();
            zip.flush();
        }
    }

    private void agregar(ZipOutputStream zip, String ruta, String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(ruta));
        zip.write(contenido.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static final String CAB = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";

    private String crearContentTypes() {
        StringBuilder sb = new StringBuilder(CAB);
        sb.append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">");
        sb.append("<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>");
        sb.append("<Default Extension=\"xml\" ContentType=\"application/xml\"/>");
        sb.append("<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>");
        sb.append("<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>");
        for (int i = 0; i < hojas.size(); i++) {
            sb.append("<Override PartName=\"/xl/worksheets/sheet").append(i + 1)
              .append(".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");
        }
        sb.append("</Types>");
        return sb.toString();
    }

    private String crearRelsRaiz() {
        return CAB
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
            + "</Relationships>";
    }

    private String crearWorkbook() {
        StringBuilder sb = new StringBuilder(CAB);
        sb.append("<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" ")
          .append("xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets>");
        for (int i = 0; i < hojas.size(); i++) {
            sb.append("<sheet name=\"").append(escapar(hojas.get(i).nombre))
              .append("\" sheetId=\"").append(i + 1)
              .append("\" r:id=\"rId").append(i + 1).append("\"/>");
        }
        sb.append("</sheets></workbook>");
        return sb.toString();
    }

    private String crearWorkbookRels() {
        StringBuilder sb = new StringBuilder(CAB);
        sb.append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">");
        for (int i = 0; i < hojas.size(); i++) {
            sb.append("<Relationship Id=\"rId").append(i + 1)
              .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet")
              .append(i + 1).append(".xml\"/>");
        }
        // El id de styles va despues del de todas las hojas para no chocar
        sb.append("<Relationship Id=\"rId").append(hojas.size() + 1)
          .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>");
        sb.append("</Relationships>");
        return sb.toString();
    }

    /**
     * Paleta del sistema: cabecera azul oscuro (#1A1A2E) con letra blanca,
     * igual que las tablas de la aplicacion web.
     */
    private String crearStyles() {
        return CAB
            + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
            + "<numFmts count=\"1\">"
            +   "<numFmt numFmtId=\"164\" formatCode=\"&quot;S/ &quot;#,##0.00\"/>"
            + "</numFmts>"
            + "<fonts count=\"3\">"
            +   "<font><sz val=\"11\"/><name val=\"Calibri\"/></font>"
            +   "<font><b/><sz val=\"11\"/><color rgb=\"FFFFFFFF\"/><name val=\"Calibri\"/></font>"
            +   "<font><b/><sz val=\"14\"/><color rgb=\"FFE94560\"/><name val=\"Calibri\"/></font>"
            + "</fonts>"
            + "<fills count=\"3\">"
            +   "<fill><patternFill patternType=\"none\"/></fill>"
            +   "<fill><patternFill patternType=\"gray125\"/></fill>"
            +   "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF1A1A2E\"/><bgColor indexed=\"64\"/></patternFill></fill>"
            + "</fills>"
            + "<borders count=\"2\">"
            +   "<border><left/><right/><top/><bottom/><diagonal/></border>"
            +   "<border>"
            +     "<left style=\"thin\"><color rgb=\"FFCBD5E1\"/></left>"
            +     "<right style=\"thin\"><color rgb=\"FFCBD5E1\"/></right>"
            +     "<top style=\"thin\"><color rgb=\"FFCBD5E1\"/></top>"
            +     "<bottom style=\"thin\"><color rgb=\"FFCBD5E1\"/></bottom>"
            +     "<diagonal/>"
            +   "</border>"
            + "</borders>"
            + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
            + "<cellXfs count=\"6\">"
            +   "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
            +   "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\"/>"
            +   "<xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyNumberFormat=\"1\" applyBorder=\"1\"/>"
            +   "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyBorder=\"1\"/>"
            +   "<xf numFmtId=\"0\" fontId=\"2\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>"
            +   "<xf numFmtId=\"2\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyNumberFormat=\"1\" applyBorder=\"1\"/>"
            + "</cellXfs>"
            + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
            + "</styleSheet>";
    }

    private String crearHojaXml(Hoja hoja) {
        StringBuilder sb = new StringBuilder(CAB);
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");

        if (hoja.anchos.length > 0) {
            sb.append("<cols>");
            for (int i = 0; i < hoja.anchos.length; i++) {
                sb.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1)
                  .append("\" width=\"").append(hoja.anchos[i]).append("\" customWidth=\"1\"/>");
            }
            sb.append("</cols>");
        }

        sb.append("<sheetData>");
        for (int f = 0; f < hoja.filas.size(); f++) {
            List<Celda> fila = hoja.filas.get(f);
            int nroFila = f + 1;
            sb.append("<row r=\"").append(nroFila).append("\">");
            for (int c = 0; c < fila.size(); c++) {
                Celda celda = fila.get(c);
                String ref = letraColumna(c) + nroFila;
                if (celda.numero != null) {
                    sb.append("<c r=\"").append(ref).append("\" s=\"").append(celda.estilo).append("\">")
                      .append("<v>").append(celda.numero).append("</v></c>");
                } else {
                    // inlineStr evita tener que construir la tabla sharedStrings
                    sb.append("<c r=\"").append(ref).append("\" s=\"").append(celda.estilo)
                      .append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                      .append(escapar(celda.texto)).append("</t></is></c>");
                }
            }
            sb.append("</row>");
        }
        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    /** 0 -> A, 1 -> B ... 26 -> AA */
    private static String letraColumna(int indice) {
        StringBuilder sb = new StringBuilder();
        int n = indice;
        while (n >= 0) {
            sb.insert(0, (char) ('A' + (n % 26)));
            n = (n / 26) - 1;
        }
        return sb.toString();
    }

    /** Escapa los caracteres que romperian el XML. */
    private static String escapar(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<':  sb.append("&lt;");   break;
                case '>':  sb.append("&gt;");   break;
                case '&':  sb.append("&amp;");  break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                default:
                    // XML 1.0 no admite caracteres de control
                    if (ch >= 0x20 || ch == '\t' || ch == '\n') sb.append(ch);
            }
        }
        return sb.toString();
    }
}
