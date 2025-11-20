package mecanicas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Stage;
import audios.Musica;
import enemigos.EnemigoBase;
import juego.Partida;
import jugadores.Jugador;
import mecanicas.GestorDerrota;
import niveles.NivelBase;
import pantallas.NivelSuperado;
import pantallas.Victoria;
import red.HiloServidor;
public class GestorNiveles {

    private final Game JUEGO;
    private final NivelBase[] niveles;
    private NivelBase nivelActual;
    private int indiceNivelActual = 0;
    
    public GestorNiveles(Game juego, NivelBase[] niveles, NivelBase nivelActual, Partida partida) {
        this.JUEGO = juego;
        this.niveles = niveles;
        this.nivelActual = nivelActual;
    }
 
    public void inicializarNivel(Jugador[] jugadores, int jugador1, int jugador2,
                                 Stage stage, GestorDerrota gestorDerrota) {

    	this.nivelActual.restaurarEstadoCajas();
        this.nivelActual.crearEnemigos();

        if (jugadores[jugador1].getPersonajeElegido() != null) {
            jugadores[jugador1].getPersonajeElegido()
                    .setPosicion(nivelActual.getInicioX1(), nivelActual.getInicioY1());
            jugadores[jugador1].generarPersonajeAleatorio();
        }
        if (jugadores[jugador2].getPersonajeElegido() != null) {
            jugadores[jugador2].getPersonajeElegido()
                    .setPosicion(nivelActual.getInicioX2(), nivelActual.getInicioY2());
            jugadores[jugador2].generarPersonajeAleatorio();
        }

        stage.clear();

        if (jugadores[jugador1].getPersonajeElegido() != null)
            stage.addActor(jugadores[jugador1].getPersonajeElegido());

        if (jugadores[jugador2].getPersonajeElegido() != null)
            stage.addActor(jugadores[jugador2].getPersonajeElegido());

        for (EnemigoBase enemigo : nivelActual.getEnemigos()) {
            stage.addActor(enemigo);
        }

        gestorDerrota.resetear();
    }

    public void comprobarVictoriaYAvanzar(Jugador[] jugadores, Partida partida, HiloServidor hiloServidor) {
        boolean victoria = nivelActual.comprobarVictoria(
            jugadores[0].getPersonajeElegido().getX(),
            jugadores[0].getPersonajeElegido().getY(),
            jugadores[1].getPersonajeElegido().getX(),
            jugadores[1].getPersonajeElegido().getY()
        );

        if (victoria) {
            // proteger índice
           

            // Guardar nivel actual antes del incremento para el mensaje
            String nivelActualNombre = this.nivelActual.getNombreNivel();
            this.indiceNivelActual++; 
            if (this.indiceNivelActual + 1 > this.niveles.length) {
               hiloServidor.sendMessageToAll("Victoria");
               this.JUEGO.setScreen(new Victoria(this.JUEGO, hiloServidor));
            }
            
            this.nivelActual = this.niveles[this.indiceNivelActual];

            // Enviar mensaje homogéneo: usar "NivelCompletado" (o "NivelSuperado") — elegimos "NivelCompletado"
            String mensajeNivel = String.format("NivelCompletado:%s:%s:%d",
                    nivelActualNombre,
                    this.nivelActual.getNombreNivel(),this.indiceNivelActual
            );
            hiloServidor.sendMessageToAll(mensajeNivel);
            
            // Mostrar pantalla en el servidor
            NivelSuperado nivelSuperado = new NivelSuperado(
                nivelActualNombre,
                this.JUEGO,
                this.nivelActual.getNombreNivel(),
                partida
            );
            JUEGO.setScreen(nivelSuperado);
            // Enviar Empezar con los ids correctos (jugadores 0 y 1)
          
        }
    }

    
    public void inicializarSiguienteNivel(Jugador[] jugadores, int jugador1, int jugador2,
    Stage stage, GestorDerrota gestorDerrota, Partida partida, HiloServidor hiloServidor) {
    		this.nivelActual = this.niveles[this.indiceNivelActual];
            inicializarNivel(jugadores, jugador1, jugador2, stage, gestorDerrota);
            int p1ID = jugadores[0].getIdPersonajeElegido();
            int p2ID = jugadores[1].getIdPersonajeElegido();
            hiloServidor.sendMessageToAll(String.format("CambioPersonajesNivel:%d:%d", p1ID, p2ID));
    }
    
    public NivelBase getNivelActual() {
        return this.nivelActual;
    }
    
    public int getIndiceNivelActual() {
    	return this.indiceNivelActual;
    }
    
}
