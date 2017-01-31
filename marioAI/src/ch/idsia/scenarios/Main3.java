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

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardJumpingAgent;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class Main3
{
public static void main(String[] args)
{
    final MarioAIOptions marioAIOptions = new MarioAIOptions(args);

    final Agent agent = new ForwardJumpingAgent();
    marioAIOptions.setAgent(agent);
    
    int seed = 99;
    marioAIOptions.setLevelRandSeed(seed);
    
    int d = 100;
    marioAIOptions.setLevelDifficulty(d);
    
    //marioAIOptions.setEnemies("off");
    //marioAIOptions.setEnemies("g");
    marioAIOptions.setEnemies("ggk");
    
    marioAIOptions.setDeadEndsCount(true);
    marioAIOptions.setCannonsCount(true);
    marioAIOptions.setHillStraightCount(true);
    marioAIOptions.setTubesCount(true); 
    marioAIOptions.setGapsCount(true);
    marioAIOptions.setHiddenBlocksCount(true);
    marioAIOptions.setBlocksCount(true);
    marioAIOptions.setCoinsCount(true);
    marioAIOptions.setFlatLevel(false);
    
    final BasicTask basicTask = new BasicTask(marioAIOptions);
    basicTask.setOptionsAndReset(marioAIOptions);
    basicTask.doEpisodes(1,true,1);
    System.exit(0);
}

}
