package ch.idsia.agents.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import ch.idsia.agents.Agent;
import ch.idsia.agents.KeyOfMC;
import ch.idsia.agents.MCAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;
import ch.idsia.agents.controllers.RandomAgent;

import ownaiutils.State;
import ownaiutils.DistanceIdPair;
import ownaiutils.ZobristHash;


public class OwnAgent extends BasicMarioAIAgent implements Agent {
    private Agent randomAgent = new RandomAgent();
    private int fcnt = 0;
    private ArrayList<Integer> actions;
    private float val = 0.0f;
    private int d, seed;
    private ArrayList<State> cur, nex;
    private int breadth = 128;
    // private int breadth = 32;	
    private int all_use = 48;
    private int max_turn = 3000;
    private String arg;
    private boolean cont;
    private ZobristHash zobrist;
    private Set<Integer> used;
    private int id_cnt = 0;
    private boolean use[] = new boolean[1024];
    private int family_dist = 1;

    private int num_threshold = 5;
    private int threshold_pos[] = { 0  , 1 , 4 , 16 , 32 , breadth };
    private int threshold_val[] = { 12 , 6 , 4 , 2  , 1  , 0       };

    /*
    private int num_threshold = 3;
    private int threshold_pos[] = { 0  , 1 , 4 , breadth };
    private int threshold_val[] = { 12 , 4 , 1 , 0       };
    */
    
    private int num_act[] = new int[breadth];
    
    public OwnAgent(){
		super("OwnAgent");
		actions = new ArrayList<Integer>();
		reset();    	
    }
    
    public OwnAgent( int _d , int _seed ){
 		super("OwnAgent");
 		set_stage( _d , _seed );
 		actions = new ArrayList<Integer>();
 		reset();
     }
    
    public OwnAgent( String _arg ){
 		super("OwnAgent");
 		arg = _arg;
 		actions = new ArrayList<Integer>();
 		reset();
     }
    
    public void set_stage( int _d , int _seed ){
		d = _d;
		seed = _seed;
    }

    private void decode( int x ){
		for( int i = 0; i < 6; i++ ){
		    if( x % 2 == 0 ) action[i] = false;
		    else action[i] = true;
		    x /= 2;
		}
    }
    
    private boolean is_family( State a , State b ){
    	for( int i = 0; i < family_dist; i++ ){
    		if( a.dep > b.dep ){
    			a = a.par;
    		} else if( b.dep > a.dep ){
    			b = b.par;
    		} else if( a.dep > 0 ){
    			a = a.par;
    			b = b.par;
    		}
    	}
    	return a.id == b.id;
    }

    private void add_nex( ArrayList<Integer> ss , State prv ){
    	State st = new State( ss , arg , zobrist , id_cnt++ , prv );
    	if( !used.contains( st.hash ) ){
    		used.add( st.hash );
    		nex.add( st );
    	} else {
    		
    	}
    }
    
    private float dist( State a , State b ){
    	float dx = a.x - b.x;
    	float dy = a.y - b.y;
    	return dx * dx + dy * dy;
    }
    
    private void set_next(){
    	int n = nex.size();
    	int c = n;
    	for( int i = 0; i < n; i++ ){
    		use[i] = true;
    	}
    	ArrayList<DistanceIdPair> v = new ArrayList<DistanceIdPair>();
    	for( int i = 0; i < n; i++ ){
    		for( int j = 0; j < i; j++ ){
    			if( !is_family( nex.get(i) , nex.get(j) ) ){
    				v.add( new DistanceIdPair( dist( nex.get(i) , nex.get(j) ) , i , j ) );
    			}
    		}
    	}
    	Collections.sort( v );
    	System.out.println( c );
    	for( int i = 0; i < v.size(); i++ ){
    		if( c < breadth ){
    			break;
    		}
    		int a = v.get(i).a;
    		int b = v.get(i).a;
    		if( !use[a] || !use[b] ){
    			continue;
    		}
    		int x = a;
    		if( nex.get(b).val < nex.get(a).val ){
    			x = b;
    		}
    		c--;
    		use[x] = false;
    	}
    	cur = new ArrayList<State>();
    	for( int i = 0; i < n; i++ ){
    		if( use[i] ){
    			cur.add( nex.get(i) );
    		}
    	}
    	Collections.sort( cur );
    	nex = new ArrayList<State>();
    }
    
    boolean read_flag( String filename ){
	    try{
	    	File file = new File( filename );
	    	Scanner sc = new Scanner( file );
	    	
	    	int x = Integer.parseInt( sc.next() );
	    	if( x == 1 ) return true;
	    	else return false;
	    } catch( FileNotFoundException e ){
	    	System.out.println( e );
	    } catch( IOException e ){
	    	System.out.println( e );
	    }
	    return false;
    }
    
    public void calc(){
		cont = false;

    	cur = new ArrayList<State>();
    	nex = new ArrayList<State>();
    	
    	zobrist = new ZobristHash();
    	
		cur.add( new State( new ArrayList<Integer>() , arg , zobrist , id_cnt++ , 0 ) );
		
		Random rnd = new Random();
		int turn = 0;
		int prv = 0;
		
		while( !cur.isEmpty() ){
			
			if( turn >= max_turn ){
				break;
			}
			
		    if( read_flag( "stopf" ) ) break;

			
			if( read_flag( "resetf" ) ){
				cont = true;
				return;
			}
			
		    
			turn++;
			
			if( cur.get(0).val > val ){
				actions = cur.get(0).actions;
				val = cur.get(0).val;
				prv = turn;
			}
			
			if( cur.get(0).val >= 4096.0f ){
				break;
			}

			used = new HashSet<Integer>();

			
			System.out.print( turn + " " + cur.size() + " " );
			
			for( int cnt = 0; cnt < cur.size(); cnt++ ){
				if( cur.get(cnt).actions.size() == 0 || cnt < all_use || rnd.nextInt(cnt) == 0 ){
					System.out.print( "*" );
					for( int i = 0; i < 32; i++ ){
						if( ( i / 4 ) % 2 == 0 && ( i / 16 ) == 1 && i % 2 != ( i / 2 ) % 2 ){
							ArrayList<Integer> st = new ArrayList<Integer>( cur.get(cnt).actions );
							for( int j = 0; j < 4; j ++ ){
								st.add( i );
							}
							add_nex( st , cur.get(cnt) );
						}
					}
				} else {
					System.out.print( " " );
					ArrayList<Integer> st = new ArrayList<Integer>( cur.get(cnt).actions );
					for( int j = 0; j < 4; j ++ ){
						st.add( st.get( st.size() - 1 ) );
					}
					add_nex( st , cur.get(cnt) );
				}
				
				/*
				for( int i = 0; i < num_act[cnt]; i++ ){
					int next_act = cur.get(cnt).next_action[i];
					ArrayList<Integer> st = new ArrayList<Integer>( cur.get(cnt).actions );
					for( int j = 0; j < 4; j++ ){
						st.add( next_act );
					}
					add_nex( st , cur.get(cnt) );					
				}
				*/
				
				System.out.print( cur.get(cnt).val + "(" + cur.get(cnt).x + ") " );

			}
			System.out.print( "\n" );
			
			set_next();
		}
		

		try{
			String filename = "dats/" + String.valueOf( System.currentTimeMillis() ) + ".dat";
			System.out.println( filename );
			File file = new File( filename );
			FileWriter filewriter = new FileWriter( file );
			
			for( int i = 0; i < actions.size(); i++ ){
				filewriter.write( String.valueOf( actions.get(i) ) + "\n" );
			}
			
			filewriter.close();
		} catch( IOException e ){
			System.out.println(e);
		}    	
    }
	
    public void reset(){
		action = new boolean[Environment.numberOfKeys];
		
		if( actions.size() > 0 ) return;
		
		for( int i = 0; i < num_threshold; i++ ){
			for( int j = threshold_pos[i]; j < threshold_pos[i+1]; j++ ){
				num_act[j] = threshold_val[i];
			}
		}

		cont = true;
		while( cont ) calc();
    }
	
    public boolean[] getAction(){
    	System.out.println( fcnt );
    	if( fcnt < actions.size() ){
    		decode( actions.get( fcnt ) );
    	}
		fcnt++;
			
		return action;
    }
}
