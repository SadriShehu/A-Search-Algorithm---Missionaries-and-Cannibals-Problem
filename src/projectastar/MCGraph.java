package projectastar;

import java.util.*;

public class MCGraph {

	LinkedList<MCNode> vertex_list;
    PriorityQueue<MCNode> open_list;
    HashSet<MCNode> closed_list;
    
    int maxm,maxc,boatcarry;
    
    MCGraph(){
        vertex_list = new LinkedList<MCNode>();
    }
    
    @SuppressWarnings("rawtypes")
	void astarSearch(int m,int c,int bc){
        int steps_needed=0;
        maxm=m;
        maxc=c;
        boatcarry=bc;
        
        Comparator<MCNode> comparator = new MCNodeComparator();
        open_list = new PriorityQueue<MCNode>(10,comparator);
        closed_list = new HashSet<MCNode>();
        
        MCNode start = new MCNode(m,c,'L');
        start.g = 0;
        open_list.add(start);
        MCNode transfer_node = null;
        
        while(!open_list.isEmpty() && (transfer_node == null || transfer_node.missionaries!=0 || transfer_node.cannibals!=0 || transfer_node.boatpos!='R')){
            steps_needed++;
            
            transfer_node = open_list.poll();
            closed_list.add(transfer_node);
            ArrayList<MCNode> adj_nodes = transfer_node.getAdjacentNodes(m,c,boatcarry);
            for(MCNode adj_node : adj_nodes){
                Iterator it = closed_list.iterator();
                boolean inCL=false;
                while(it.hasNext()){
                    MCNode closed_node = (MCNode)it.next();
                    if(adj_node.equals(closed_node)){
                        if (transfer_node.g + 1 < closed_node.g) {
                            closed_node.g = transfer_node.g + 1;
                            closed_node.predecessor = transfer_node;
                            parentRedirection(closed_node);
                        }
                        inCL = true;break;
                    }
                }
                if(inCL)continue;
                it = open_list.iterator();
                boolean inOL=false;
                while(it.hasNext()){
                    MCNode open_node = (MCNode)it.next();
                    if(adj_node.equals(open_node)){
                        if (transfer_node.g + 1 < open_node.g) {
                            open_node.g = transfer_node.g + 1;
                            open_node.predecessor = transfer_node;
                        }
                        inOL = true;
                        break;
                    }
                }
                if(inOL)continue;
                adj_node.g = transfer_node.g + 1;
                adj_node.predecessor = transfer_node;
                open_list.add(adj_node);
            }
        }
        
        System.out.println(steps_needed);
        
        if(transfer_node.missionaries==0 && transfer_node.cannibals==0 && transfer_node.boatpos=='R'){
            System.out.println("Path:");
            transfer_node.printPath();
        }
    }
    
    @SuppressWarnings("rawtypes")
	void parentRedirection(MCNode node){
        ArrayList<MCNode> adj_nodes = node.getAdjacentNodes(maxm,maxc,boatcarry);
        for(MCNode adj_node : adj_nodes){
            Iterator it = closed_list.iterator();
            while (it.hasNext()) {
                MCNode closed_node = (MCNode) it.next();
                if (adj_node.equals(closed_node)) {
                    if (node.g + 1 < closed_node.g) {
                        closed_node.g = node.g + 1;
                        closed_node.predecessor = node;
                        parentRedirection(closed_node);
                    }
                    break;
                }
            }
        }
    }
}


class MCNode{
    int missionaries;
    int cannibals;
    char boatpos;
    MCNode predecessor;
    int g,h;
    
    MCNode(int m,int c,char b){
        missionaries = m;
        cannibals = c;
        boatpos = b;
        h = (int) ((m*m+c*c+2*m*c));
        predecessor = null;
    }
    
    boolean equals(MCNode node){
        if(node.boatpos == this.boatpos && 
                node.missionaries == this.missionaries &&
                node.cannibals == this.cannibals)return true;
        return false;
    }
    
    ArrayList<MCNode> getAdjacentNodes(int m,int c,int bc){
        ArrayList<MCNode> adj_nodes = new ArrayList<MCNode>();
        for(int carry = 1;carry <= bc; carry++){
            int carrym = carry,carryc = 0;
            while(carrym>=0){
                if(this.boatpos == 'L'){
                    int newm = this.missionaries - carrym;
                    int newc = this.cannibals - carryc;
                    if(newm>=0 && newc>=0 && (newm>=newc || newm==0) && ((m-newm)>=(c-newc) || (m-newm==0))){
                        MCNode adj_node = new MCNode(newm,newc,'R');
                        adj_nodes.add(adj_node);
                    }
                }
                else if(this.boatpos == 'R'){
                    int newm = this.missionaries + carrym;
                    int newc = this.cannibals + carryc;
                    if(newm<=m && newc<=c && (newm>=newc || newm==0) && ((m-newm)>=(c-newc) || (m-newm==0))){
                        MCNode adj_node = new MCNode(newm,newc,'L');
                        adj_nodes.add(adj_node);
                    }
                }
                carrym--;carryc++;
            }
        }
        return adj_nodes;
    }
    
    void printPath(){
        if(this.predecessor!=null)this.predecessor.printPath();
        System.out.println("< m "+this.missionaries+", c "+this.cannibals + " | <b "+this.boatpos+"> |" + "< m "+ (3 - this.missionaries) +", c "+ (3 - this.cannibals) +">");
    }
}


class MCNodeComparator implements Comparator<MCNode>{
    @Override
    public int compare(MCNode x, MCNode y){
        if(x.g + x.h < y.g + y.h)return -1;
        if(x.g + x.h > y.g + y.h)return 1;
        else return 0;
    }
}
