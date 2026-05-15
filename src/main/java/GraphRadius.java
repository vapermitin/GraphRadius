import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class GraphRadius implements GraphProperty {
    private Graph graph;
    private Map<Integer, List<Edge>> neighbours = new HashMap<>();
    private Map<Integer, Integer> idToIndex;

    static class Node implements Comparable<Node> {
        int vertex_id;
        int distance;

        Node(int vertex_id, int distance) {
            this.vertex_id = vertex_id;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    private int[][] floyd_warshall() {
        int vertexCount = graph.getVertexCount();
        int[][] dist = new int[vertexCount][vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
            dist[i][i] = 0;
        }

        for (Edge edge : graph.getEdgeList()) {
            int srcIdx = idToIndex.get(edge.getSource());
            int tgtIdx = idToIndex.get(edge.getTarget());
            dist[srcIdx][tgtIdx] = edge.getWeight() == 0 ? 1 : edge.getWeight();
        }

        for (int k = 0; k < vertexCount; k++) {
            for (int i = 0; i < vertexCount; i++) {
                if (dist[i][k] == Integer.MAX_VALUE) continue;
                for (int j = 0; j < vertexCount; j++) {
                    if (dist[k][j] == Integer.MAX_VALUE) continue;
                    int newDist = dist[i][k] + dist[k][j];
                    if (newDist < dist[i][j]) {
                        dist[i][j] = newDist;
                    }
                }
            }
        }
        return dist;
    }

    private int dijkstra_eccentricity(Integer start) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Map<Integer, Integer> distances = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            distances.put(vertex.getId(), Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(new Node(start, 0));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            List<Edge> edges = neighbours.get(node.vertex_id);
            if (edges == null) continue;
            for (Edge neighbour : edges) {
                int edge_weight = neighbour.getWeight() == 0 ? 1 : neighbour.getWeight();
                int newDistance = edge_weight + node.distance;
                Integer currentDist = distances.get(neighbour.getTarget());
                if (currentDist != null && newDistance < currentDist) {
                    distances.put(neighbour.getTarget(), newDistance);
                    queue.offer(new Node(neighbour.getTarget(), newDistance));
                }
            }
        }

        int maxDistance = 0;
        for (Integer dist : distances.values()) {
            maxDistance = max(maxDistance, dist);
        }
        return maxDistance;
    }

    @Override
    public boolean run(Graph graph) {
        this.graph = graph;
        for (Edge edge : this.graph.getEdgeList()) {
            int temp_id = edge.getSource();
            edge.setSource(edge.getTarget());
            edge.setTarget(temp_id);
        }

        neighbours.clear();
        idToIndex = new HashMap<>();
        List<Vertex> vertexList = graph.getVertexList();
        for (int i = 0; i < vertexList.size(); i++) {
            idToIndex.put(vertexList.get(i).getId(), i);
        }

        for (Vertex vertex : vertexList) {
            neighbours.put(vertex.getId(), new ArrayList<>());
        }

        boolean hasNegativeEdges = false;
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getWeight() < 0) hasNegativeEdges = true;
            List<Edge> srcEdges = neighbours.get(edge.getSource());
            if (srcEdges != null) {
                srcEdges.add(edge);
            }
        }

        if (!hasNegativeEdges) {
            int minEccentricity = Integer.MAX_VALUE;
            for (Vertex vertex : vertexList) {
                minEccentricity = min(minEccentricity, dijkstra_eccentricity(vertex.getId()));
            }
            return minEccentricity == 2;
        }

        int[][] resultMatrix = floyd_warshall();
        int vertexCount = graph.getVertexCount();
        int radius = Integer.MAX_VALUE;
        for (int i = 0; i < vertexCount; i++) {
            if (resultMatrix[i][i] < 0) return false;
            int eccentricity = 0;
            for (int j = 0; j < vertexCount; j++) {
                eccentricity = max(eccentricity, resultMatrix[i][j]);
            }
            radius = min(radius, eccentricity);
        }
        return radius == 2;
    }
}
