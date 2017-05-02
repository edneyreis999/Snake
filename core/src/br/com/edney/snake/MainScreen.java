package br.com.edney.snake;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Desktop on 30/04/2017.
 */

public class MainScreen implements Screen {

    private Game game;
    private Viewport viewport;
    private SpriteBatch batch;

    private Texture[] fundo;

    private float tempoTelaAberta;
    private boolean segurandoToque = false;

    public MainScreen(Game game){
        this.game = game;
    }

    @Override
    public void show() {
        // Criando sprite batch
        batch = new SpriteBatch();

        // criando e aplicando a view port
        viewport = new FillViewport(1000, 1500);
        viewport.apply();

        fundo = new Texture[2];
        fundo[0] = new Texture("fundo0.png");
        fundo[1] = new Texture("fundo1.png");

        tempoTelaAberta = 0f;

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        tempoTelaAberta += delta;

        input();

        // Passa a camera do viewport
        // faz com que ele desenhe a tela virtual e não a tela real
        batch.setProjectionMatrix(viewport.getCamera().combined);

        //limpa a tela
        Gdx.gl.glClearColor(0.29f, 0.894f, 0.373f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        //(int)tempoAnimFundo % 2 => resto da divisão por 2. eu seja, varia entre 0 e 1
        batch.draw(fundo[(int)tempoTelaAberta % 2], 0, 0, 1000, 1500);

        batch.end();

    }

    private void input() {
        // um exemplo de holding
        if(Gdx.input.isTouched()){
            segurandoToque = true;
        }else if(!Gdx.input.isTouched() && segurandoToque){
            // soltei o touch do aparelho
            segurandoToque = false;
            // mudo de tela

            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width ,height ,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
