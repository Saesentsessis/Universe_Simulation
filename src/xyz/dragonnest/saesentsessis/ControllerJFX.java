package xyz.dragonnest.saesentsessis;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ControllerJFX implements Initializable {

    Timeline timeline;

    static ArrayList<Rigidbody2D> rigidbodies;
    private NBodySimulation nbs;
    private CelestialBody[] bodies;
    private Star[] stars;
    static boolean paused = false;
    boolean isRunning = false;
    private int choosenPlanet = -1;
    Color standart = new Color(0.06,0.06,0.06,1);

    @FXML Canvas canvas;
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private Button FScreenStartButton;
    @FXML private Text LogoText;
    @FXML private ImageView LogoImg;

    private int counter;

    @Override public void initialize(URL location, ResourceBundle resources) {
        //com.sun.glass.ui.Robot robot = com.sun.glass.ui.Application.GetApplication().createRobot();
        LogoImg.setOpacity(0);
        LogoText.setOpacity(0);
        FScreenStartButton.setOpacity(0); FScreenStartButton.setDisable(true);
        timeline = new Timeline(new KeyFrame(Duration.millis(20),event -> startApp()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        FScreenStartButton.setOnAction(event -> {
            if (!isRunning) {
                run();
                isRunning = true;
            } else {
                pause();
            }
        });
        canvas.setOnMouseClicked(event -> {
            if (!isRunning) return;
            for (CelestialBody c: bodies) { // выбор планеты мышкой для показа инфы о ней.
                float sqrDistance = Vector2.subtract(new Vector2((float)event.getSceneX(), (float)event.getSceneY()), c.rb.position).sqrMagnitude();
                if (sqrDistance <= (c.radius)*(c.radius)+400) {
                    choosenPlanet = c.rb.instanceId;
                    return;
                }
            }
            choosenPlanet = -1;
        });
    }

    public void startApp() { // анимация первого экрана
        if (counter < 50) {counter++; return;}
        if (LogoImg.getOpacity()<1) {
            LogoImg.setOpacity(LogoImg.getOpacity() + 0.005);
        }
        if (LogoImg.getOpacity()>=0.5) {
            if (LogoText.getOpacity() < 1) {
                LogoText.setOpacity(LogoText.getOpacity()+0.01);
            }
        }
        if (LogoText.getOpacity()>=1) {
            if (FScreenStartButton.getOpacity() < 1) FScreenStartButton.setOpacity(FScreenStartButton.getOpacity()+0.005); else FScreenStartButton.setDisable(false);
        }
    }

    public void run() {
        timeline.stop();
        rigidbodies = new ArrayList<>();
        // задаем константы симуляции. Первая - шаг симуляции(1 - раз в секунду, 0.1 - 10 раз в секунду, стандарт 50 раз в сек).
        // Вторая - универсальная притяжения. Влияет на массу планет и силу притяжения этих же планет.
        Universe.timeStep = 0.02f;
        Universe.gravitationalConstant = 0.0001f;
        Vector<CelestialBody> vector =  new Vector<>();
        // заполняем звезды на заднем фоне
        stars = new Star[150];
        CreateStars();
        // создаем планеты
        RecreateBodies(vector, 2);
        this.bodies = VectorIntoArray(vector); // переводим коллекцию вектор в массив, для удобства работы в других классах
        this.nbs = new NBodySimulation(bodies, bodies[0]);
        timeline = new Timeline(new KeyFrame(Duration.millis(Universe.timeStep*1000), event -> Update()));
        timeline.setCycleCount(Animation.INDEFINITE);
        counter = 0;
        timeline.play();
        FScreenStartButton.setOpacity(0); FScreenStartButton.setDisable(false);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(standart);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
    }

    public void pause() {
        paused = !paused;
    }

    private void CreateStars() { // создает звезды
        Random rnd = new Random();
        for (int i = 0; i < stars.length; i++) {
            float size_opacity = rnd.nextFloat()+1;
            stars[i] = new Star(new Vector2(rnd.nextInt((int)canvas.getWidth()), rnd.nextInt((int)canvas.getHeight())), new Color(1,rnd.nextFloat()/10+0.9,rnd.nextFloat()/10+0.9,size_opacity/2), size_opacity);
        }
    }

    private void Update() { // вызывается каждый Universe.timeStep секунд
        if (!paused) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(standart);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            for (int i = 0; i < stars.length; i++) { // с самого начала рисуем звезды
                stars[i].draw(gc);
            }
            this.nbs.Update(); // обновляем силы притяжения для каждой из планет
            if (counter < 100*Universe.timeStep) {
                counter++;
            } else {
                counter = 0;
            }
            for (CelestialBody c : bodies) { // раз в два(на данный момент) TimeStep'а добавляем новую позицию хвоста.
                if (counter >= 100*Universe.timeStep) {
                    c.TailRenderer().add(c.rb.position);
                }
                c.TailRenderer().draw(gc); // Рисуем хвосты планетам каждый кадр, для большей наглядности куда они двигаются
            }
            for (CelestialBody c : bodies) {
                c.rb.Update(); // обновляем позиции планет
                if (c.rb.instanceId == choosenPlanet) { // если планета выбрана, то рисуем дополнительно инфу о ней, если нет, просто рисуем её и название.
                    c.draw(gc, true, (float)canvas.getWidth(), (float)canvas.getHeight());
                } else {
                    c.draw(gc, false, (float)canvas.getWidth(), (float)canvas.getHeight());
                }
            }

        }
    }

    private CelestialBody[] VectorIntoArray(Vector<CelestialBody> vector) {
        CelestialBody[] bodies = new CelestialBody[vector.size()];
        byte i = 0;
        for (CelestialBody b : vector) {
            System.out.println(i);
            bodies[i] = b;
            i++;
        }
        return bodies;
    }

    private void RecreateBodies(Vector<CelestialBody> bodies, int amount) {
        // хотел сделать тут рандомное заполнение планет, но из этого в симуляции ничего хорошего не получилось, был хаос.
        bodies.clear();
        bodies.add(new CelestialBody(100,25, new Vector2((float)canvas.getWidth() / 2,(float)canvas.getHeight()/2), new Vector2(0,0), "Sun", 0, new Color(1, 1, 0.5, 1), 1));
        rigidbodies.add(bodies.get(0).rb);
        bodies.add(new CelestialBody(10,2f, new Vector2((float)canvas.getWidth()/2,(float)canvas.getHeight()/2-375), new Vector2(-49f,0), "Green Moon", 1, new Color(0.5, 0.5, 0.5, 1) ,200));
        rigidbodies.add(bodies.get(1).rb);
        bodies.add(new CelestialBody(50,15, new Vector2((float)canvas.getWidth()/2,(float)canvas.getHeight()/2-450), new Vector2(-25.5f,0), "Green Planet", 2, new Color(0.5, 1, 0.5, 1), 200));
        rigidbodies.add(bodies.get(2).rb);
        bodies.add(new CelestialBody(5,0.8f, new Vector2((float)canvas.getWidth()/2-150,(float)canvas.getHeight()/2), new Vector2(5,35), "Red twin", 3, new Color(0.7, 0.2, 0.2, 1), 200));
        rigidbodies.add(bodies.get(3).rb);
        bodies.add(new CelestialBody(6,0.9f, new Vector2((float)canvas.getWidth()/2+150,(float)canvas.getHeight()/2), new Vector2(5,-35), "Blue twin", 4, new Color(0.2, 0.4, 0.7, 1), 200));
        rigidbodies.add(bodies.get(4).rb);
    }
}
