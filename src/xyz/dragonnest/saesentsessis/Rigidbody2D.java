package xyz.dragonnest.saesentsessis;

import java.util.ArrayList;

/*

    КЛАСС ОТВЕЧАЮЩИЙ ЗА ВСЮ 2Д ФИЗИКУ

*/

public class Rigidbody2D {
    Vector2 position;
    Vector2 velocity;
    float speed;
    float mass;
    float radius;
    boolean collide;

    public int instanceId;

    byte counter = 0;

    public Rigidbody2D(Vector2 position, float mass, Vector2 initialVelocity, ForceMode forceMode, boolean canCollide, float radius) {
        this.position = position;
        this.mass = mass;
        switch (forceMode) {
            case Impulse: {
                this.velocity = new Vector2(initialVelocity.x/this.mass, initialVelocity.y/this.mass);
                break;
            }
            case Acceleration: {
                this.velocity = initialVelocity;
                break;
            }
        }
        Speed();
        this.collide = canCollide;
        this.radius = radius;
    }

    public void AddForce(Vector2 direction, ForceMode forceMode) {
        switch (forceMode) {
            case Impulse: {
                this.velocity = Vector2.add(this.velocity, new Vector2(direction.x/this.mass, direction.y/this.mass));
                break;
            }
            case Acceleration: {
                this.velocity = Vector2.add(this.velocity, direction);
                break;
            }
        }
    }

    public void MovePosition(Vector2 newPosition) {
        this.position = newPosition;
    }

    public void SetMass(float mass) {
        this.mass = mass;
    }

    public void ToggleCollisions() {
        this.collide = !this.collide;
    }

    public void Update() {
        this.position.Translate(Vector2.multiply(this.velocity, Universe.timeStep));
        if (this.collide) CollisionDetection();
        Speed();
        if (counter < 50) {counter++; return;}
        counter = 0;
        //System.out.println(this.position.x + ", " + this.position.y + " velocity:" + this.velocity.x + ", " + this.velocity.y + " speed:" +this.speed + " mass:" + this.mass);
    }

    private void CollisionDetection() { // хотел сделать коллизии, уже есть нахождение пересечений, но нету действий которые бы делались после того как пересечения найдутся.
        ArrayList<Rigidbody2D> bodies = ControllerJFX.rigidbodies;
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = 0; j < bodies.size(); j++) {
                if (bodies.get(i).instanceId != bodies.get(j).instanceId) {
                    float distance = Vector2.subtract(bodies.get(i).position,bodies.get(j).position).sqrMagnitude();
                    float radiuses = bodies.get(i).radius*bodies.get(j).radius + bodies.get(i).radius*bodies.get(j).radius;
                    if (radiuses > distance) {
                        System.out.println("Collision between " + bodies.get(i).instanceId + " " + bodies.get(j).instanceId + " sqr distance: " + distance + " radiuses sqr: " + radiuses);
                    }
                }
            }
        }
    }

    private void Speed() {
        this.speed = Math.abs(this.velocity.x) + Math.abs(this.velocity.y);
    }

    enum ForceMode {
        Impulse,
        Acceleration
    }
}