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

package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey.karakovskiy@gmail.com
 * Date: Apr 8, 2009
 * Time: 4:03:46 AM
 */

public class IgnoreObstacleAgent extends BasicMarioAIAgent implements Agent
{
int trueJumpCounter = 0;
int stop = 0;

public IgnoreObstacleAgent()
{
    super("IgnoreObstacleAgent");
    reset();
}

public void reset()
{
    action = new boolean[Environment.numberOfKeys];
    action[Mario.KEY_RIGHT] = true;
}

public boolean isObstacle(int r, int c){
	return getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BRICK
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.FLOWER_POT_OR_CANNON
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.LADDER;
}

// judge weather the cell is hole or not
public boolean isHole( int r , int c ){
	for( int i = r; i < 18; i++ ){
		if( getReceptiveFieldCellValue(i, c ) != 0 ) return false;	
	}
	return true;
}

public boolean[] getAction()
{

	// default action is going right without speed
	action[Mario.KEY_RIGHT] = true;
	action[Mario.KEY_SPEED] = false;

	if( isMarioOnGround ){
		// reset stop variable if mario is on ground
		stop = 0;
	} else if( stop == 0 && ( isHole( marioEgoRow+1 , marioEgoCol+2 ) || isHole( marioEgoRow+1 , marioEgoCol+3 ) ) ){
		// mario stop if mario is jumping and a hole is up coming.
		stop = (int)distancePassedPhys;
	}	 
	
	 if( isHole(marioEgoRow+1, marioEgoCol+1 ) && isMarioAbleToJump ){
		// if hole is in the next cell
		trueJumpCounter = 0; // jump
		action[Mario.KEY_SPEED] = true; // run
		stop = -1; // don't stop
	} else if( ( isObstacle(marioEgoRow, marioEgoCol + 1 )
			|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != Sprite.KIND_NONE
			|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != Sprite.KIND_NONE )
			&& isMarioAbleToJump ){
		// if obstacles are up coming
		trueJumpCounter = 0; // jump
	}

	// keep pushing jump button for a few frames
	if( trueJumpCounter > 8 ){
		action[Mario.KEY_JUMP] = false;
	} else {
		action[Mario.KEY_JUMP] = true;
	}

	// increment jump counter
	trueJumpCounter++;
	
	// stop if mario is in danger
	if( stop > 0 && stop != (int)distancePassedPhys ){
		action[Mario.KEY_RIGHT] = false;
	}

	
    return action;
}
}
