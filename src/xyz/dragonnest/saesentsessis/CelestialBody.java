package xyz.dragonnest.saesentsessis;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*

    КЛАСС ПЛАНЕТЫ

 */

public class CelestialBody {
    public float radius;
    public float surfaceGravity;
    public Vector2 initialVelocity;
    public float mass;
    String name = "Unnamed";

    Color color;
    Rigidbody2D rb;
    TailRenderer tailRenderer;

    public CelestialBody(float radius, float surfaceGravity, Vector2 position, Vector2 initialVelocity, String name, int id, Color color, int tailSteps) {
        this.radius = radius;
        this.surfaceGravity = surfaceGravity;
        this.initialVelocity = initialVelocity;
        this.mass = surfaceGravity * radius * radius / Universe.gravitationalConstant;
        this.rb = new Rigidbody2D(position, mass, initialVelocity, Rigidbody2D.ForceMode.Acceleration, true, this.radius);
        this.rb.instanceId = id;
        if (!name.equals("")) {
            this.name = name;
        }
        this.color = color;
        this.tailRenderer = new TailRenderer(color, tailSteps, radius);
    }

    public void draw(GraphicsContext gc, boolean choosen, float w, float h) {
        double lineWidth = Math.pow(this.radius, 0.5)-1;
        if (lineWidth < 2) {
            lineWidth = 2;
        }
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(new Font("Papyrus", lineWidth * 5 + 8));
        gc.strokeText(this.name, rb.position.x, rb.position.y - radius / 2 - 10);
        if (choosen) {
            lineWidth /= 2;
            gc.setFont(new Font("Papyrus", lineWidth * 4 + 8));
            gc.strokeText("x: " + round(this.rb.position.x-w/2, 1) + " y: " + round(-this.rb.position.y+h/2, 1), rb.position.x, rb.position.y + radius / 2 + lineWidth * 2 + 10);
            gc.strokeText("vel " + round(this.rb.velocity.x, 1) + " : " + round(-this.rb.velocity.y, 1), rb.position.x, rb.position.y + radius / 2 + lineWidth * 4 + 20);
            lineWidth *= 2;
        }
        if (lineWidth > 3) {lineWidth = 3;}
        if (!choosen) lineWidth/=2;
        gc.setLineWidth(lineWidth);
        gc.setStroke(Color.WHITE);
        gc.setFill(color);
        gc.strokeOval(this.rb.position.x-this.radius/2-1-lineWidth/2, this.rb.position.y-this.radius/2-1-lineWidth/2, this.radius+2+lineWidth, this.radius+2+lineWidth);
        gc.fillOval(this.rb.position.x-this.radius/2, this.rb.position.y-this.radius/2, this.radius, this.radius);
    }

    public void UpdateVelocity (Vector2 acceleration) {
        this.rb.AddForce(Vector2.multiply(acceleration, Universe.timeStep), Rigidbody2D.ForceMode.Acceleration);
    }

    public void UpdatePosition() {
        //this.rb.MovePosition (Vector2.add(this.rb.position, Vector2.multiply(rb.velocity, Universe.timeStep)));
    }

    public TailRenderer TailRenderer() {
        return this.tailRenderer;
    }

    public static double round(double value, int places) { // Метод который округляет числа с плавающей точкой до n-ного колова символов после запятой.
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
