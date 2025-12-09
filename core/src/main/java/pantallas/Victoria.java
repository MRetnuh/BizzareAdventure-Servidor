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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
    private Label texto;
    private HiloServidor hiloServidor;

    private int indice = 1;
    private boolean cambioRealizado = false;

    public Victoria(Game game, HiloServidor hiloServidor) {
        this.game = game;
        this.hiloServidor = hiloServidor;
        this.stage = new Stage();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        imagen = new Image(getDrawable(indice));
        imagen.setFillParent(true);
        imagen.getColor().a = 0;

        texto = new Label(textoDe(indice), EstiloTexto.ponerEstiloLabel(45, Color.WHITE));
        texto.setAlignment(Align.center);
        texto.getColor().a = 0;

        Table tabla = new Table();
        tabla.setFillParent(true);

        tabla.add(imagen).grow();
        tabla.row();
        tabla.add(texto).padBottom(60);

        stage.addActor(tabla);

        mostrarImagen();
    }

    private TextureRegionDrawable getDrawable(int num){
        return new TextureRegionDrawable(new Texture(Gdx.files.internal("imagenes/fondos/creditos_" + num + ".png")));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    private void mostrarImagen(){

        imagen.addAction(Actions.sequence(
            Actions.fadeIn(2f),
            Actions.delay(2f),
            Actions.fadeOut(2f),
            Actions.run(this::siguienteImagen)
        ));

        texto.addAction(Actions.sequence(
            Actions.fadeIn(2f),
            Actions.delay(2f),
            Actions.fadeOut(2f)
        ));
    }

    private void siguienteImagen(){
        indice++;

        if (indice > 5){

            if(!cambioRealizado){
                cambioRealizado = true;

                // 👇 NO TOQUÉ NADA DE ESTO
                hiloServidor.desconectarClientes();
                hiloServidor.finalizar();

                Gdx.app.postRunnable(() -> {
                    game.setScreen(new Partida(game));
                });
            }
            return;
        }

        imagen.setDrawable(getDrawable(indice));
        texto.setText(textoDe(indice));
        mostrarImagen();
    }

    private String textoDe(int i){
        switch(i){
            case 1: return "Programador y diseñador de sonido: Eduardo Orsi";
            case 2: return "Diseñador de personajes: Eynar Mejia";
            case 3: return "Co-Programador: Kevin De Groote";
            case 4: return "Diseñador: Juan Benito Suarez Dominguez (Lokevas)";
            case 5: return "Apoyo Emocional: Bang (usuario de discord)";
        }
        return "";
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
