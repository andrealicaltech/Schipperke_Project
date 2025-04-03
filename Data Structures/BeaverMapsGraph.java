package edu.caltech.cs2.datastructures;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.ISet;

import java.io.FileReader;
import java.io.IOException;


public class BeaverMapsGraph extends Graph<Long, Double> {
    private IDictionary<Long, Location> ids;
    private ISet<Location> buildings;

    public BeaverMapsGraph() {
        super();
        this.buildings = new ChainingHashSet<>();
        this.ids = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    /**
     * Reads in buildings, waypoinnts, and roads file into this graph.
     * Populates the ids, buildings, vertices, and edges of the graph
     * @param buildingsFileName the buildings filename
     * @param waypointsFileName the waypoints filename
     * @param roadsFileName the roads filename
     */
    public BeaverMapsGraph(String buildingsFileName, String waypointsFileName, String roadsFileName) {
        this();

        JsonElement bs = fromFile(buildingsFileName);
        assert bs != null;
        for (JsonElement b : bs.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            Long l = loc.id;
            ids.put(l, loc);
            buildings.add(loc);
            addVertex(l);
            //System.out.println("One of the locations is " + loc);
        }
        JsonElement wps = fromFile(waypointsFileName);
        assert wps != null;
        for (JsonElement wp : wps.getAsJsonArray()) {
            Location loc = new Location(wp.getAsJsonObject());
            Long l = loc.id;
            ids.put(l, loc);
            addVertex(l);
            //System.out.println("One of the locations is " + loc);
        }
        JsonElement rds = fromFile(roadsFileName);
        assert rds != null;
        for (JsonElement rd : rds.getAsJsonArray()) {
            JsonArray road = rd.getAsJsonArray();
            for (int i =0; i < road.size()-1; i++) {
                Location l1 = getLocationByID(road.get(i).getAsLong());
                Location l2 = getLocationByID(road.get(i+1).getAsLong());
                double dist = l1.getDistance(l2);
                addUndirectedEdge(l1.id, l2.id, dist);
            }
            //System.out.println("Connected roads");
        }
    }

    /**
     * Returns a deque of all the locations with the name locName.
     * @param locName the name of the locations to return
     * @return a deque of all location with the name locName
     */
    public IDeque<Location> getLocationByName(String locName) {
        IDeque<Location> deque = new ArrayDeque<>();
        for (Location loc: ids.values()) {
            if (loc.name != null && loc.name.equals(locName)) {
                deque.add(loc);
            }
        }
        return deque;
    }

    /**
     * Returns the Location object corresponding to the provided id
     * @param id the id of the object to return
     * @return the location identified by id
     */
    public Location getLocationByID(long id) {
        return ids.get(id);
    }

    /**
     * Adds the provided location to this map.
     * @param n the location to add
     * @return true if n is a new location and false otherwise
     */
    public boolean addVertex(Location n) {
        for (Long l : ids.keys()) {
            if (l.equals(n.id)) {
                return false;
            }
        }

        if (n.type == Location.Type.BUILDING) {
            buildings.add(n);
        }
        ids.put(n.id, n);
        addVertex(n.id);
        return true;
    }

    /**
     * Returns the closest building to the location (lat, lon)
     * @param lat the latitude of the location to search near
     * @param lon the longitute of the location to search near
     * @return the building closest to (lat, lon)
     */
    public Location getClosestBuilding(double lat, double lon) {
        Location min_loc = null;
        double min_dist = 100000000000.0;

        for (Location loc: ids.values()) {
            if ((loc.getDistance(lat, lon) < min_dist) && buildings.contains(loc)) {
                min_dist = loc.getDistance(lat, lon);
                min_loc = loc;
            }
        }
        return min_loc;
    }

    /**
     * Returns a set of locations which are reachable along a path that goes no further than `threshold` feet from start
     * @param start the location to search around
     * @param threshold the number of feet in the search radius
     * @return all locations within the provided `threshold` feet from start
     */
    public ISet<Location> dfs(Location start, double threshold) {
        ISet<Location> visited = new ChainingHashSet<>();
        ISet<Location> set = new ChainingHashSet<>();
        return dfs_helper(start, start, threshold, visited, set);
    }

    private ISet<Location> dfs_helper(Location start, Location curr, double threshold, ISet<Location> visited, ISet<Location> toReturn) {
        if (visited.contains(curr)) {
            return toReturn;
        }

        visited.add(curr);
        if (curr.getDistance(start) <= threshold) {
            toReturn.add(curr);
            for (Long adj: neighbors(curr.id)) {
                dfs_helper(start, getLocationByID(adj), threshold, visited, toReturn);
            }//recurse on adjacent locations
        }

        return toReturn;
    }

    /**
     * Returns a list of Locations corresponding to
     * buildings in the current map.
     * @return a list of all building locations
     */
    public ISet<Location> getBuildings() {
        return this.buildings;
    }

    /**
     * Returns a shortest path (i.e., a deque of vertices) between the start
     * and target locations (including the start and target locations).
     * @param start the location to start the path from
     * @param target the location to end the path at
     * @return a shortest path between start and target
     */
    public IDeque<Location> dijkstra(Location start, Location target) {
        // TODO (student): Write This
        return null;
    }

    /**
     * Returns a JsonElement corresponding to the data in the file
     * with the filename filename
     * @param filename the name of the file to return the data from
     * @return the JSON data from filename
     */
    private static JsonElement fromFile(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            return null;
        }
    }
}
