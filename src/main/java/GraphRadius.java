import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class GraphRadius implements GraphCharacteristic {
    Graph graph;
    HashMap<Integer, List<Edge>> neighbours = new HashMap<Integer, List<Edge>>();

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
        int vertexCount = this.graph.getVertexCount();
        int[][] dist = new int[vertexCount][vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                    continue;
                }
                dist[i][j] = Integer.MAX_VALUE;
            }
        }
        for (Edge edge : this.graph.getEdgeList()) {
            dist[edge.getSource()-1][edge.getTarget()-1] = edge.getWeight();
        }
        for (int i = 0; i < vertexCount; i++) {
            for (int firstVertex = 0; firstVertex < vertexCount; firstVertex++) {
                for (int secondVertex = 0; secondVertex < vertexCount; secondVertex++) {
                    if (dist[firstVertex][i] == Integer.MAX_VALUE) continue;;
                    if (dist[i][secondVertex] == Integer.MAX_VALUE) continue;
                    dist[firstVertex][secondVertex] = min(dist[firstVertex][secondVertex], dist[firstVertex][i] + dist[i][secondVertex]);
                }
            }
        }
        return dist;
    }

    private int dijkstra_eccentricity(Integer start) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        HashMap<Integer, Integer> distances = new HashMap<>();
        for (Vertex vertex : this.graph.getVertexList()) {
            distances.put(vertex.getId(), Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(new Node(start, 0));
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            for (Edge neighbour : this.neighbours.get(node.vertex_id)) {
                int newDistance = neighbour.getWeight() + node.distance;
                if (newDistance < distances.get(neighbour.getTarget())) {
                    distances.put(neighbour.getTarget(), newDistance);
                    queue.offer(new Node(neighbour.getTarget(), newDistance));
                }
            }
        }
        int maxDistance = 0;
        for (Map.Entry<Integer, Integer> entry : distances.entrySet()) {
            maxDistance = max(maxDistance, entry.getValue());
        }
        return maxDistance;
    }

    @Override
    public int run(Graph graph) {
        this.graph = graph;
        for (Vertex vertex : this.graph.getVertexList()) {
            this.neighbours.put(vertex.getId(), new ArrayList<>());
        }
        boolean hasNegativeEdges = false;
        for (Edge edge : this.graph.getEdgeList()) {
            if (edge.getWeight() < 0) hasNegativeEdges = true;
            this.neighbours.get(edge.getSource()).add(edge);
        }

        if (!hasNegativeEdges) {
            int minEccentricity = Integer.MAX_VALUE;
            for (Vertex vertex : this.graph.getVertexList()) {
                minEccentricity = min(minEccentricity, this.dijkstra_eccentricity(vertex.getId()));
            }
            return minEccentricity;
        }

        int[][] result_matrix = floyd_warshall();
        int vertexCount = this.graph.getVertexCount();
        int radius = Integer.MAX_VALUE;
        for (int i = 0; i < vertexCount; i++) {
            if (result_matrix[i][i] < 0) return Integer.MIN_VALUE;
            int eccentricity = 0;
            for (int j = 0; j < vertexCount; j++) {
                eccentricity = max(eccentricity, result_matrix[i][j]);
            }
            radius = min(radius, eccentricity);
        }
        return radius;
    }
}
