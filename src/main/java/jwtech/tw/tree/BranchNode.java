package jwtech.tw.tree;

import java.util.LinkedList;

public class BranchNode
        extends TrieNode{

    BranchNode(char k){
        super.key=k;
        super.kind=NodeKind.BN;
        super.points=new LinkedList<>( );
    }
}