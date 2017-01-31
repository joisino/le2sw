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

import ch.idsia.agents.Agent;
import ch.idsia.agents.KeyOfMC;
import ch.idsia.agents.MCAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;

import ownaiutils.State;
import ownaiutils.ZobristHash;


public class OwnAgent extends BasicMarioAIAgent implements Agent {
    private int fcnt = 0;
    private ArrayList<Integer> actions;
    private float val = 0.0f;
    private PriorityQueue<State> cur, nex;
    private int breadth = 32;
    private int all_use = 4;
    private int max_turn = 800;
    private String arg;
    private boolean cont;
    private ZobristHash zobrist;
    private Set<Integer> used;
    private boolean debug = true;
    private int cnt = 0;
            
    public OwnAgent(){
		super("OwnAgent");
		actions = new ArrayList<Integer>();
		reset();    	
    }

    public OwnAgent( String _arg ){
 		super("OwnAgent");
 		arg = _arg;
 		actions = new ArrayList<Integer>();
 		reset();
     }
    
    public OwnAgent( String _arg , int _breadth , int _all_use ){
 		super("OwnAgent");
 		arg = _arg;
 		breadth = _breadth;
 		all_use = _all_use;
 		actions = new ArrayList<Integer>();
 		reset();
     }        

    private void decode( int x ){
		for( int i = 0; i < 6; i++ ){
		    if( x % 2 == 0 ) action[i] = false;
		    else action[i] = true;
		    x /= 2;
		}
    }
    
    private void add_nex( ArrayList<Integer> ss  ){
    	State st = new State( ss , arg , zobrist );

    	if( !used.contains( st.hash ) ){

    		used.add( st.hash );
    		nex.add( st );
    	}
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

    	cur = new PriorityQueue<State>();
    	nex = new PriorityQueue<State>();
    	
    	zobrist = new ZobristHash();
    	
		cur.add( new State( new ArrayList<Integer>() , arg , zobrist ) );
		
		Random rnd = new Random();
		int turn = 0;
		int prv = 0;
		
		while( !cur.isEmpty() ){
			
			if( turn >= max_turn || val >= 100000000.0f || cur.peek().val <= -100000000.0f ){
				break;
			}
			
		    if( read_flag( "stopf" ) ){
		    	break;
		    }

			
			if( read_flag( "resetf" ) ){
				cont = true;
				return;
			}
		    
			turn++;
			
			if( cur.peek().val > val ){
				actions = cur.peek().actions;
				val = cur.peek().val;
				prv = turn;
			}

			used = new HashSet<Integer>();
			
			
			if( debug ){
				System.out.print( turn + " " + cur.size() + " " );
			}
			
			nex.clear();
			int cnt = 0;			
			while( !cur.isEmpty() ){
				if( cnt >= breadth ) break;
				cnt++;
				
				if( cur.peek().actions.size() == 0 || cnt <= all_use || rnd.nextInt(cnt) == 0 ){
					if( debug ){
						System.out.print( "*" );
					}
					for( int i = 0; i < 32; i++ ){
						if( ( i / 4 ) % 2 == 0 && ( i / 16 ) % 2 == 1 && i % 2 != ( i / 2 ) % 2 ){
							ArrayList<Integer> st = new ArrayList<Integer>( cur.peek().actions );
							for( int j = 0; j < 4; j ++ ){
								st.add( i );
							}
							add_nex( st );
						}
					}
				} else {
					if( debug ){
						System.out.print( " " );
					}
					ArrayList<Integer> st = new ArrayList<Integer>( cur.peek().actions );
					for( int j = 0; j < 4; j++ ){
						st.add( st.get( st.size() - 1 ) );
					}
					add_nex( st );
					add_nex( cur.peek().actions );
				}
				
				if( debug ){
					System.out.print( cur.peek().val + "(" + cur.peek().fd + ") " );
				}
				
				cur.poll();
			}
			if( debug ){
				System.out.print( "\n" );
			}
			
			cur = nex;
			nex = new PriorityQueue<State>();
		}
		

		try{
			String filename = String.format( "dats/%d_%d_%d.dat" , System.currentTimeMillis() , breadth , all_use );
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

		cont = true;
		while( cont ) calc();
    }
	
    public boolean[] getAction(){
    	if( fcnt < actions.size() ){
    		decode( actions.get( fcnt ) );
    	}
		fcnt++;
			
		return action;
    }
    
    public float get_reward(){
    	return intermediateReward;
    }
}
