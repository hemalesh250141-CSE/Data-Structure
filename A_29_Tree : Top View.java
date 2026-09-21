#include<bits/stdc++.h>

using namespace std;

class Node {
    public:
        int data;
        Node *left;
        Node *right;
        Node(int d) {
            data = d;
            left = NULL;
            right = NULL;
        }
};

class Solution {
    public:
  		Node* insert(Node* root, int data) {
            if(root == NULL) {
                return new Node(data);
            } else {
                Node* cur;
                if(data <= root->data) {
                    cur = insert(root->left, data);
                    root->left = cur;
                } else {
                    cur = insert(root->right, data);
                    root->right = cur;
               }

               return root;
           }
        }

/*
class Node {
    public:
        int data;
        Node *left;
        Node *right;
        Node(int d) {
            data = d;
            left = NULL;
            right = NULL;
        }
};

*/

    void topView(Node * root) {
    if (root == NULL) return;

    // Map to store <Horizontal Distance, Node Value>
    map<int, int> topNodeMap;
    
    // Queue to perform BFS: stores pair of <Node*, Horizontal Distance>
    queue<pair<Node*, int>> q;

    q.push({root, 0});

    while (!q.empty()) {
        auto p = q.front();
        q.pop();

        Node* curr = p.first;
        int hd = p.second;

        // If this HD is seen for the first time, insert it into map
        if (topNodeMap.find(hd) == topNodeMap.end()) {
            topNodeMap[hd] = curr->data;
        }

        // Push left child with hd - 1
        if (curr->left != NULL) {
            q.push({curr->left, hd - 1});
        }

        // Push right child with hd + 1
        if (curr->right != NULL) {
            q.push({curr->right, hd + 1});
        }
    }

    // Print values from leftmost HD to rightmost HD
    for (auto const& [hd, val] : topNodeMap) {
        cout << val << " ";
    }
}

}; //End of Solution

int main() {
  
    Solution myTree;
    Node* root = NULL;
    
    int t;
    int data;

    std::cin >> t;

    while(t-- > 0) {
        std::cin >> data;
        root = myTree.insert(root, data);
    }
  
	myTree.topView(root);
    return 0;
}
