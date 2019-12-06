package day6;

import java.io.FileReader;
import java.util.*;

public class Day6 {

    private static String resourceDirectory = "src/main/resources/";
    public static String inputFile = resourceDirectory + "day6/day6Input.txt";
    public static String exampleFilePart1 = resourceDirectory + "day6/day6Example.txt";
    public static String exampleFilePart2 = resourceDirectory + "day6/day6ExamplePart2.txt";


    public static void main (String[] args) throws Exception {
        Scanner scanner = new Scanner(new FileReader(inputFile));

        Tree<String> tree = new Tree<>();
        HashMap<String, Tree.Node> nodes = new HashMap<>();
        while (scanner.hasNext()) {
            String[] orbitingString = scanner.nextLine().split("\\)");
            String orbitee = orbitingString[0];
            String orbiter = orbitingString[1];

            Tree.Node orbiteeNode = nodes.getOrDefault(orbitee, new Tree.Node<>(orbitee));
            Tree.Node orbiterNode = nodes.getOrDefault(orbiter, new Tree.Node<>(orbiter));

            orbiteeNode.getChildren().add(orbiterNode);
            orbiterNode.setParent(orbiteeNode);

            if (!tree.hasRoot() || tree.isRoot(orbiterNode)) {
                tree.setRoot(orbiteeNode);
            }

            nodes.putIfAbsent(orbitee, orbiteeNode);
            nodes.putIfAbsent(orbiter, orbiterNode);
        }
        System.out.println("num orbits : " + calculateNumberOfOrbits(nodes));
        calculateHopsBetweenNodesXAndYOrbit(nodes, "YOU", "SAN");
    }

    /** Find the part 2 solution.**/
    private static void calculateHopsBetweenNodesXAndYOrbit(HashMap<String, Tree.Node> nodes, String x, String y) {
        Tree.Node node1 = nodes.get(x);
        Tree.Node node2 = nodes.get(y);

        if(node1 != null && node2 != null) {
            Set<Tree.Node> set1 = findTotalOrbitSetOfANode(node1);
            Set<Tree.Node> set2 = findTotalOrbitSetOfANode(node2);
            int sizeOfSet1 = set1.size();
            int sizeOfSet2 = set2.size();

            set1.retainAll(set2);
            int sizeOfIntersection = set1.size();
            int numOfHopsBetween = sizeOfSet1 + sizeOfSet2 - (2*sizeOfIntersection);

            System.out.println("Number Of Hops to unit " + x + " and " + y + ": " + numOfHopsBetween);
        } else {
            System.out.println("One node of : " + x + " or " + y + " Not Found");
        }
    }

    private static Set<Tree.Node> findTotalOrbitSetOfANode(Tree.Node node) {
        Set<Tree.Node> set = new HashSet<>();
        while (node.parent != null) { //Really assuming the tree has no cycles here.
            set.add(node.parent);
            node = node.parent;
        }
        return set;
    }

    /** part 1 solution **/
    private static int calculateNumberOfOrbits(HashMap<String, Tree.Node> nodes) {
        int total = 0;
        for(Tree.Node node : nodes.values()) {
            Tree.Node currentNode = node;
            while(currentNode.parent != null) {
                total++;
                currentNode = currentNode.parent;
            }
        }
        return total;
    }

    private static class Tree<T> {
        private Node<T> root;

        public Tree() {}

        public boolean hasRoot() { return root != null; }

        public boolean isRoot(Node<T> node) { return node.data == root.data; }

        public void setRoot(Node<T> rootData) { root = rootData; }

        public static class Node<T> {
            private T data;
            private Node<T> parent;
            private List<Node<T>> children;

            public Node(T data) {
                this.children = new ArrayList<>();
                this.data = data;
            }

            public List<Node<T>> getChildren() { return children; }

            public void setParent(Node<T> node) { parent = node; }

            @Override
            public boolean equals(Object obj) {
                return this.data == ((Node<T>) obj).data;
            }

            @Override
            public int hashCode() {
                return data.toString().length();
            }
        }
    }
}


