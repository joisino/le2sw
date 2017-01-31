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

public class task3 extends BasicMarioAIAgent implements Agent
{
int trueJumpCounter = 0;
int stop = 0;
int fcnt = 0;

public task3()
{
    super("task3");
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

private boolean isDanger( int y , int x ){
	for( int i = 0; i < 2; i++ ){
		for( int j = 0; j < 3; j++ ){
			if( getEnemiesCellValue(y+i,x+j) != Sprite.KIND_NONE && getEnemiesCellValue(y+i,x+j) != Sprite.KIND_FIREBALL){
				return true;
			}
		}
	}
	return false;
}

private boolean isPacken( int y , int x ){
	return getEnemiesCellValue(y,x) == Sprite.KIND_ENEMY_FLOWER && getReceptiveFieldCellValue(y-1,x) != GeneralizerLevelScene.FLOWER_POT_OR_CANNON;
}

private boolean isPackenNow( int y , int x ){
	for( int i = 0; i < 19; i++ ){
		if( isPacken( i , x ) ){
			return true;
		}
	}
	return false;
}


public boolean[] getAction()
{
	
	// default action is going right without speed
	action[Mario.KEY_RIGHT] = true;
	action[Mario.KEY_LEFT] = false;
	action[Mario.KEY_SPEED] = false;

	
	if( ( isObstacle(marioEgoRow, marioEgoCol + 1 )
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

	// if mario cannot jump and is in danger, go back 
	if( !isMarioAbleToJump && isDanger( marioEgoRow , marioEgoCol ) ){
		action[Mario.KEY_RIGHT] = false;
		action[Mario.KEY_LEFT] = true;
	}	

	// if packen is near, back.
	if( isPackenNow( marioEgoRow , marioEgoCol ) ){
		action[Mario.KEY_RIGHT] = false;
		action[Mario.KEY_LEFT] = true;
	}

	// increment jump counter
	trueJumpCounter++;
	
	fcnt++;
	
	// stop speed to shoot fireball
	if( fcnt % 8 != 0 ){
		action[Mario.KEY_SPEED] = true;
	}
	
	// if mario cannot jump, sometimes go back for safety
	if( !isMarioAbleToJump && 1 < fcnt % 8 && fcnt % 8 < 5 ){
		action[Mario.KEY_RIGHT] = false;
		action[Mario.KEY_LEFT] = true;		
	}

    return action;
}

}
