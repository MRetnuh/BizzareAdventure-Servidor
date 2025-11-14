package interfaces;

public interface GameController {
    void mover(int numJugador);
    void empezarJuego();
    void finalizarTiempo();
    void procesarInputRemoto(int numJugador, boolean derecha, boolean izquierda, boolean saltar, boolean atacar);
    int getIdPersonaje(int idCliente);
    int getNumNivel();
    }

