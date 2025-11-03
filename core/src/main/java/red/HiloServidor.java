package red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class HiloServidor extends Thread{
	
	private DatagramSocket socket;
    private int servidorPort = 5555;
    private boolean fin = false;
    private final int MAX_CLIENTES = 2;
    private int clientesConectados = 0;
    private ArrayList<Cliente> clientes = new ArrayList<Cliente>();
    
    public HiloServidor() {
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
        } while(!this.fin);
    }

}
