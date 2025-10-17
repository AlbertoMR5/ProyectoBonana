import java.io.*; //esto es para poder leer y escribir archivos.
import java.nio.charset.StandardCharsets; //para poder guardar caracteres especiales.
import java.util.ArrayList; //para almacenar en listas
import java.util.List;
import java.util.Scanner; //para poder leer lo que escribe el usuario

class Fruta {
    private int id;             //permite identificar de forma única
    private String nombre;      //string permite guardar nombres
    private double precioKg;    //double permite guardar con decimales
    private int stockUnidades;  //int permite guardar nº enteros

    public Fruta() {}

    //esto es un constructor para crear objetos Fruta fácilmente pasando todos los datos necesarios
    //this. se usa para diferenciar entre el parámetro y el atributo de la clase.

    public Fruta(int id, String nombre, double precioKg, int stockUnidades) {
        this.id = id;
        this.nombre = nombre;
        this.precioKg = precioKg;
        this.stockUnidades = stockUnidades;
    }

    // Getters y Setters. Get para leer atributos desde otras clases y Set para modificar valores de forma controlada.

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecioKg() { return precioKg; }
    public void setPrecioKg(double precioKg) { this.precioKg = precioKg; }

    public int getStockUnidades() { return stockUnidades; }
    public void setStockUnidades(int stockUnidades) { this.stockUnidades = stockUnidades; }

    @Override
    public String toString() {
        return String.format("ID: %d | Nombre: %-20s | Precio/kg: %.2f€ | Stock: %d Unidades",
                id, nombre, precioKg, stockUnidades);
    }
}   //%-20s hace que el nombre ocupe 20 espacios y se alinee a la izq.
    //%.2f Muestra el precio con 2 decimales (para los céntimos)
    //@Override Indica que estamos reescribiendo un método que ya existe

public class ProyectoBonana {
    private static List<Fruta> inventario = new ArrayList<>();
    private static final String RUTA_ARCHIVO = "data/frutas.txt";
    private static final Scanner scanner = new Scanner(System.in);
//estas son variables principales del programa

    public static void main(String[] args) { //el método main es el método de entrada, es el que java ejecuta primero.
        cargarDirectorio();
        mostrarMenu();
    }

    private static void cargarDirectorio() {
        File directorio = new File("data"); //asegura que exista la carpeta Data
        if (!directorio.exists()) {
            directorio.mkdirs(); //mkdirs crea la carpeta y también las c. padres si no existen.
        }
    }

    private static void mostrarMenu() {
        while (true) { //Mantiene el programa ejecutándose hasta que el usuario elija salir.
            System.out.println("\n--- GESTIÓN FRUTERÍA ---");
            System.out.println("1. Añadir fruta");
            System.out.println("2. Listar frutas");
            System.out.println("3. Exportar a TXT");
            System.out.println("4. Importar desde TXT");
            System.out.println("5. Salir");
            System.out.print("Seleccione opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) { //Dirige a cada función según la opción elegida.
                    case 1 -> añadirFruta();
                    case 2 -> listarFrutas();
                    case 3 -> exportarTXT();
                    case 4 -> importarTXT();
                    case 5 -> {
                        System.out.println("¡Hasta pronto!");
                        return;
                    }
                    default -> System.out.println("Opción no válida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número");
            }   //try-catch → Captura errores si el usuario escribe texto en lugar de números
        }
    }

    private static void añadirFruta() {
        try {
            System.out.println("\n--- AÑADIR FRUTA ---");

            int id = inventario.size() + 1;

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim(); //.trim() → Elimina espacios al principio y final para evitar nombres vacíos
            if (nombre.length() < 2) {
                System.out.println("Error: El nombre debe tener al menos 2 caracteres");
                return;
            }

            System.out.print("Precio por kg: ");
            double precio = Double.parseDouble(scanner.nextLine());
            if (precio < 0) {
                System.out.println("Error: El precio no puede ser negativo");
                return;
            }

            System.out.print("Stock en unidades: ");
            int stock = Integer.parseInt(scanner.nextLine());
            if (stock < 0) {
                System.out.println("Error: El stock no puede ser negativo");
                return;
            }

            Fruta nuevaFruta = new Fruta(id, nombre, precio, stock);
            inventario.add(nuevaFruta);
            System.out.println("✅ Fruta añadida correctamente");

        } catch (NumberFormatException e) {
            System.out.println("Error: Formato numérico inválido");
        }
    }

    private static void listarFrutas() {
        System.out.println("\n--- INVENTARIO ACTUAL ---");
        if (inventario.isEmpty()) {
            System.out.println("No hay frutas en el inventario");
            return;
        }
        inventario.forEach(System.out::println);
    }

    private static void exportarTXT() {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(RUTA_ARCHIVO),
                        StandardCharsets.UTF_8))) {

            for (Fruta fruta : inventario) {
                writer.printf("%d;%s;%.2f;%d%n",
                        fruta.getId(),
                        fruta.getNombre(),
                        fruta.getPrecioKg(),
                        fruta.getStockUnidades());
            }

            System.out.println("✅ Inventario exportado a " + RUTA_ARCHIVO);
        } catch (IOException e) {
            System.out.println("❌ Error al exportar: " + e.getMessage());
        }
    }

    private static void importarTXT() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("❌ No existe el archivo " + RUTA_ARCHIVO);
            return;
        }

        List<Fruta> nuevoInventario = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(archivo),
                        StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                try {
                    String[] campos = linea.split(";"); //split(";") Divide cada línea usando el punto y coma como separador.


                    if (campos.length != 4) continue; //Salta líneas corruptas o mal formadas.

                    Fruta fruta = new Fruta(
                            Integer.parseInt(campos[0]), //Convierten el texto a números
                            campos[1],
                            Double.parseDouble(campos[2]),//Convierten el texto a números
                            Integer.parseInt(campos[3])
                    );
                    nuevoInventario.add(fruta);
                } catch (NumberFormatException e) {
                    System.out.println("Línea ignorada: " + linea);
                }
            }

            inventario = nuevoInventario;
            System.out.println("✅ Inventario importado correctamente");

        } catch (IOException e) {
            System.out.println("❌ Error al importar: " + e.getMessage()); //e.getMessage()MuesMuestra el error específico que ocurrió
        }
    }
}