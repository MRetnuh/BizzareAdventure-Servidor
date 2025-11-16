package io.github.some;

import com.badlogic.gdx.Game;

import audios.Musica;
import juego.Partida;
import pantallas.Menu;

public class Principal extends Game {

    @Override
    public void create() {
        setScreen(new Partida(this));
    }
}
