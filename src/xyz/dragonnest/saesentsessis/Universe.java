package xyz.dragonnest.saesentsessis;

public class Universe {
    static float timeStep = 1f;
    static float gravitationalConstant = 0.001f;

    public Universe(float timeStep, float UGC) {
        Universe.timeStep = timeStep;
        Universe.gravitationalConstant = UGC;
    }
}
