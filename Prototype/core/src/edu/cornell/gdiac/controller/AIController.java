package edu.cornell.gdiac.controller;


import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.model.PlanetModel;
import edu.cornell.gdiac.model.ShipModel;
import edu.cornell.gdiac.model.OobModel;

/**
 * Created by Matt Loughney on 3/18/2017.
 */
public class AIController {
    /** list of ships */
    private Array<ShipModel> ships;
    /** hashmap from ship to current planet they're each orbiting */
    private ObjectMap<ShipModel,PlanetModel> targetPlanets;
    /** list of planets in the map */
    private Array<PlanetModel> planets;
    /** the ships that aren't in orbit */
    private ObjectSet<ShipModel> wanderers;
    /** Oob */
    private OobModel avatar;
    /** temp Vector2s */
    private Vector2 tempVec1;
    private Vector2 tempVec2;
    /** how fast a ship moves */
    private static final float MOVE_SPEED = 0.01f;
    /** epsilon for distance from orbiting planet */
    private static final float EPSILON = 0.1f;

    public AIController(Array<ShipModel> ships, Array<PlanetModel> planets, OobModel oob) {
        this.ships = ships;
        this.planets = planets;
        targetPlanets = new ObjectMap<ShipModel, PlanetModel>();
        for(int i = 0; i < ships.size; i++)
            targetPlanets.put(ships.get(i), planets.get(i));
        avatar = oob;
        tempVec1 = new Vector2();
        tempVec2 = new Vector2();
        wanderers = new ObjectSet<ShipModel>();
    }

    /** each ship will move counter-clockwise in orbit around its
     * planet, or towards its planet it it's not in orbit yet
     *
     * @param dt
     */
    public void update(float dt) {
        for(ShipModel s : ships) {
            s.setAggroed(s.getPosition().cpy().sub(avatar.getPosition()).len() <= s.getAggroRange());
            tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()));
            if(s.getAggroed()) {
                aggroPathfind(s);
            }
            else
                peacefulPathfind(s);
        }
    }

    /**
     * move counterclockwise if in orbit, else try to get into orbit of nearest planet
     *
     * @param s
     */
    public void peacefulPathfind(ShipModel s) {
        s.setInOrbit(Math.abs(tempVec1.len() - targetPlanets.get(s).getRadius() - 2) < EPSILON);
        if(s.getInOrbit()) {
            if(wanderers.contains(s))
                wanderers.remove(s);
            tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()));
            tempVec2.set(-tempVec1.y,tempVec1.x);
            tempVec2.scl(MOVE_SPEED/tempVec2.len());
            tempVec1.set(targetPlanets.get(s).getPosition().cpy().add(tempVec1).add(tempVec2));
            tempVec1.scl((targetPlanets.get(s).getMass() + 2)/tempVec1.len());
            s.setPosition(tempVec1);
        }
        else {
            tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()));
            if(tempVec1.len() < targetPlanets.get(s).getMass() + 2) {
                tempVec1.scl(MOVE_SPEED/tempVec1.len());
                s.setPosition(s.getPosition().cpy().add(tempVec1));
            }
            else {
                if(!wanderers.contains(s)) {
                    Vector2 tempVec1 = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
                    int closestPlanet = 0;
                    for (int i = 0; i < planets.size; i++) {
                        tempVec2.set(s.getPosition().cpy().sub(planets.get(i).getPosition()));
                        if (tempVec2.len() - planets.get(i).getRadius() < tempVec1.len()) {
                            tempVec1 = tempVec2.cpy();
                            tempVec1.scl((tempVec1.len() - planets.get(i).getRadius()) / tempVec1.len());
                            closestPlanet = i;
                        }
                    }
                    targetPlanets.put(s, planets.get(closestPlanet));
                    wanderers.add(s);
                }
                tempVec1.set(targetPlanets.get(s).getPosition().cpy().sub(s.getPosition()));
                tempVec1.scl(MOVE_SPEED/tempVec1.len());
                s.setPosition(s.getPosition().cpy().add(tempVec1));
            }
        }
    }

    public void aggroPathfind(ShipModel s) {
        
    }
}
