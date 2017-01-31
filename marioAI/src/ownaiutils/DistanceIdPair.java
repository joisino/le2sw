package ownaiutils;


public class DistanceIdPair implements Comparable{
    public float d = 0.0f;
    public int a = 0;
    public int b = 0;
 
    public DistanceIdPair(){
    	
    }
    
    public DistanceIdPair( float _d , int _a , int _b ){
    	d = _d;
    	a = _a;
    	b = _b;
    }
    
    public int compareTo( Object other ){
    	DistanceIdPair ot = (DistanceIdPair) other;
    	
    	if( this.d < ot.d ) return 1;
    	if( this.d == ot.d && this.a < ot.a ) return 1;
    	if( this.d == ot.d && this.a == ot.a && this.b < ot.b ) return 1;
    	if( this.d == ot.d && this.a == ot.a && this.b == ot.b ) return 0;
    	return -1;
    }    
}

