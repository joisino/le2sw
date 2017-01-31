package ownaiutils;

import java.util.Random;

public class ZobristHash {
	private int table_enemy[][];
	private int table_field[][];
	private int cur_enemy[];
	private int cur_field[];
	private Random rnd = new Random();
	
	final private int size = 361;
		
	public ZobristHash(){
		table_enemy = new int[size][256];
		table_field = new int[size][256];
		
		for( int i = 0; i < size; i++ ){
			for( int j = 0; j < 256; j++ ){
				table_enemy[i][j] = rnd.nextInt( Integer.MAX_VALUE );
				table_field[i][j] = rnd.nextInt( Integer.MAX_VALUE );
			}
		}
		
		cur_enemy = new int[size];
		cur_field = new int[size];
	}
	
	public void set_enemy( int y , int x , int z ){
		cur_enemy[y*18+x] = z + 128;
	}
	
	public void set_field( int y , int x , int z ){
		cur_field[y*18+x] = z + 128;
	}
	
	public int calc(){
		int res = 0;
		for( int i = 0; i < size; i++ ){
			res = res ^ table_enemy[i][ cur_enemy[i] ]; 
			res = res ^ table_field[i][ cur_field[i] ]; 
		}
		return res;
	}
}
