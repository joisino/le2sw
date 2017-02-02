package ch.idsia.scenarios;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.OwnAgent;
import ch.idsia.agents.controllers.SimulateAgent;

import ownaiutils.State;

public final class Load
{
public static void main(String[] args)
{
    final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
    
    int d = 10;
    int seed = 99;
    
    
    final SimulateAgent agent = new SimulateAgent();

    
    try{
    	// 4-1 File file = new File( "dats/keep/rand_cur1_task41.dat" );
    	// 4-2 File file = new File( "dats/keep/rand_cur1_task42_2.dat" );
    	// File file = new File( "dats/keep/rand_cur1_task43.dat" );
    	// dif100 File file = new File( "dats/keep/rand_cur1_dif100.dat" );
    	
    	// File file = new File( "dats/keep/rand_cur1_task43.dat" );
    	// File file = new File( "dats/keep/rand_cur1_task43_score2.dat" );
    	
    	// File file = new File( "dats/keep/cur.dat" );
    	File file = new File( "dats/1486007212506.dat" );
    	
    	Scanner sc = new Scanner( file );
    	
    	ArrayList<Integer> actions = new ArrayList<Integer>();
    	while( sc.hasNext() ){
    		int x = Integer.parseInt( sc.next() );
    		System.out.println( x );
    		actions.add( x );
    	}
    	agent.set_actions( actions );
    } catch( FileNotFoundException e ){
    	System.out.println( e );
    } catch( IOException e ){
    	System.out.println( e );
    }
    
    marioAIOptions.setAgent(agent);
    // marioAIOptions.setLevelDifficulty(d);
    // marioAIOptions.setLevelRandSeed(seed);
    marioAIOptions.setArgs( "-lde on -i off -ltb off -ld 2 -ls 0 -le g" );
    // marioAIOptions.setArgs( "-lde on -i on -ltb off -ld 2 -ls 0 -le g" );
    // 4-2 marioAIOptions.setArgs( "-lco off -lb on -le off -lhb off -lg on -ltb on -lhs off -lca on -lde on -ld 5 -ls 133829" );
    // 4-3 marioAIOptions.setArgs( "-lde on -i off -ld 30 -ls 133434 -lhb on" );
    // dif100 marioAIOptions.setArgs( "-lde on -i off -ld 100 -ls 133434 -lhb on" );

    
    final BasicTask basicTask = new BasicTask(marioAIOptions);
    basicTask.setOptionsAndReset(marioAIOptions);
    basicTask.doEpisodes(1,true,1);

    agent.printReward();
    
    System.exit(0);
}

}
