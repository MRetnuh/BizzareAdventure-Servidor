package mecanicas;

import audios.Musica;
import personajes.Personaje;
import red.HiloServidor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GestorDerrota {

    private boolean gameOver1 = false;
    private boolean gameOver2 = false;

    public boolean isGameOver1() { return gameOver1; }
    public boolean isGameOver2() { return gameOver2; }

    public void manejarMuerteJugador(Personaje personaje, boolean esJugador1,
            Musica musicaPartida, Stage stage,
            HiloServidor servidor) {
    	 if (personaje.getVida() > 0){
             if(esJugador1) this.gameOver1 = false;
             else this.gameOver2 = false;
             return;
         }

    	 
        if ((esJugador1 && !gameOver1) || (!esJugador1 && !gameOver2)) {
            if (esJugador1) gameOver1 = true;
            else gameOver2 = true;

    	if (gameOver1 && gameOver2) {
    	    // 🔥 Avisar por red
    	    servidor.sendMessageToAll("Derrota");
    	    // 🔥 Ejecutar animación en el hilo principal
    	    Gdx.app.postRunnable(() -> {
    	        personaje.morir(stage);
    	    });
    	}
        }
}



    public boolean partidaTerminada() {
        return gameOver1 && gameOver2;
    }

    public void resetear() {
        gameOver1 = false;
        gameOver2 = false;
    }
}
