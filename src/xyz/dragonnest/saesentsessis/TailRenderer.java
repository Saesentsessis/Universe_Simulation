package xyz.dragonnest.saesentsessis;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/*

    КЛАСС ОТРИСОВКИ ХВОСТА

 */

public class TailRenderer {
    private Vector2[] positions;
    private int steps, maxStep;
    private Color defaultColor;
    private float radius;

    public TailRenderer(Color defaultColor, int steps, float radius) {
        this.steps = steps;
        this.defaultColor = defaultColor;
        this.radius = radius;
        positions = new Vector2[steps];
        maxStep = 0;
    }

    public void add(Vector2 pos) {
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == null) {
                positions[i] = new Vector2(pos.x, pos.y);
                maxStep++;
                return;
            }
        }
        Vector2[] buff = new Vector2[steps];
        for (int i = 0; i < positions.length-1; i++) {
            buff[i] = positions[i+1];
        }
        positions = buff;
        positions[positions.length-1] = new Vector2(pos.x, pos.y);
    }

    public void draw(GraphicsContext gc) { // Тут очень много вычислений, для красивой отрисовки хвоста, чтобы он плавно сужался к концу, и менял цвет тоже плавно
        // также я немного оптимизировал код, заменив отрисовку только кругов на отрисовку линиями. Выглядело это не очень красиво, особенно когда линия рисовалась
        // на прошлой позиции планеты. Исправил тем, что ближайшие 3 процента шагов отрисовки рисуются кругами, остальное линиями.
        Color addStep = new Color((defaultColor.getRed()-0.05) / (steps*1.5), (defaultColor.getGreen()-0.05) / (steps*1.5), (defaultColor.getBlue()-0.05) / (steps*1.5),1);
        Color buffer = new Color(0.05,0.05,0.05,1);
        for (int i = 0; i < positions.length-1; i++) {
            if (positions[i] != null && positions[i+1] != null) {
                float multiplier = steps / (float) (1 + i);
                buffer = new Color(buffer.getRed() + addStep.getRed(), buffer.getGreen() + addStep.getGreen(), buffer.getBlue() + addStep.getBlue(), 1);
                if (i < 0.95 * maxStep+1) {
                    gc.setStroke(buffer);
                    float lineWidth = radius / multiplier * 0.97f;
                    if (lineWidth < 0.01f) lineWidth = 0.01f;
                    gc.setLineWidth(lineWidth);
                    gc.strokeLine(positions[i].x, positions[i].y, positions[i + 1].x, positions[i + 1].y);
                } else {
                    gc.setFill(buffer);
                    gc.fillOval(positions[i].x - radius / multiplier / 2, positions[i].y - radius / multiplier / 2, radius / multiplier, radius / multiplier);
                }
            }
        }
        gc.setStroke(Color.WHITE);
    }
}
