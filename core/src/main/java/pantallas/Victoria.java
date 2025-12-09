package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import estilos.EstiloTexto;
import juego.Partida;
import red.HiloServidor;

public class Victoria implements Screen {

    private final Game game;
    private Stage stage;
    private Image imagen;

    private Label texto1;   
    private Label texto2;   

    private HiloServidor hiloServidor;

    private int indice = 0;   // ahora 0 = texto introductorio
    private boolean cambioRealizado = false;

    public Victoria(Game game, HiloServidor hiloServidor) {
        this.game = game;
        this.hiloServidor = hiloServidor;
        this.stage = new Stage();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        imagen = new Image();
        imagen.setFillParent(true);
        imagen.getColor().a = 0;

        texto1 = new Label("", EstiloTexto.ponerEstiloLabel(60, Color.WHITE));
        texto1.getColor().a = 0;

        texto2 = new Label("", EstiloTexto.ponerEstiloLabel(60, Color.WHITE));
        texto2.getColor().a = 0;

        colocarTextos();

        stage.addActor(imagen);
        stage.addActor(texto1);
        stage.addActor(texto2);

        mostrarAnimacion();
    }

    private TextureRegionDrawable getDrawable(int num) {
        return new TextureRegionDrawable(
                new Texture(Gdx.files.internal("imagenes/fondos/creditos_" + num + ".png"))
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    private void mostrarAnimacion() {

        // imagen solo se usa a partir de índice 1
        if (indice >= 1 && indice <= 5) {
            imagen.addAction(Actions.sequence(
                    Actions.fadeIn(3f),
                    Actions.delay(3f),
                    Actions.fadeOut(3f),
                    Actions.run(this::siguiente)
            ));
        } else {
            // intro y outro son solo texto
            texto1.addAction(Actions.sequence(
                    Actions.fadeIn(3f),
                    Actions.delay(3f),
                    Actions.fadeOut(3f)
            ));
            texto2.addAction(Actions.sequence(
                    Actions.fadeIn(3f),
                    Actions.delay(3f),
                    Actions.fadeOut(3f),
                    Actions.run(this::siguiente)
            ));
        }

        if (indice >= 1 && indice <= 5) {
            texto1.addAction(Actions.sequence(
                    Actions.fadeIn(1f),
                    Actions.delay(3f),
                    Actions.fadeOut(5f)
            ));

            texto2.addAction(Actions.sequence(
                    Actions.fadeIn(1f),
                    Actions.delay(3f),
                    Actions.fadeOut(5f)
            ));
        }
    }

    private void siguiente() {
        indice++;

        // OUTRO
        if (indice == 6) {
            // Apagar imagen
            imagen.getColor().a = 0;

            texto1.setText("Gracias por jugar");
            texto2.setText("");
            centrarTexto();
            mostrarAnimacion();
            return;
        }

        if (indice == 7) {
            texto1.setText("Profesor, por favor apruebenos");
            texto2.setText("");
            centrarTexto();
            mostrarAnimacion();
            return;
        }

        // Fin total
        if (indice > 7) {
            if (!cambioRealizado) {
                cambioRealizado = true;

                hiloServidor.desconectarClientes();
                hiloServidor.finalizar();

                Gdx.app.postRunnable(() -> {
                    game.setScreen(new Partida(game));
                });
            }
            return;
        }

        // Créditos con imágenes (1–5)
        if (indice >= 1 && indice <= 5) {
            imagen.setDrawable(getDrawable(indice));
            colocarTextos();
            mostrarAnimacion();
        }
    }

    private void centrarTexto() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        texto1.setAlignment(Align.center);
        texto2.setAlignment(Align.center);

        texto1.setPosition(w * 0.5f, h * 0.52f, Align.center);
        texto2.setPosition(w * 0.5f, h * 0.45f, Align.center);
    }

    private void colocarTextos() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        texto1.setText("");
        texto2.setText("");

        switch (indice) {

            case 0: // ------------------- INTRO -----------------------------------
                texto1.setText("Un juego hecho por");
                texto2.setText("3 pibes de la 35");
                centrarTexto();
                break;

            case 1:
                texto1.setText("Programador y desarrollador de sonido:");
                texto2.setText("Eduardo Orsi");
                centrarTexto();
                break;

            case 2:
                texto1.setText("Desarrollador de personajes:");
                texto2.setText("Eynar Mejia");

                texto1.setAlignment(Align.left);
                texto2.setAlignment(Align.left);

                texto1.setPosition(w * 0.0000001f, h * 0.82f, Align.center);
                texto2.setPosition(w * 0.0000001f, h * 0.75f, Align.center);

                break;

            case 3:
                texto1.setText("Co-Programador:");
                texto2.setText("Kevin De Groote");
                centrarTexto();
                break;

            case 4:
                texto1.setText("Creador de la portada:");
                texto2.setText("Juan Benito Suarez Dominguez (Lokevas)");
                centrarTexto();
                break;

            case 5:
                texto1.setText("Apoyo Emocional:");
                texto2.setText("Bang (usuario de Discord)");
                centrarTexto();
                break;
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
