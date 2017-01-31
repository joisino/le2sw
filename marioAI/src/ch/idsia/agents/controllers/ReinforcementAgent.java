package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import ownaiutils.State;
import ownaiutils.ZobristHash;

public class ReinforcementAgent extends BasicMarioAIAgent implements Agent{
	private String arg;
	private ArrayList<Integer> acts;
	private int fcnt = 0;
	private float prv = 7.0f;
	private float px = 0.0f;
	private float py = 0.0f;
	private boolean learning = false;
	private boolean dbl = false;
	private ArrayList<Integer> actions = new ArrayList<Integer>();
	
	public ReinforcementAgent( String _arg ){
	    super("ReinforcementAgent");
	    arg = _arg;
	    reset();
	}
	
	public void reset(){
		action = new boolean[Environment.numberOfKeys];
		
		read_actions();
	}
	
    public void write(){
		try{
			File file = new File( "./cnn/dat/state.dat" );
			FileWriter filewriter = new FileWriter( file );
			
			int cnt = 0;
        	for( int i = 0; i < 19; i++ ){
        		for( int j = 0; j < 19; j++ ){
        			cnt++;
        			filewriter.write( String.valueOf( getReceptiveFieldCellValue(i,j) ) );            			
        			filewriter.write( " " );            			
        		}
        	}
        	for( int i = 0; i < 19; i++ ){
        		for( int j = 0; j < 19; j++ ){
        			filewriter.write( String.valueOf( getEnemiesCellValue(i,j) ) );
        			cnt++;
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
			
	private boolean read_flag( String filename ){
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
	
	private void write_reward( float x ){
		try{
			File file = new File( "./cnn/dat/reward.dat" );
			FileWriter filewriter = new FileWriter( file );
			
			String str = String.format( "%.10f" , x );
			filewriter.write( str );
			filewriter.write( "\n" );
			
			filewriter.close();
		} catch( IOException e ){
			System.out.println(e);
		}
	}
	
	private void write_actions( int x ){
		try{
			File file = new File( "./cnn/dat/teacher.dat" );
			FileWriter filewriter = new FileWriter( file );
			
			String str = String.format( "%d" , x );
			filewriter.write( str );
			filewriter.write( "\n" );
			
			filewriter.close();
		} catch( IOException e ){
			System.out.println(e);
		}
	}	
	
	private void write_vel( float vx , float vy ){
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
	
	private void write_pos( float x , float y ){
		try{
			File file = new File( "./cnn/dat/pos.dat" );
			FileWriter filewriter = new FileWriter( file );
			
			String str = String.format( "%.10f %.10f" , x , y );
			filewriter.write( str );
			filewriter.write( "\n" );
			
			filewriter.close();
		} catch( IOException e ){
			System.out.println(e);
		}
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

   private void decode( int x ){
		for( int i = 0; i < 6; i++ ){
		    if( x % 2 == 0 ) action[i] = false;
		    else action[i] = true;
		    x /= 2;
		}
    }
   
   private int convert( int x ){
	   int res = 16;
	   if( x % 2 == 0 ){
		   res += 1;
	   } else {
		   res += 2;
	   }
	   x /= 2;
	   if( x % 2 == 1 ){
		   res += 8;
	   }
	   return res;
   }
		
	private int read(){
	    try{
	    	File file = new File( "./cnn/dat/pred.dat" );
	    	Scanner sc = new Scanner( file );

	    	return Integer.parseInt( sc.next() ); 
	    } catch( FileNotFoundException e ){
	    	System.out.println( e );
	    } catch( IOException e ){
	    	System.out.println( e );
	    }
	    
	    return -1;
	}

	private void exec( List<String> command ){
		ProcessBuilder pb = new ProcessBuilder( command );
		
		try{
			Process proc = pb.start();
			int exitCode = proc.waitFor();
		} catch( IOException e ){
			e.printStackTrace();
		} catch( InterruptedException e ){
			e.printStackTrace();
		}
	}
	
	public void wait_done(){
		while( get_request() != 0 ){
			try{
				Thread.sleep( 2 );
			} catch( InterruptedException e ){
				System.out.println( e );
			}
		}
	}
	
	private void read_actions(){
	    try{
	    	File file = new File( "./cnn/dat/tekitou.dat" );
	    	Scanner sc = new Scanner( file );

	    	while( sc.hasNext() ){
	    		actions.add( Integer.parseInt( sc.next() ) );
	    	}
	    } catch( FileNotFoundException e ){
	    	System.out.println( e );
	    } catch( IOException e ){
	    	System.out.println( e );
	    }
	}
	
	private void int_to_action( int n ){
		action[Mario.KEY_LEFT] = (n == 3 || n == 7 || n == 10);
		action[Mario.KEY_RIGHT] = (n == 2 || n == 6 || n == 9);
		action[Mario.KEY_DOWN] = (n == 4 || n == 8);
		action[Mario.KEY_JUMP] = (n == 0 || (n > 4 && n < 11));
		action[Mario.KEY_SPEED] = (n == 1 || n == 5 || n == 9 || n == 10);
	}
	
	private boolean check_action_to_int( int act , int x ){
		int_to_action( x );
		for( int i = 0; i < 5; i++ ){
			if( (act % 2 == 1) != action[i] ){
				return false;
			}
			act /= 2;
		}
		return true;
	}
	
	private int action_to_int( int act ){
		for( int i = 0; i < 12; i++ ){
			if( check_action_to_int( act , i ) ){
				return i;
			}
		}
		return -1;
	}
	
	private void do_action( int x ){
		for( int i = 0; i < 5; i++ ){
			if( x % 2 == 1 ){
				action[i] = true;
			} else {
				action[i] = false;
			}
			x /= 2;
		}
	}
	
	public boolean[] getAction(){
		
		if( learning && 470 < fcnt && fcnt < 700 ){
			if( fcnt < actions.size() ){
				int x = action_to_int( actions.get(fcnt) );
				
				System.out.println( x );

				if( x != -1 ){
					write();
					write_vel( marioFloatPos[0] - px , marioFloatPos[1] - py );
					write_actions( x );
					
					set_request( 6 );
				
					wait_done();
				}
				
				do_action( actions.get(fcnt) );
			}
		} else if( dbl ){
			do_action( actions.get(fcnt) );
			System.out.println( fcnt );
		} else if( marioStatus != 2 || fcnt % 4 == 3 ){
			// float cur = intermediateReward + distancePassedPhys / 4.0f;
			int cur = (int)intermediateReward;
	
			write();
			write_vel( marioFloatPos[0] - px , marioFloatPos[1] - py );
			write_pos( marioFloatPos[0] , marioFloatPos[1] );
			
			float rw = 0.0f;
			if( 1800.0f < marioFloatPos[0] && marioFloatPos[0] < 2110.f && marioFloatPos[1] > 100.0f ){
				System.out.print( "-" );
				rw = -10.0f;
			} else if( 1640.0f < marioFloatPos[0] && marioFloatPos[0] < 2100.0f && marioFloatPos[1] >= 130.0f ){
				rw = -1.0f;
				System.out.print( "_" );
			} else if( cur < prv ){
				rw = -1.0f;
			} else if( px < marioFloatPos[0] ){
				rw = 1.0f;
			} else {
				rw = -1.0f;
			}			
			/*
			if( 1800.0f < marioFloatPos[0] && marioFloatPos[0] < 2110.f && marioFloatPos[1] > 100.0f ){
				System.out.print( "-" );
				rw = -10.0f;
			} else if( 1640.0f < marioFloatPos[0] && marioFloatPos[0] < 2100.0f ){
				if( 80.0f < marioFloatPos[1] && marioFloatPos[1] < 130.0f ){
					System.out.print( "+" );
					rw = 1.0f;
				} else if( marioFloatPos[1] <= 80.0f ){
					System.out.print( "*" );
					rw = 10.0f;
				} else {
					rw = -1.0f;
					System.out.print( "_" );
				}
			} else if( cur < prv ){
				rw = -1.0f;
			} else if( px < marioFloatPos[0] ){
				rw = 1.0f;
			} else {
				rw = -1.0f;
			}
			*/			

			/*
			if( cur > prv ){
				rw = 1.0f;
			} else if( cur < prv ){
				rw = -1.0f;
			} else if( px < marioFloatPos[0] ){
				rw = 0.1f;
			} else {
				rw = -0.1f;
			}
			*/
			write_reward( rw );
			
			if( marioStatus == 2 ){
				set_request( 1 );
			} else {
				set_request( 9 );
			}
			
			wait_done();
	
			int_to_action( read() );
			
			// int act = convert( read() );
			// decode( act );
			
			prv = cur;
		}
		
		px = marioFloatPos[0];
		py = marioFloatPos[1];
		
		fcnt++;
		
	    return action;
	}
	
	public float reward(){
		return intermediateReward;
	}
	
	public float xpos(){
		return marioFloatPos[0];
	}
}
