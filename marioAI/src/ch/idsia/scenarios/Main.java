/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.scenarios;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.OwnAgent;
import ch.idsia.agents.controllers.SimulateAgent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.agents.controllers.ReinforcementAgent;

import ownaiutils.State;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class Main
{
private static void write_reward( float reward ){
	try{
		File file = new File( "./cnn/dat/reward.log" );
		FileWriter filewriter = new FileWriter( file, true );
			
		String str = String.format( "%.3f" , reward );
		filewriter.write( str );
		filewriter.write( "\n" );
			
		filewriter.close();
	} catch( IOException e ){
		System.out.println(e);
	}
}

public static void main(String[] args)
{
	int cnt = 0;
	// for( int i = 0; i < 100000; i++ ){
		String arg;
		arg = "-lde on -i off -ltb off -ld 2 -ls 0 -le g";
		// arg = "-lde on -i off -ltb off -ld 1 -ls 0 -le g";
		// 4-2 arg = "-lco off -lb on -le off -lhb off -lg on -ltb on -lhs off -lca on -lde on -ld 5 -ls 133829";
		// 4-3 arg = "-lde on -i off -ld 30 -ls 133434 -lhb on";
		// String arg = "-lde on -i off -ld 100 -ls 133434 -lhb on";
	    final MarioAIOptions marioAIOptions = new MarioAIOptions( arg );
	    
	
	    final OwnAgent agent = new OwnAgent( arg );
	    // final ForwardAgent agent = new ForwardAgent();
	    // final ReinforcementAgent agent = new ReinforcementAgent( arg );
	 
	    marioAIOptions.setAgent(agent);
	    // marioAIOptions.setFPS(100);
	    marioAIOptions.setTimeLimit(200);
	    
	    marioAIOptions.setVisualization(true);
	
	    final BasicTask basicTask = new BasicTask(marioAIOptions);
	    basicTask.setOptionsAndReset(marioAIOptions);
	    basicTask.runSingleEpisode(1);
	    // basicTask.doEpisodes(1,true,1);
	    
	    /*
	    float reward = agent.reward();
	    write_reward( agent.xpos() );
	    System.out.println( agent.xpos() );
	    System.out.println( agent.reward() );
	    */
	    
	    /*
	    if( agent.xpos() > 2110.0f ){
	    	System.out.println( "WALL" );
	    	cnt++;
	    }
	    
	    for( int c = 0; c < cnt; c++ ){
	    	System.out.print( "W" );
	    }
	    System.out.println( "" );
	    */
	    
	    /*
	    if( i % 256 == 255 ){
	    	agent.set_request( 2 );
	    
	    	agent.wait_done();
	   	}
	   	*/
	
	    /*
	    System.out.print( "Done " );	    
	    System.out.println( i );
	}*/

    System.exit(0);
}

}
