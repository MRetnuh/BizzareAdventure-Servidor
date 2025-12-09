package mecanicas;

import personajes.Personaje;
import niveles.NivelBase;
import com.badlogic.gdx.math.Rectangle;

public class GestorGravedad {

    public static void aplicarGravedad(Personaje personaje, float delta, NivelBase nivel) {
    	float gravedad = -500;
    	if(personaje.getVida() <= 0) {
    		return;
    	}
    		
        boolean estaSobreElSuelo = nivel.detectarColision(
            new Rectangle(personaje.getX(), personaje.getY() - 1, 16, 16)
        );

        personaje.guardarPosicionAnterior();

        if (!estaSobreElSuelo) {
            personaje.setVelocidadCaida(personaje.getVelocidadCaida() + gravedad * delta);
            personaje.setY(personaje.getY() + personaje.getVelocidadCaida() * delta);
        } else {
            personaje.setVelocidadCaida(0);
        }

        if (personaje.getY() < -190) {
            personaje.reducirVida();
        }
    }
}
