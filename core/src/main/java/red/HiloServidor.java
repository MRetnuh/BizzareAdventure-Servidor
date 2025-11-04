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
    private ArrayList<Cliente> clientes = new ArrayList<Cliente>();
    private GameController gameController;

    public HiloServidor(GameController gameController) {
        this.gameController = gameController;
        try {
            this.socket = new DatagramSocket(this.servidorPort);
        } catch (SocketException e) {
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket paquete = new DatagramPacket(new byte[1024], 1024);
            try {
                this.socket.receive(paquete);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        } while (!this.fin);
    }

    private void procesarCliente(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");
        int index = encontrarClienteIndex(packet);
        System.out.println("Mensaje recibido " + message);

        if (parts[0].equals("Conectado")) {

            if (index != -1) {
                System.out.println("Cliente ya conectado");
                this.enviarMensaje("Yaconectado", packet.getAddress(), packet.getPort());
                return;
            }
            if(this.clientesConectados < this.MAX_CLIENTES) {
                this.clientesConectados++;
                Cliente newClient = new Cliente(this.clientesConectados, packet.getAddress(), packet.getPort());
                this.clientes.add(newClient);
                enviarMensaje("Conectado:"+this.clientesConectados, packet.getAddress(), packet.getPort());

                if(this.clientesConectados == this.MAX_CLIENTES) {
                    for(Cliente client : this.clientes) {
                        enviarMensaje("Empezar", client.getIp(), client.getPort());
                        this.gameController.empezarJuego();
                    }
                }

            } else {
                enviarMensaje("Lleno", packet.getAddress(), packet.getPort());
            }
        } else if(index==-1){
            System.out.println("Cliente no conectado");
            this.enviarMensaje("Noconectado", packet.getAddress(), packet.getPort());
            return;
        } else {
            Cliente client = this.clientes.get(index);
            switch(parts[0]){
                case "Mover":
                    this.gameController.mover(client.getNum());
                    break;
            }

        }
    }

    private int encontrarClienteIndex(DatagramPacket packet) {
        int i = 0;
        int clientIndex = -1;
        while(i < this.clientes.size() && clientIndex == -1) {
            Cliente client = this.clientes.get(i);
            String id = packet.getAddress().toString()+":"+packet.getPort();
            if(id.equals(client.getId())){
                clientIndex = i;
            }
            i++;

        }
        return clientIndex;
    }

    public void enviarMensaje(String message, InetAddress clientIp, int clientPort) {
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, clientIp, clientPort);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void terminate(){
        this.fin = true;
        this.socket.close();
        this.interrupt();
    }

    public void sendMessageToAll(String message) {
        for (Cliente client : this.clientes) {
            enviarMensaje(message, client.getIp(), client.getPort());
        }
    }

    public void disconnectClients() {
        for (Cliente client : this.clientes) {
            enviarMensaje("Desconectado", client.getIp(), client.getPort());
        }
        this.clientes.clear();
        this.clientesConectados = 0;
    }
}
