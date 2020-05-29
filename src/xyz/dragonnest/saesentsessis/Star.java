package xyz.dragonnest.saesentsessis;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Star {
    Vector2 position;
    Color color;
    float size;

    public Star(Vector2 position, Color color, float size) {
        this.position = position;
        this.color = color;
        this.size = size;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(this.color);
        gc.fillRect(position.x,position.y,size,size);
    }
}
