package ch.idsia.agents.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import ch.idsia.agents.Agent;
import ch.idsia.agents.KeyOfMC;
import ch.idsia.agents.MCAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;

import ownaiutils.ZobristHash;


public class SimulateAgent extends BasicMarioAIAgent implements Agent {
    private ArrayList<Integer> actions;
    private int fcnt = 0;
    private Random rnd = new Random();
    public float maxd = 0.0f;
    public float reward = 0.0f;
    public float x = 0.0f;
    public float y = 0.0f;
    private float px = 0.0f;
    private float py = 0.0f;
    
    public SimulateAgent(){
		super("SimulateAgent");
		reset();
    }

    public void set_actions( ArrayList<Integer> _actions ){
    	actions = _actions;
    }

    private void decode( int x ){
		for( int i = 0; i < 6; i++ ){
		    if( x % 2 == 0 ) action[i] = false;
		    else action[i] = true;
		    x /= 2;
		}
    }
    
	private void int_to_action( int n ){
		action[Mario.KEY_JUMP] = (n == 0 || (n > 4 && n < 11));
		action[Mario.KEY_SPEED] = (n == 1 || n == 5 || n == 9 || n == 10);
		action[Mario.KEY_RIGHT] = (n == 2 || n == 6 || n == 9);
		action[Mario.KEY_LEFT] = (n == 3 || n == 7 || n == 10);
		action[Mario.KEY_DOWN] = (n == 4 || n == 8);
	}
	    
	
    public void reset(){
		action = new boolean[Environment.numberOfKeys];
	
   }
	
    public boolean[] getAction(){
    	if( fcnt < actions.size() ){
    		// int_to_action( actions.get(fcnt) );
    		decode( actions.get(fcnt) );
        }
    	
    	if( distancePassedPhys > maxd ){
    		maxd = distancePassedPhys;
    	}
    	
        fcnt++;
        
        px = x;
        py = y;
        x = marioFloatPos[0];
        y = marioFloatPos[1];
        
        reward = intermediateReward;
        
        // System.out.println( intermediateReward );
        
        return action;
    }
    
    public void printReward(){
    	System.out.println( intermediateReward );
    }
    
    public int calc_hash( ZobristHash zobrist ){
    	for( int i = 0; i < 19; i++ ){
    		for( int j = 0; j < 19; j++ ){
    			zobrist.set_field(i, j, getReceptiveFieldCellValue(i,j) );
    			zobrist.set_enemy(i, j, getEnemiesCellValue(i,j) );
    		}
    	}
    	int res = zobrist.calc();
    	if( isMarioAbleToJump ) res = res ^ 114514;
    	if( marioStatus == 2 ) res = res ^ 7272727;
    	return res;
    }
    
    public void write(){
		try{
			File file = new File( "./cnn/dat/state.dat" );
			FileWriter filewriter = new FileWriter( file );
			
        	for( int i = 0; i < 19; i++ ){
        		for( int j = 0; j < 19; j++ ){
        			filewriter.write( String.valueOf( getReceptiveFieldCellValue(i,j) ) );            			
        			filewriter.write( " " );            			
        		}
        	}
        	for( int i = 0; i < 19; i++ ){
        		for( int j = 0; j < 19; j++ ){
        			filewriter.write( String.valueOf( getEnemiesCellValue(i,j) ) );
        			if( i == 18 && j == 18 ){
        				filewriter.write( "\n" );
        			} else {
        				filewriter.write( " " );            				
        			}
        		}
        	}    			
			filewriter.close();
		} catch( IOException e ){
			System.out.println(e);
		}    	
    }
    
	public void write_vel(){
		float vx = x - px;
		float vy = y - py;
		try{
			File file = new File( "./cnn/dat/vel.dat" );
			FileWriter filewriter = new FileWriter( file );
			
			String str = String.format( "%.10f %.10f" , vx , vy );
			filewriter.write( str );
			filewriter.write( "\n" );
			
			filewriter.close();
		} catch( IOException e ){
			System.out.println(e);
		}
	}	    
}
