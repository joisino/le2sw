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
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.OwnAgent;
import ch.idsia.agents.controllers.SimulateAgent;

import ownaiutils.State;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */


public final class Main
{
	
	private static int breadth[] = { 32 , 64 , 128 , 256 };
	private static int all_use[] = { 4 , 8 , 16 , 32 };
	
public static void main(String[] args)
{
	
	for( int i = 0; i < 10; i ++ ){
	
	long start = System.currentTimeMillis();
	
	String arg = "-lde on -i off -ltb off -ld 2 -ls 0 -le g";
	// 4-2 String arg = "-lco off -lb on -le off -lhb off -lg on -ltb on -lhs off -lca on -lde on -ld 5 -ls 133829";
	// 4-3 String arg = "-lde on -i off -ld 30 -ls 133434 -lhb on";
	// String arg = "-lde on -i off -ld 100 -ls 133434 -lhb on";
    final MarioAIOptions marioAIOptions = new MarioAIOptions( arg );
    
    // final OwnAgent agent = new OwnAgent( d , seed );
    // final OwnAgent agent = new OwnAgent( arg , breadth[i] , all_use[j] );
    final OwnAgent agent = new OwnAgent( arg , 32 , 32 );
 
    marioAIOptions.setAgent(agent);
    
    final BasicTask basicTask = new BasicTask(marioAIOptions);
    basicTask.setOptionsAndReset(marioAIOptions);
    basicTask.doEpisodes(1,true,1);

    
    System.out.println( "*** " +  32 + " " + 32 );
    System.out.println( ( System.currentTimeMillis() - start ) );
    // System.out.println( agent.get_reward() );
	}
    
    System.exit(0);
}

}