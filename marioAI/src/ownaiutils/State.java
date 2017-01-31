package ownaiutils;

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
    public float fd = 0;
    private ZobristHash zobrist;
    public int stop = 0;
    public int id = 0;
    public State par;
    public int dep = 0;
 
    public State(){
    	
    }
    
    public State( ArrayList<Integer> _actions , String _arg , ZobristHash _zobrist ){
    	arg = _arg;
    	zobrist = _zobrist;
    	set_actions( _actions );
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
		
			val = agent.reward * 10.0f + ( agent.maxd * 9.0f + evaluationInfo.distancePassedPhys ) / 10.0f - ( 2 - evaluationInfo.marioMode ) * 1000; // max cur intdiv
			if( evaluationInfo.marioStatus == 0 ) val = -1000000000.0f;
			if( evaluationInfo.marioStatus == 1 ) val =  1000000000.0f;
			
			fd = evaluationInfo.distancePassedPhys;
			hash = agent.calc_hash(zobrist);
			dist = evaluationInfo.distancePassedCells;

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
