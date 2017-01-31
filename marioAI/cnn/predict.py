import numpy as np
from cnn import CNN
import chainer
from chainer import Variable, training
from chainer import iterators, optimizers, serializers
import chainer.functions as F
import chainer.links as L
from convert import convert_field, convert_enemy

def pred():
    state = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/state.dat' , 'r' ).readline();
    data = state.split( ' ' )

    assert( len(data) == 19 * 19 * 2 )

    field = []
    for i in range( 19 * 19 ):
        field.append( int(data[i]) )

    enemy = []
    for i in range( 19 * 19 ):
        enemy.append( int(data[ 19 * 19 + i ]) )

    res = convert_field( field )
    res.extend( convert_enemy( enemy ) )
    res = np.array( res , np.float32 )

    res = res.reshape( 1 , 24 , 19 , 19  )

    cnn = CNN()
    ans = cnn( Variable( res ) );

    f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/pred.dat' , 'w' )
    f.write( str( np.argmax( ans.data ) ) )
    f.write( '\n' )
    f.close()

    f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/request.dat' , 'w' )
    f.write( "0\n" )
    f.close()
    
