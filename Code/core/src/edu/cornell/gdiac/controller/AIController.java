package edu.cornell.gdiac.controller;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.model.PlanetModel;
import edu.cornell.gdiac.model.ShipModel;
import edu.cornell.gdiac.model.ComplexOobModel;

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
    private ComplexOobModel avatar;
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
    private static final int COOLDOWN = 100;
    /** burst amount */
    private static final int BURSTCOUNT = 5;
    /** burst delay */
    private static final int DELAY = 5;

    public void setTarget(ShipModel ship, PlanetModel planet) {
        targetPlanets.put(ship, planet);
    }

    public void addShip(ShipModel ship, PlanetModel planet){
        ships.add(ship);
        targetPlanets.put(ship, planet);
    }

    public void removePlanet(PlanetModel planet){
        planets.removeValue(planet, true);
    }

    public AIController(Array<ShipModel> ships, Array<PlanetModel> planets, Array<PlanetModel> commandPlanets, ComplexOobModel oob, Vector2 scale) {
        this.scale = scale;
        this.ships = ships;
        this.planets = planets;
        targetPlanets = new ObjectMap<ShipModel, PlanetModel>();
        avatar = oob;
        tempVec1 = new Vector2();
        tempVec2 = new Vector2();
        for(int i = 0; i < ships.size; i++)
            if (ships.get(i).getType() == 2) {
                PlanetModel bigPlanet = planets.get(0);
                for (int j = 0; j < planets.size; j++) {
                    if ((planets.get(j).getRadius() > bigPlanet.getRadius()) && (planets.get(j).getType() != 1)) {
                        bigPlanet = planets.get(j);
                    }
                }
                targetPlanets.put(ships.get(i), bigPlanet);
            }
            else {
                Vector2 tempVec3 = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
                int cloPl = -1;
                for (int j = 0; j < planets.size; j++) {
                    tempVec2.set(ships.get(i).getPosition().cpy().sub(planets.get(j).getPosition()));
                    if (tempVec2.len() - planets.get(j).getRadius() < tempVec3.len()) {
                        tempVec3 = tempVec2.cpy();
                        tempVec3.scl((tempVec3.len() - planets.get(j).getRadius()) / tempVec3.len());
                        cloPl = j;
                    }
                }
                targetPlanets.put(ships.get(i), planets.get(cloPl));
            }
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
            if (s.getType()==0) {
                s.setAggroed(Math.abs(s.getPosition().cpy().sub(avatar.getPosition()).len()) <= s.getAggroRange());
                if (s.getAggroed()) {
                    aggroPathfind(s);
                } else
                    peacefulPathfind(s);

            }
            else if(s.getType()==1){
                peacefulPathfind(s);
                shootInRange(s);
            }
            else if(s.getType() ==2){
                moveToPlanet(s);
                convertInRange(s);
            }
            // set the ship's orientation to the correct angle
            float desiredAngle = (float) (Math.atan2(s.getY() - s.getOldPosition().y, s.getX() - s.getOldPosition().x) - Math.PI / 2);
            if(desiredAngle < 0)
                desiredAngle += (float)(Math.PI * 2);
            float difference = desiredAngle - s.getAngle();
            if(difference < -Math.PI)
                difference += 2*Math.PI;
            else if(difference > Math.PI)
                difference -= 2*Math.PI;
            s.setAngle(s.getAngle() + difference / 10);
            if(s.getAngle() >= Math.PI * 2)
                s.setAngle(s.getAngle() - (float)Math.PI * 2);
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
        s.setInOrbit(Math.abs(tempVec1.len() - targetPlanets.get(s).getRadius() - s.getOrbitDistance()) < EPSILON);
        if (!planets.contains(targetPlanets.get(s), false)) {
            s.setInOrbit(false);
        }
        if(s.getInOrbit()) {
            tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()));
            tempVec2.set(-tempVec1.y,tempVec1.x);
            tempVec2.scl(s.getMoveSpeed()/tempVec2.len());
            tempVec1.set(s.getPosition().cpy().add(tempVec2));
            tempVec1.sub(targetPlanets.get(s).getPosition());
            tempVec1.scl((targetPlanets.get(s).getRadius() + s.getOrbitDistance())/tempVec1.len());
            s.setPosition(targetPlanets.get(s).getPosition().cpy().add(tempVec1));
        }
        else {
            if(tempVec1.len() < s.getOrbitDistance() + targetPlanets.get(s).getRadius()) {
                s.setPosition(s.getPosition().cpy().add(tempVec1.cpy().nor().scl(s.getMoveSpeed())));
            }
            else {
                moveToPlanet(s);
            }
//            tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()));
//            if(tempVec1.len() < targetPlanets.get(s).getRadius() + s.getOrbitDistance()) {
//                tempVec1.scl(s.getMoveSpeed()/tempVec1.len());
//                s.setPosition(s.getPosition().cpy().add(tempVec1));
//            }
//            else {
//                if(!wanderers.contains(s)) {
//                    tempVec1.set(Float.MAX_VALUE, Float.MAX_VALUE);
//                    int closestPlanet = 0;
//                    for (int i = 0; i < planets.size; i++) {
//                        tempVec2.set(s.getPosition().cpy().sub(planets.get(i).getPosition()));
//                        if (tempVec2.len() - planets.get(i).getRadius() < tempVec1.len()) {
//                            tempVec1 = tempVec2.cpy();
//                            tempVec1.scl((tempVec1.len() - planets.get(i).getRadius()) / tempVec1.len());
//                            closestPlanet = i;
//                        }
//                    }
//                    targetPlanets.put(s, planets.get(closestPlanet));
//                    wanderers.add(s);
//                }
//                tempVec1.set(targetPlanets.get(s).getPosition().cpy().sub(s.getPosition()));
//                tempVec1.scl(s.getMoveSpeed()/tempVec1.len());
//                s.setPosition(s.getPosition().cpy().add(tempVec1));
//            }
        }
    }

    public void findBigPlanet(ShipModel s) {
        PlanetModel bigPlanet = planets.get(0);
        for (int j = 0; j < planets.size; j++) {
            if ((planets.get(j).getRadius() > bigPlanet.getRadius()) && (planets.get(j).getType() != 1)) {
                bigPlanet = planets.get(j);
            }
        }
        targetPlanets.put(s, bigPlanet);
    }

    /**
     * try to get into orbit of target planet
     *
     * @param s
     */
    public void moveToPlanet(ShipModel s) {
        tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()).scl(-1));
        s.setInOrbit(Math.abs(tempVec1.len() - targetPlanets.get(s).getRadius() - s.getOrbitDistance()) < EPSILON);
        if (!planets.contains(targetPlanets.get(s), false)) {
            s.setInOrbit(false);
        }
        if(s.getInOrbit()) {
            peacefulPathfind(s);
        }
        else {
            int cloPl = -1;
            for (int i = 0; i < planets.size; i++) {
                tempVec2.set(s.getPosition().cpy().sub(planets.get(i).getPosition()));
                if (tempVec2.len() < planets.get(i).getRadius() + 1.5f + EPSILON ) {
                    cloPl = i;
                }
            }
            // at this point cloPl is the nearest planet we might hit
            if(cloPl == -1) {
                tempVec1.scl(s.getMoveSpeed()/tempVec1.len());
                s.setPosition(s.getPosition().cpy().add(tempVec1));
            }
            else {
                PlanetModel target = planets.get(cloPl);
                // tempVec1 is planet to ship
                tempVec1.set(s.getPosition().cpy().sub(target.getPosition()));
                tempVec2.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()).scl(-1));
                if(tempVec1.len() - 1.5f - planets.get(cloPl).getRadius() < EPSILON && tempVec1.dot(tempVec2.cpy().scl(-1)) > 0) {
                    tempVec2.set(-tempVec1.y, tempVec1.x);
                    tempVec2.scl(s.getMoveSpeed() / tempVec2.len());
                    tempVec1.set(s.getPosition().cpy().add(tempVec2));
                    tempVec1.sub(target.getPosition());
                    tempVec1.scl((target.getRadius() + 1.5f) / tempVec1.len());
                    s.setPosition(target.getPosition().cpy().add(tempVec1));
                }
                else if(tempVec1.len() < 1.5f + planets.get(cloPl).getRadius()){
                    s.setPosition(s.getPosition().cpy().add(tempVec1.nor().scl(s.getMoveSpeed())));
                }
                else {
                    tempVec1.set(s.getPosition().cpy().sub(targetPlanets.get(s).getPosition()).scl(-1));
                    tempVec1.scl(s.getMoveSpeed()/tempVec1.len());
                    s.setPosition(s.getPosition().cpy().add(tempVec1));
                }
            }
        }
    }

    //Shoots if the ship is in range of oob
    public void shootInRange(ShipModel s){
        if (s.getType() != 2) {
            tempVec1.set(avatar.getPosition().cpy().sub(s.getPosition()));
            if (s.getCooldown() == 0) {
                if (s.getBurstCount() != 0) {
                    if (s.getDelay() == 0) {
                        if (tempVec1.len() <= s.getAggroRange()) {
                            tempVec1.scl(1f / tempVec1.len());
                            bulletData.add(s.getX() + tempVec1.x);
                            bulletData.add(s.getY() + tempVec1.y);
                            bulletData.add(tempVec1.x * 10);
                            bulletData.add(tempVec1.y * 10);
                        }
                        s.decBurstCount();
                        s.setDelay(DELAY);
                    } else {
                        s.decDelay();
                    }
                } else {
                    s.setCooldown(COOLDOWN);
                    s.setBurstCount(BURSTCOUNT);
                }
            } else {
                s.decCooldown();
            }
        }
    }

    // Convert if ship is in range
    public void convertInRange(ShipModel s){
        if (s.getInOrbit()) {
            if (targetPlanets.get(s).getType() != 1) {
                targetPlanets.get(s).convert();
            }
        }
    }

    public void aggroPathfind(ShipModel s) {
        tempVec1.set(avatar.getPosition().cpy().sub(s.getPosition()));
        shootInRange(s);
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
            if(tempVec1.len() <= planets.get(closestPlanet).getRadius() + s.getOrbitDistance() && Math.abs(vecAngle) < Math.PI / 2) {
                float crossProd = tempVec1.x*tempVec2.y - tempVec1.y*tempVec2.x;
                if(crossProd > 0) {
                    tempVec2.set(-tempVec1.y,tempVec1.x);
                    tempVec2.scl(s.getMoveSpeed()/tempVec2.len());
                    tempVec1.set(s.getPosition().cpy().add(tempVec2));
                    tempVec1.sub(planets.get(closestPlanet).getPosition());
                    tempVec1.scl((planets.get(closestPlanet).getRadius() + s.getOrbitDistance())/tempVec1.len());
                    s.setPosition(planets.get(closestPlanet).getPosition().cpy().add(tempVec1));
                }
                else {
                    tempVec2.set(tempVec1.y,-tempVec1.x);
                    tempVec2.scl(s.getMoveSpeed()/tempVec2.len());
                    tempVec1.set(s.getPosition().cpy().add(tempVec2));
                    tempVec1.sub(planets.get(closestPlanet).getPosition());
                    tempVec1.scl((planets.get(closestPlanet).getRadius() + s.getOrbitDistance())/tempVec1.len());
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
        }
    }

    public void removeShip(ShipModel s) {
        ships.removeValue(s, true);
    }
}
