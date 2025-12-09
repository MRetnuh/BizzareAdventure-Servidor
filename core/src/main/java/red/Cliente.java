package red;

import java.net.InetAddress;

public class Cliente {

    private String id;
    private int num;
    private InetAddress ip;
    private int port;
    private long ultimaActividad; // Timestamp de la última actividad

  
    public Cliente(int num, InetAddress ip, int port) {
        this.num = num;
        this.id = ip.toString() + ":" + port;
        this.ip = ip;
        this.port = port;
        this.ultimaActividad = System.currentTimeMillis(); // Inicializar con el tiempo actual
    }

    // Obtener el ID único del cliente
    public String getId() {
        return this.id;
    }

    // Obtener la dirección IP del cliente
    public InetAddress getIp() {
        return this.ip;
    }

    // Obtener el puerto del cliente
    public int getPort() {
        return this.port;
    }

    // Obtener el número del cliente
    public int getNum() {
        return this.num;
    }

    // Actualizar el timestamp de la última actividad
    public void actualizarActividad() {
        this.ultimaActividad = System.currentTimeMillis(); // Actualizar con el tiempo actual
    }

    // Obtener el timestamp de la última actividad
    public long getUltimaActividad() {
        return this.ultimaActividad;
    }
}
