#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

const int MOD = 1e9 + 7;
const int MAXN = 200005;
const int LOGN = 18;

vector<int> adj[MAXN];
int depth[MAXN], parent_up[MAXN][LOGN];
int in_time[MAXN], out_time[MAXN], timer = 0;

void dfs(int u, int p, int d) {
    depth[u] = d;
    in_time[u] = ++timer;
    parent_up[u][0] = p;
    for (int i = 1; i < LOGN; ++i) {
        parent_up[u][i] = parent_up[parent_up[u][i - 1]][i - 1];
    }
    for (int v : adj[u]) {
        if (v != p) {
            dfs(v, u, d + 1);
        }
    }
    out_time[u] = timer;
}

bool is_ancestor(int u, int v) {
    return in_time[u] <= in_time[v] && out_time[u] >= out_time[v];
}

int get_lca(int u, int v) {
    if (is_ancestor(u, v)) return u;
    if (is_ancestor(v, u)) return v;
    for (int i = LOGN - 1; i >= 0; --i) {
        if (!is_ancestor(parent_up[u][i], v)) {
            u = parent_up[u][i];
        }
    }
    return parent_up[u][0];
}

bool compare_nodes(int a, int b) {
    return in_time[a] < in_time[b];
}

vector<int> vtree_adj[MAXN];
long long sum_val[MAXN];
bool is_query_node[MAXN];
long long lca_sum = 0;

void dfs_virtual(int u) {
    if (is_query_node[u]) {
        sum_val[u] = u;
    } else {
        sum_val[u] = 0;
    }

    long long current_pair_sum = 0;

    for (int v : vtree_adj[u]) {
        dfs_virtual(v);
        
        // Product of (nodes in current subtree) * (nodes in previous subtrees of u)
        long long cross_terms = (current_pair_sum * sum_val[v]) % MOD;
        lca_sum = (lca_sum + (cross_terms * depth[u]) % MOD) % MOD;

        current_pair_sum = (current_pair_sum + sum_val[v]) % MOD;
        sum_val[u] = (sum_val[u] + sum_val[v]) % MOD;
    }

    // If u itself is a query node, pair it with all nodes in its subtrees
    if (is_query_node[u]) {
        long long direct_terms = (1LL * u * (sum_val[u] - u + MOD)) % MOD;
        lca_sum = (lca_sum + (direct_terms * depth[u]) % MOD) % MOD;
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    int n, q;
    if (!(cin >> n >> q)) return 0;

    for (int i = 0; i < n - 1; ++i) {
        int u, v;
        cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    dfs(1, 1, 0);

    while (q--) {
        int k;
        cin >> k;
        vector<int> query_nodes(k);
        
        long long total_sum_nodes = 0;
        long long total_sum_depth = 0;
        long long sum_sq_depth = 0;

        for (int i = 0; i < k; ++i) {
            cin >> query_nodes[i];
            int u = query_nodes[i];
            is_query_node[u] = true;

            long long u_depth = (1LL * u * depth[u]) % MOD;
            total_sum_nodes = (total_sum_nodes + u) % MOD;
            total_sum_depth = (total_sum_depth + u_depth) % MOD;
            sum_sq_depth = (sum_sq_depth + (1LL * u * u_depth) % MOD) % MOD;
        }

        if (k <= 1) {
            cout << 0 << "\n";
            for (int u : query_nodes) is_query_node[u] = false;
            continue;
        }

        // Build Virtual Tree
        sort(query_nodes.begin(), query_nodes.end(), compare_nodes);
        vector<int> vnodes = query_nodes;
        for (int i = 0; i < k - 1; ++i) {
            vnodes.push_back(get_lca(query_nodes[i], query_nodes[i + 1]));
        }

        sort(vnodes.begin(), vnodes.end(), compare_nodes);
        vnodes.erase(unique(vnodes.begin(), vnodes.end()), vnodes.end());

        vector<int> st;
        st.push_back(vnodes[0]);

        for (size_t i = 1; i < vnodes.size(); ++i) {
            int u = vnodes[i];
            while (!is_ancestor(st.back(), u)) {
                st.pop_back();
            }
            vtree_adj[st.back()].push_back(u);
            st.push_back(u);
        }

        lca_sum = 0;
        dfs_virtual(vnodes[0]);

        // Final Answer Calculation
        long long part1 = (total_sum_nodes * total_sum_depth) % MOD;
        part1 = (part1 - sum_sq_depth + MOD) % MOD;

        long long part2 = (2 * lca_sum) % MOD;
        long long ans = (part1 - part2 + MOD) % MOD;

        cout << ans << "\n";

        // Reset Virtual Tree state
        for (int u : vnodes) {
            vtree_adj[u].clear();
            is_query_node[u] = false;
            sum_val[u] = 0;
        }
    }

    return 0;
}
