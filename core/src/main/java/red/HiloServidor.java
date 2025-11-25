package red;

import interfaces.GameController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class HiloServidor extends Thread {

    private DatagramSocket socket;
    private int servidorPort = 5555;
    private boolean fin = false;
    private final int MAX_CLIENTES = 2;
    private int clientesConectados = 0;
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private GameController gameController;
    private boolean enJuego = false;
    private final long TIEMPO_MAX_INACTIVIDAD = 5000; 

    public HiloServidor(GameController gameController) {
        this.gameController = gameController;
        try {
            this.socket = new DatagramSocket(this.servidorPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket paquete = new DatagramPacket(new byte[1024], 1024);
            try {
                this.socket.receive(paquete);
                this.procesarMensaje(paquete);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Verificar la inactividad de los clientes
           
            for (int i = this.clientes.size() - 1; i >= 0; i--) {
                Cliente cliente = this.clientes.get(i);
                // Si el cliente ha estado inactivo más allá del tiempo máximo
                if(this.enJuego) {
                	long tiempoActual = System.currentTimeMillis();
                if (tiempoActual - cliente.getUltimaActividad() > this.TIEMPO_MAX_INACTIVIDAD) {
                    System.out.println("Cliente desconectado por inactividad: " + cliente.getId());
                    // Enviar mensaje de desconexión al cliente
                    enviarMensajeATodos("ErrorJugador");
                    // Eliminar al cliente de la lista
                    this.clientes.remove(i);
                    this.clientesConectados--;
                }
                }
            }

        } while (!this.fin);
    }

    private void procesarMensaje(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");
        int index = encontrarClienteIndex(packet);
        System.out.println("Mensaje recibido: " + message);

        if (parts[0].equals("Conectado")) {
            if (index != -1) {
                System.out.println("Cliente ya conectado");
                enviarMensaje("Yaconectado", packet.getAddress(), packet.getPort());
                return;
            }
            if (this.clientesConectados < this.MAX_CLIENTES) {
                this.clientesConectados++;
                Cliente newClient = new Cliente(this.clientesConectados, packet.getAddress(), packet.getPort());
                this.clientes.add(newClient);
                enviarMensaje("Conectado:" + this.clientesConectados, packet.getAddress(), packet.getPort());

                if (this.clientesConectados == this.MAX_CLIENTES) {
                    int p1ID = this.gameController.getIdPersonaje(1); 
                    int p2ID = this.gameController.getIdPersonaje(2);
                    int indiceNivelActual = this.gameController.getNumNivel();
                    String mensajeNivelActual = String.format("Nivel:%d", indiceNivelActual);
                    String mensajeEmpezar = String.format("Empezar:%d:%d", p1ID, p2ID);
                    for (Cliente client : this.clientes) {
                        enviarMensaje(mensajeNivelActual, client.getIp(), client.getPort());
                        enviarMensaje(mensajeEmpezar, client.getIp(), client.getPort());
                    }
                    this.gameController.empezarJuego();
                }
            } else {
                enviarMensaje("Lleno", packet.getAddress(), packet.getPort());
            }
        } else if (index == -1) {
            System.out.println("Cliente no conectado");
            this.enviarMensaje("Noconectado", packet.getAddress(), packet.getPort());
        } else {
            switch (parts[0]) {
            	case "ActivarEnJuego":
            		this.enJuego = true;
            		break;
            	case "DetenerEnJuego":
            		this.enJuego = false;
            		break;
                case "Mover":
                	int numJugador = Integer.parseInt(parts[1]);
                    boolean derecha = Boolean.parseBoolean(parts[2]);
                    boolean izquierda = Boolean.parseBoolean(parts[3]);
                    boolean saltar = Boolean.parseBoolean(parts[4]);
                    boolean atacar = Boolean.parseBoolean(parts[5]);
                    this.gameController.procesarInputRemoto(numJugador, derecha, izquierda, saltar, atacar);
                    break;
            }

            Cliente cliente = clientes.get(index);
            cliente.actualizarActividad();
        }
    }

    private int encontrarClienteIndex(DatagramPacket packet) {
        int i = 0;
        int clientIndex = -1;
        while (i < this.clientes.size() && clientIndex == -1) {
            Cliente client = this.clientes.get(i);
            String id = packet.getAddress().toString() + ":" + packet.getPort();
            if (id.equals(client.getId())) {
                clientIndex = i;
            }
            i++;
        }
        return clientIndex;
    }

    private void enviarMensaje(String message, InetAddress clientIp, int clientPort) {
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, clientIp, clientPort);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finalizar() {
        this.fin = true;
        this.socket.close();
        this.interrupt();
        System.out.println("Conexión finalizada");
    }

    public void enviarMensajeATodos(String message) {
        for (Cliente client : this.clientes) {
            enviarMensaje(message, client.getIp(), client.getPort());
        }
    }

    public int getClientesConectados() {
        return this.clientesConectados;
    }
    
    public void setEnJuego(boolean e) {
    	this.enJuego = e;
    }

    public void desconectarClientes() {
        for (Cliente client : this.clientes) {
            enviarMensaje("Desconectado", client.getIp(), client.getPort());
        }
        this.clientes.clear();
        this.clientesConectados = 0;
    }
}
