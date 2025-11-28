package mecanicas;

import audios.Musica;
import personajes.Personaje;
import red.HiloServidor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GestorDerrota {

    private boolean gameOver1 = false;
    private boolean gameOver2 = false;

    public void manejarMuerteJugador(Personaje personaje, boolean esJugador1,
    Musica musicaPartida, Stage stage,HiloServidor servidor, Game game) {
    	 if (personaje.getVida() > 0){
             if(esJugador1) this.gameOver1 = false;
             else this.gameOver2 = false;
             return;
         }

    	 
        if ((esJugador1 && !this.gameOver1) || (!esJugador1 && !this.gameOver2)) {
            if (esJugador1) this.gameOver1 = true;
            else this.gameOver2 = true;

    	if (this.gameOver1 && this.gameOver2) {
    	    // 🔥 Avisar por red
    	    servidor.enviarMensajeATodos("Derrota");
    	    // 🔥 Ejecutar animación en el hilo principal
    	    Gdx.app.postRunnable(() -> {
    	        personaje.morir(stage, servidor, game);
    	    });
    	}
        }
}
    public boolean partidaTerminada() {
        return this.gameOver1 && this.gameOver2;
    }

    public void resetear() {
        this.gameOver1 = false;
        this.gameOver2 = false;
    }
}
