package br.com.edney.snake;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

/**
 * Created by Desktop on 30/04/2017.
 */

public class GameScreen implements Screen, GestureDetector.GestureListener{

    private Game game;
    private Viewport viewport;
    private SpriteBatch batch;

    private Texture texCorpo;
    private Texture texFundo;
    private Texture texPontos;

    private boolean[][] corpo;
    private Array<Vector2> partes;
    private Array<Vector2> pontos;
    private float timeToNextPonto;

    private Random rand;

    private Vector2 toque;
    private int direcao; // 1-cima 2-direita 3-baixo 4-esquerda

    private float timeToMove;

    private int estado;


    public GameScreen(Game game){
        this.game = game;
    }

    private void init() {
        corpo = new boolean[20][20];
        partes = new Array<Vector2>();
        pontos = new Array<Vector2>();

        partes.add(new Vector2(6,5));
        corpo[6][5] = true;

        partes.add(new Vector2(5,5));
        corpo[5][5] = true;

        toque = new Vector2();

        direcao = 2;

        timeToMove = 0.4f;
        timeToNextPonto = 3.0f;

        rand = new Random();
        estado = 0;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        viewport = new FitViewport(100, 100);
        viewport.apply();

        gerarTextura();
        gerarTexturaFundo();
        gerarTexturaPontuacao();

        init();

        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void render(float delta) {
        update(delta);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        //limpa a tela
        Gdx.gl.glClearColor(0.29f, 0.894f, 0.373f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.draw(texFundo, 0, 0 , 100, 100);

        // desenha as partes do corpo da cobra
        for (Vector2 parte:partes) {
            batch.draw(texCorpo, parte.x*5, parte.y*5, 5, 5);
        }

        // desenha os pontos
        for (Vector2 ponto:pontos) {
            batch.draw(texPontos, ponto.x*5, ponto.y*5, 5, 5);
        }

        batch.end();
    }

    private void update(float delta) {
        if(estado == 0){
            timeToMove -= delta;
            timeToNextPonto -= delta;

            if(timeToNextPonto <= 0){
                int x = rand.nextInt(20);
                int y = rand.nextInt(20);

                if(!corpo[x][y]){
                    pontos.add(new Vector2(x, y));
                    timeToNextPonto = 5f;
                }
            }

            if(timeToMove <= 0){
                timeToMove = 0.4f;
                Gdx.app.log("Log", "move");

                int xPosAtual, xPosAtualAux, yPosAtual, yPosAtualAux;

                xPosAtual = (int)partes.get(0).x;
                yPosAtual = (int)partes.get(0).y;
                corpo[xPosAtual][yPosAtual] = false;

                xPosAtualAux = xPosAtual;
                yPosAtualAux = yPosAtual;

                switch (direcao){
                    case 1:
                        yPosAtual++;
                        break;
                    case 2:
                        xPosAtual++;
                        break;
                    case 3:
                        yPosAtual--;
                        break;
                    case 4:
                        xPosAtual--;
                        break;
                }

                if(xPosAtual < 0 || xPosAtual > 19 || yPosAtual < 0 || yPosAtual > 19 || corpo[xPosAtual][yPosAtual]){
                    //perdemos
                    estado = 1;
                    return;
                }

                for (int i = 0; i < pontos.size; i++) {
                    if(pontos.get(i).x == xPosAtual && pontos.get(i).y == yPosAtual){
                        // Adiciona um pedaço de corpo na frente e não movimenta o corpo.
                        pontos.removeIndex(i);
                        partes.insert(0, new Vector2(xPosAtual, yPosAtual));
                        corpo[xPosAtual][yPosAtual] = true;
                        corpo[xPosAtualAux][yPosAtualAux] = true;

                        return;
                    }
                }

                partes.get(0).set(xPosAtual, yPosAtual);
                corpo[xPosAtual][yPosAtual] = true;

                for (int i = 1; i < partes.size; i++) {
                    xPosAtual = (int)partes.get(i).x;
                    yPosAtual = (int)partes.get(i).y;
                    corpo[xPosAtual][yPosAtual] = false;

                    partes.get(i).set(xPosAtualAux, yPosAtualAux);
                    corpo[xPosAtualAux][yPosAtualAux] = true;

                    xPosAtualAux = xPosAtual;
                    yPosAtualAux = yPosAtual;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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

    private void gerarTexturaPontuacao() {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGB888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fillCircle(32, 32, 32);

        texPontos = new Texture(pixmap);

        pixmap.dispose();
    }

    private void gerarTexturaFundo() {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGB888);
        pixmap.setColor(0.29f, 0.784f, 0.373f, 0.5f);
        pixmap.fillRectangle(0, 0, 64, 64);

        texFundo = new Texture(pixmap);

        pixmap.dispose();
    }

    private void gerarTextura() {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGB888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fillRectangle(0, 0, 64, 64);

        texCorpo = new Texture(pixmap);

        pixmap.dispose();
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        viewport.unproject(toque.set(velocityX, velocityY));
        Gdx.app.log("Log", velocityX + " " + velocityY + " " + toque.x + " " + toque.y);

        if(estado == 0){
            if(Math.abs(toque.x) > Math.abs(toque.y)){
                toque.y = 0;
            }else{
                toque.x = 0;
            }
            if(toque.x > 50 && direcao != 4){
                direcao = 2;
            }else if(toque.y > 50 && direcao != 3){
                direcao = 1;
            }else if(toque.x < -50 && direcao != 2){
                direcao = 4;
            }else if(toque.y < -50 && direcao != 1){
                direcao =3;
            }
        }
        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if(estado == 1){
            game.setScreen(new MainScreen(game));
        }
        return true;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
