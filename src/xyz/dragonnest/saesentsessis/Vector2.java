package xyz.dragonnest.saesentsessis;

/*

    ЭТО КАК POINT ТОЛЬКО ЛУЧШЕ, СО ВСЕМИ МЕТОДАМИ ДЛЯ ВЫЧИСЛЕНИЙ

*/

public class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x; this.y = y;
    }

    public Vector2() {
        this.x = one().x;
        this.y = one().y;
    }

    public Vector2 zero() {
        return  new Vector2(0,0);
    }

    public Vector2 one() {
        return new Vector2(1,1);
    }

    public void Translate(Vector2 direction) {
        this.x += direction.x;
        this.y += direction.y;
    }

    public Vector2 normalize(Vector2 vector) {
        double x = vector.x, y = vector.y, subdivider = Math.sqrt(Math.abs(x)*Math.abs(x)+Math.abs(y)*Math.abs(y));
        if (subdivider == 0) return new Vector2().zero();
        x/=subdivider; y/=subdivider;
        return new Vector2((float)x,(float)y);
    }

    public Vector2 normalize() {
        return this.normalize(this);
    }

    public float sqrMagnitude(Vector2 direction) {
        double result = Math.abs(direction.x*direction.x)+Math.abs(direction.y*direction.y);
        return (float)result;
    }

    public float sqrMagnitude() {
        return sqrMagnitude(this);
    }

    public static Vector2 add(Vector2 first, Vector2 second) {
        return new Vector2(first.x+second.x,first.y+second.y);
    }

    public static Vector2 subtract(Vector2 first, Vector2 second) {
        return new Vector2(first.x-second.x,first.y-second.y);
    }

    public static Vector2 multiply(Vector2 vector, float number) {
        if (number < 0) {
            number = Math.abs(number);
            return new Vector2(-(vector.x*number),-(vector.y*number));
        } else {
            return new Vector2((vector.x*number),(vector.y*number));
        }
    }
}
