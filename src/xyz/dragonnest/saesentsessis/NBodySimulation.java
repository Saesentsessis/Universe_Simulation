package xyz.dragonnest.saesentsessis;

/*

    СЕРДЦЕ СИМУЛЯЦИИ КОСМИЧЕСКИХ ТЕЛ

 */

public class NBodySimulation {
    private CelestialBody[] bodies;
    private CelestialBody centralBody;

    public NBodySimulation(CelestialBody[] bodies, CelestialBody centralBody) {
        this.bodies = bodies;
        this.centralBody = centralBody;
    }

    public void Update() {
        //System.out.println("Update NBodySimulation");
        for (CelestialBody celestialBody : bodies) {
            if (!celestialBody.name.equals(centralBody.name)) {
                //System.out.println(celestialBody.name);
                Vector2 acceleration = CalculateAcceleration(celestialBody.rb.position);
                celestialBody.UpdateVelocity(acceleration);
            }
        }

        for (CelestialBody body : bodies) {
            body.UpdatePosition();
        }
    }

    public Vector2 CalculateAcceleration (Vector2 point) {
        Vector2 acceleration = new Vector2().zero();
        for (CelestialBody body : bodies) {
            float sqrDst = (Vector2.subtract(body.rb.position, point)).sqrMagnitude();
            if (sqrDst != 0) {
                Vector2 forceDir = (Vector2.subtract(body.rb.position, point)).normalize();
                acceleration = Vector2.add(acceleration, Vector2.multiply(forceDir, Universe.gravitationalConstant * body.mass / sqrDst));
            }
        }
        //System.out.println(acceleration.x + " - x, y - " + acceleration.y);
        return acceleration;
    }

    public CelestialBody[] GetBodies() {
        return bodies;
    }
}
