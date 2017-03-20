package edu.cornell.gdiac.controller;


import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.model.BulletModel;
import edu.cornell.gdiac.model.PlanetModel;
import edu.cornell.gdiac.model.ShipModel;
import edu.cornell.gdiac.model.OobModel;
import edu.cornell.gdiac.model.obstacle.Obstacle;
import edu.cornell.gdiac.util.PooledList;

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
    /** All the objects in the world. */
    protected Array<Float> bulletData = new Array<Float>();
    /** Oob */
    private OobModel avatar;
    /** draw scale */
    private Vector2 scale;
    /** temp Vector2s */
    private Vector2 tempVec1;
    private Vector2 tempVec2;
    private Vector2 oldPosition;
    private Vector2 newPosition;
    /** epsilon for distance from orbiting planet */
    private static final float EPSILON = 0.1f;
    /** firing cooldown time */
    private static final int COOLDOWN = 60;
    /** distance from orbiting planet */
    private static final float ORBIT_DISTANCE = 3.0f;

    public AIController(Array<ShipModel> ships, Array<PlanetModel> planets, OobModel oob, Vector2 scale) {
        this.scale = scale;
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
        if(ships.size == 0)
            return;
        for(ShipModel s : ships) {
            s.setAggroed(Math.abs(s.getPosition().cpy().sub(avatar.getPosition()).len()) <= s.getAggroRange());
            if(s.getAggroed()) {
                aggroPathfind(s);
            }
            else
                peacefulPathfind(s);
            s.setAngle((float)(Math.atan2(s.getY() - s.getOldPosition().y, s.getX() - s.getOldPosition().x) - Math.PI/2));
            s.setOldPosition(s.getPosition());
        }
    }

    /**
     * move counterclockwise if in orbit, else try to get into orbit of nearest planet
     *
     * @param s
     */
    public void peacefulPathfind(ShipModel s) {
        tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()));
        s.setInOrbit(Math.abs(tempVec1.len() - targetPlanets.get(s).getRadius() - ORBIT_DISTANCE) < EPSILON);
        if(s.getInOrbit()) {
            if(wanderers.contains(s))
                wanderers.remove(s);
            tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()));
            tempVec2.set(-tempVec1.y,tempVec1.x);
            tempVec2.scl(s.getMoveSpeed()/tempVec2.len());
            tempVec1.set(s.getPosition().cpy().add(tempVec2));
            tempVec1.sub(targetPlanets.get(s).getPosition());
            tempVec1.scl((targetPlanets.get(s).getRadius() + ORBIT_DISTANCE)/tempVec1.len());
            s.setPosition(targetPlanets.get(s).getPosition().cpy().add(tempVec1));
        }
        else {
            tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()));
            if(tempVec1.len() < targetPlanets.get(s).getRadius() + ORBIT_DISTANCE) {
                tempVec1.scl(s.getMoveSpeed()/tempVec1.len());
                s.setPosition(s.getPosition().cpy().add(tempVec1));
            }
            else {
                if(!wanderers.contains(s)) {
                    tempVec1.set(Float.MAX_VALUE, Float.MAX_VALUE);
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
                tempVec1.scl(s.getMoveSpeed()/tempVec1.len());
                s.setPosition(s.getPosition().cpy().add(tempVec1));
            }
        }
    }

    public void aggroPathfind(ShipModel s) {
        tempVec1.set(avatar.getPosition().cpy().sub(s.getPosition()));
        if(s.getCooldown() == 0) {
            tempVec1.scl(1f/tempVec1.len());
            bulletData.add(s.getX() + tempVec1.x);
            bulletData.add(s.getY() + tempVec1.y);
            bulletData.add(tempVec1.x*10);
            bulletData.add(tempVec1.y*10);
            s.setCooldown(COOLDOWN);
        }
        else
            s.decCooldown();
        // moving towards Oob
        if(tempVec1.len() > 4) {
            tempVec1.set(Float.MAX_VALUE, Float.MAX_VALUE);
            int closestPlanet = 0;
            for (int i = 0; i < planets.size; i++) {
                tempVec2.set(planets.get(i).getPosition().cpy().sub(s.getPosition()));
                if (tempVec2.len() - planets.get(i).getRadius() < tempVec1.len()) {
                    tempVec1 = tempVec2.cpy();
                    tempVec1.scl((tempVec1.len() - planets.get(i).getRadius()) / tempVec1.len());
                    closestPlanet = i;
                }
            }
            tempVec2.set(avatar.getPosition().cpy().sub(s.getPosition()));
            tempVec1.set(planets.get(closestPlanet).getPosition().cpy().sub(s.getPosition()));
            // tempVec2 is ship to Oob, tempVec1 is ship to planet
            float vecAngle = (float)Math.acos(tempVec1.dot(tempVec2)/(tempVec1.len()*tempVec2.len()));
            // orbiting around a planet
            if(tempVec1.len() <= planets.get(closestPlanet).getRadius() + ORBIT_DISTANCE/2 && Math.abs(vecAngle) < Math.PI / 2) {
                float crossProd = tempVec1.x*tempVec2.y - tempVec1.y*tempVec2.x;
                if(crossProd > 0) {
                    tempVec2.set(-tempVec1.y,tempVec1.x);
                    tempVec2.scl(s.getMoveSpeed()/tempVec2.len());
                    tempVec1.set(s.getPosition().cpy().add(tempVec2));
                    tempVec1.sub(planets.get(closestPlanet).getPosition());
                    tempVec1.scl((planets.get(closestPlanet).getRadius() + ORBIT_DISTANCE/2)/tempVec1.len());
                    s.setPosition(planets.get(closestPlanet).getPosition().cpy().add(tempVec1));
                }
                else {
                    tempVec2.set(tempVec1.y,-tempVec1.x);
                    tempVec2.scl(s.getMoveSpeed()/tempVec2.len());
                    tempVec1.set(s.getPosition().cpy().add(tempVec2));
                    tempVec1.sub(planets.get(closestPlanet).getPosition());
                    tempVec1.scl((planets.get(closestPlanet).getRadius() + ORBIT_DISTANCE/2)/tempVec1.len());
                    s.setPosition(planets.get(closestPlanet).getPosition().cpy().add(tempVec1));
                }
            }
            // moving in space
            else {
                tempVec2.scl(s.getMoveSpeed()*20/tempVec2.len());
                s.setVX(tempVec2.x);
                s.setVY(tempVec2.y);
            }
        }
        // slowing down to not ram into Oob
        else {
            s.setVX(s.getVX()*0.75f);
            s.setVY(s.getVY()*0.75f);
            s.setAngle((float)(Math.atan2(s.getVY(), s.getVX()) - Math.PI/2));
        }
    }

    public void removeShip(ShipModel s) {
        ships.removeValue(s, true);
    }
}
