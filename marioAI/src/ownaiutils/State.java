package ownaiutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import ch.idsia.agents.Agent;
import ch.idsia.agents.KeyOfMC;
import ch.idsia.agents.MCAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;
import ch.idsia.agents.controllers.SimulateAgent;

import ownaiutils.ZobristHash;

public class State implements Comparable{
    public ArrayList<Integer> actions;
    public float val = 0.0f;
    private int d, seed;
    private String arg;
    private Random rnd = new Random();
    public int hash = 0;
    public int dist = 0;
    private ZobristHash zobrist;
    public int id = 0;
    public State par;
    public int dep = 0;
    public float x = 0.0f;
    public float y = 0.0f;
    public float reward = 0.0f;
    public boolean end = false;
    public int mode = 2;
    public int next_action[];
    public float DQN_val;
    
    public State(){
    	
    }
    
    public State( ArrayList<Integer> _actions , String _arg , ZobristHash _zobrist , int _id , int _dep ){
    	id = _id;
    	dep = _dep;    	
    	arg = _arg;
    	zobrist = _zobrist;
    	set_actions( _actions );
    }        
    
    public State( ArrayList<Integer> _actions , String _arg , ZobristHash _zobrist , int _id , State _par ){
    	id = _id;
    	par = _par;
    	dep = par.dep + 1;
    	arg = _arg;
    	zobrist = _zobrist;
    	set_actions( _actions );
    }    
    
    public State( ArrayList<Integer> _actions , int _d , int _seed ){
    	set_stage( _d , _seed );
    	set_actions( _actions );
    }    
    
	public void set_request( int x ){
		try{
			File file = new File( "./cnn/dat/request.dat" );
			FileWriter filewriter = new FileWriter( file );
			
			filewriter.write( String.valueOf( x ) );
			filewriter.write( "\n" );
			
			filewriter.close();
		} catch( IOException e ){
			System.out.println(e);
		}
	}    
    
	private int get_request(){
	    try{
	    	File file = new File( "./cnn/dat/request.dat" );
	    	Scanner sc = new Scanner( file );

	    	if( sc.hasNext() ){
	    		return Integer.parseInt( sc.next() );
	    	}
	    } catch( FileNotFoundException e ){
	    	System.out.println( e );
	    } catch( IOException e ){
	    	System.out.println( e );
	    }
	    
	    return -1;
	}    
    
	private void wait_done(){
		while( get_request() != 0 ){
			try{
				Thread.sleep( 2 );
			} catch( InterruptedException e ){
				System.out.println( e );
			}
		}
	}
	
	private void read_next(){
	    try{
	    	File file = new File( "./cnn/dat/pred.dat" );
	    	Scanner sc = new Scanner( file );

	    	next_action = new int[12];
	    	for( int i = 0; i < 12; i++ ){
	    		next_action[i] = Integer.parseInt( sc.next() );
	    	}
	    } catch( FileNotFoundException e ){
	    	System.out.println( e );
	    } catch( IOException e ){
	    	System.out.println( e );
	    }
	}
	
	private void read_val(){
	    try{
	    	File file = new File( "./cnn/dat/val.dat" );
	    	Scanner sc = new Scanner( file );

	    	DQN_val = Float.parseFloat( sc.next() );
	    } catch( FileNotFoundException e ){
	    	System.out.println( e );
	    } catch( IOException e ){
	    	System.out.println( e );
	    }
	}
    
    private void calc(){
    	long start = System.currentTimeMillis();

    	MarioAIOptions marioAIOptions = new MarioAIOptions(arg);
    	
		BasicTask basicTask = new BasicTask(marioAIOptions);

		marioAIOptions.setVisualization(false);
		
		SimulateAgent agent = new SimulateAgent();
		agent.set_actions( actions );
		marioAIOptions.setAgent(agent);
	
		basicTask.setOptionsAndReset( marioAIOptions );
			
		basicTask.run(1, actions.size() + 4 );

		EvaluationInfo evaluationInfo = basicTask.getEvaluationInfo();

		agent.write();
		agent.write_vel();
		set_request( 4 );
		wait_done();
		read_next();
		read_val();
		
		val = DQN_val * 100.f + agent.reward * 100.0f + ( agent.maxd * 9.0f + evaluationInfo.distancePassedPhys ) / 10.0f - ( 2 - evaluationInfo.marioMode ) * 1000000; // max cur intdiv
		val = ( agent.maxd * 9.0f + evaluationInfo.distancePassedPhys ) / 10.0f - ( 2 - evaluationInfo.marioMode ) * 1000000; // max cur intdiv
		if( evaluationInfo.marioStatus == 0 ) val = -1000000000.0f;
		// if( evaluationInfo.marioStatus == 1 ) val =  1000000000.0f;
		
		end = ( evaluationInfo.marioStatus == 0 || evaluationInfo.marioStatus == 1 ); 

		x = agent.x;
		y = agent.y;
		
		reward = agent.reward;
		
		mode = evaluationInfo.marioMode;
		
		hash = agent.calc_hash(zobrist);
		dist = evaluationInfo.distancePassedCells;
		
		agent.write();

		
		//System.out.println( "*** " + ( System.currentTimeMillis() - start ) );
    }
    
    public void set_actions( ArrayList<Integer> _actions ){
    	actions = _actions;
    	calc();
    }
    
    public void set_stage( int _d , int _seed ){
    	d = _d;
    	seed = _seed;
    }
    
    public int compareTo( Object other ){
    	State st = (State) other;
    	
    	if( this.val < st.val ) return 1;
    	if( this.val == st.val ) return 0;
    	return -1;
    }
}
