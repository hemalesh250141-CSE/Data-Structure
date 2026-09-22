import java.io.*;
import java.util.*;

public class Solution {

    static long minAdd;
    static long totalSum;
    static long[] values;
    static long[] subtreeSum;
    static List<Integer>[] adj;

    static Set<Long> ancestors = new HashSet<>();
    static Set<Long> visited = new HashSet<>();

    public static long balancedForest(List<Integer> c, List<List<Integer>> edges) {
        int n = c.size();
        values = new long[n + 1];
        subtreeSum = new long[n + 1];
        adj = new ArrayList[n + 1];
        
        for (int i = 1; i <= n; i++) {
            adj[i] = new ArrayList<>();
            values[i] = c.get(i - 1);
        }

        for (List<Integer> edge : edges) {
            int u = edge.get(0);
            int v = edge.get(1);
            adj[u].add(v);
            adj[v].add(u);
        }

        minAdd = Long.MAX_VALUE;
        ancestors.clear();
        visited.clear();

        // Step 1: Compute total sum and subtree sums
        totalSum = computeSubtreeSum(1, 0);

        // Step 2: DFS to evaluate cutting options
        dfs(1, 0);

        return minAdd == Long.MAX_VALUE ? -1 : minAdd;
    }

    private static long computeSubtreeSum(int u, int parent) {
        long sum = values[u];
        for (int v : adj[u]) {
            if (v != parent) {
                sum += computeSubtreeSum(v, u);
            }
        }
        subtreeSum[u] = sum;
        return sum;
    }

    private static void dfs(int u, int parent) {
        long s = subtreeSum[u];

        // Case 1: Target sum is s (Two subtrees of sum s exist)
        if (3 * s >= totalSum) {
            long candidateX = 3 * s - totalSum;
            // Check if another subtree with sum s exists elsewhere,
            // or if a subtree of sum (totalSum - 2*s) exists
            if (visited.contains(s) || ancestors.contains(totalSum - 2 * s) || visited.contains(totalSum - 2 * s)) {
                minAdd = Math.min(minAdd, candidateX);
            }
            // Check if an ancestor subtree has sum 2*s (nested cut)
            if (ancestors.contains(2 * s)) {
                minAdd = Math.min(minAdd, candidateX);
            }
        }

        // Case 2: Target sum is S = (totalSum - s) / 2
        if ((totalSum - s) % 2 == 0) {
            long S = (totalSum - s) / 2;
            if (S >= s) {
                long candidateX = S - s;
                // Check if a subtree with sum S exists elsewhere
                if (visited.contains(S) || ancestors.contains(S + s)) {
                    minAdd = Math.min(minAdd, candidateX);
                }
            }
        }

        // Backtracking logic for DFS traversal
        ancestors.add(s);
        for (int v : adj[u]) {
            if (v != parent) {
                dfs(v, u);
            }
        }
        ancestors.remove(s);
        visited.add(s);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int q = Integer.parseInt(bufferedReader.readLine().trim());

        for (int qItr = 0; qItr < q; qItr++) {
            int n = Integer.parseInt(bufferedReader.readLine().trim());

            String[] cTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
            List<Integer> c = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                c.add(Integer.parseInt(cTemp[i]));
            }

            List<List<Integer>> edges = new ArrayList<>();
            for (int i = 0; i < n - 1; i++) {
                String[] edgesRowTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
                List<Integer> edgesRowItems = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    edgesRowItems.add(Integer.parseInt(edgesRowTemp[j]));
                }
                edges.add(edgesRowItems);
            }

            long result = balancedForest(c, edges);

            bufferedWriter.write(String.valueOf(result));
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
