package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import juego.Partida;
import red.HiloServidor;

public class PantallaEspera implements Screen {

    private final Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private HiloServidor hiloServidor;
    private Partida partida;
    public PantallaEspera(Game game, HiloServidor hiloServidor, Partida partida) {
        this.game = game;
        this.hiloServidor = hiloServidor;
        this.partida = partida;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Esperando jugadores...", 100, 200);
        if(this.hiloServidor.getClientesConectados() == 2) {
        	Gdx.app.postRunnable(() -> {
        	this.game.setScreen(partida); 
        	});
        }
        batch.end();
    }

    @Override public void dispose() { batch.dispose(); font.dispose(); }

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
}

