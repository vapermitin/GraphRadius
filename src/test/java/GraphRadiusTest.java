import org.junit.jupiter.api.Test;
import ru.leti.wise.task.graph.util.FileLoader;

import java.io.FileNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GraphRadiusTest {

    private final GraphRadius checker = new GraphRadius();

    @Test
    public void singleVertex() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/single_vertex.json");
        assertThat(checker.run(graph)).isEqualTo(0);
    }

    @Test
    public void twoVerticesConnected() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/two_vertices_with_edge.json");
        assertThat(checker.run(graph)).isEqualTo(1);
    }

    @Test
    public void twoVerticesDisconnected() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/two_vertices_without_edge.json");
        assertThat(checker.run(graph)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void pathOfThreeVertices() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/path_3_vertices.json");
        assertThat(checker.run(graph)).isEqualTo(1);
    }

    @Test
    public void pathOfFourVertices() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/path_4_vertices.json");
        assertThat(checker.run(graph)).isEqualTo(2);
    }

    @Test
    public void starGraph() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/star_4_vertices.json");
        assertThat(checker.run(graph)).isEqualTo(1);
    }

    @Test
    public void completeGraphK3() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/complete_graph_K3.json");
        assertThat(checker.run(graph)).isEqualTo(1);
    }

    @Test
    public void undirectedCycleC4() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/undirected_cycle_4.json");
        assertThat(checker.run(graph)).isEqualTo(2);
    }

    @Test
    public void directedCycleC4() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/directed_cycle_4.json");
        assertThat(checker.run(graph)).isEqualTo(3);
    }

    @Test
    public void weightedTriangle() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/weighted_triangle.json");
        assertThat(checker.run(graph)).isEqualTo(2);
    }

    @Test
    public void disconnectedGraphWithTwoComponents() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/disconnected_two_components.json");
        assertThat(checker.run(graph)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void graphWithIsolatedVertex() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/one_edge_and_isolated.json");
        assertThat(checker.run(graph)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void emptyGraph() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/empty_graph.json");
        assertThat(checker.run(graph)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void lineOfFiveVertices() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/path_5_vertices.json");
        assertThat(checker.run(graph)).isEqualTo(2);
    }

    @Test
    public void singleEdgeDirected() throws FileNotFoundException {
        var graph = FileLoader.loadGraphFromJson("src/test/resources/directed_one_way_edge.json");
        assertThat(checker.run(graph)).isEqualTo(1);
    }
}
