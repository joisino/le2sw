import numpy as np
from cnn import CNN
import chainer
from chainer import Variable, training
from chainer import iterators, optimizers, serializers
import chainer.functions as F
import chainer.links as L
from chainer.training import extensions
from convert import convert_field, convert_enemy

class JAI:
    def __init__(self, file = None, lr = 0.01 ):
        self.cnn = CNN()
        
        self.lr = lr
        self.model = L.Classifier( self.cnn, F.mean_squared_error, F.mean_squared_error )
        self.optimizer = optimizers.RMSpropGraves()
        self.optimizer.setup( self.model )

        self.num_act = 12

        self.cur_inp = np.array( [] )
        self.cur_out = np.array( [] )
        self.cur_act = 0
        self.prv_inp = np.array( [] )
        self.prv_out = np.array( [] )
        self.prv_act = 0

        self.ins = []
        self.tcs = []

        self.gamma = 0.99
        self.epsilon = 10 # epsilon greedy / choose random eplison percent
        
        if file:
            serializers.load_npz(file, self.cnn)

    def save(self, file):
        serializers.save_npz(file, self.cnn)

    def load(self, file):
        serializers.load_npz(file, self.cnn)

    def data_reset(self):
        self.ins = []
        self.tcs = []

    def set_input(self):
        state = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/state.dat' , 'r' ).readline();
        data = state.split( ' ' )

        if len(data) != 19 * 19 * 2:
            return False

        field = []
        for i in range( 19 * 19 ):
            field.append( int(data[i]) )

        enemy = []
        for i in range( 19 * 19 ):
            enemy.append( int(data[ 19 * 19 + i ]) )

        state = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/vel.dat' , 'r' ).readline();
        data = state.split( ' ' )

        if len(data) != 2:
            return False

        vel = []
        for i in range(2):
            for j in range( 19 * 19 ):
                vel.append( float(data[i]) )

        inp = convert_field( field )
        inp.extend( convert_enemy( enemy ) )
        inp.extend( vel )
        inp = np.array( inp , np.float32 )
        inp = inp.reshape( 1 , 26 , 19 , 19  )

        self.cur_inp = np.array( inp[0] )

        return True
        
    def pred(self):
        if not self.set_input():
            return
        
        ans = self.cnn( Variable( np.array( [ self.cur_inp ] ) ) );

        self.cur_out = np.array( ans.data[0] )

        if np.random.randint(100) < self.epsilon:
            self.cur_act = np.random.randint( self.num_act )
        else:
            # self.cur_act = np.random.choice( np.arange( self.cur_out.size ) , p=F.softmax(ans).data[0] )
            self.cur_act = np.argmax( ans.data[0] )

        state = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/pos.dat' , 'r' ).readline();
        data = state.split( ' ' )
        if 1850.0 < float( data[0] ) and float( data[0] ) < 2110.0 and float( data[1] ) < 130.0:
            # temp = np.random.randint( 4 )
            temp = 1000
            if temp == 0:
                self.cur_act = 0
            elif temp == 1:
                self.cur_act = 2
            elif temp == 2:
                self.cur_act = 11
            elif temp == 3:
                self.cur_act = 6

            print( "#" )
                    
        if 1700.0 < float( data[0] ) and float( data[0] ) < 2110.0 and float( data[1] ) > 130.0:
            # temp = np.random.randint( 3 )
            temp = 1000
            if temp == 0:
                self.cur_act = 0
            elif temp == 1:
                self.cur_act = 3
            elif temp == 2:
                self.cur_act = 7
                
            print( "." )

        # print( self.cur_act )
        # print( F.softmax(ans).data[0] )
        # print( self.cur_out )

        f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/pred.dat' , 'w' )
        f.write( str( self.cur_act ) )
        f.write( '\n' )
        f.close()

        f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/request.dat' , 'w' )
        f.write( "0\n" )
        f.close()

    def calc_state(self):
        if not self.set_input():
            return
        
        ans = self.cnn( Variable( np.array( [ self.cur_inp ] ) ) );

        self.cur_out = np.array( ans.data[0] )

        od = np.argsort( self.cur_out )
        
        f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/pred.dat' , 'w' )
        for i in range( od.shape[0] ):
            f.write( str( od[ od.shape[0] - 1 - i ] ) )
            f.write( ' ' )
        f.write( '\n' )
        f.close()

        f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/val.dat' , 'w' )
        f.write( str( np.max( self.cur_out ) ) )
        f.write( '\n' )
        f.close()

        f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/request.dat' , 'w' )
        f.write( "0\n" )
        f.close()
        

    def teach( self ):
        if not self.set_input():
            return
        
        ans = self.cnn( Variable( np.array( [ self.cur_inp ] ) ) );

        self.cur_out = np.array( ans.data[0] )
        
        state = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/teacher.dat' , 'r' ).readline();
        self.cur_act = int( state.split( ' ' )[0] )

        print( self.cur_act )

        t = np.array( self.cur_out )
        t[ self.cur_act ] = 20.0

        self.ins.append( np.array( self.cur_inp ) )
        self.tcs.append( t )

        f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/request.dat' , 'w' )
        f.write( "0\n" )
        f.close()

        
    def end(self):
        self.cur_inp = np.array( [] )
        self.cur_out = np.array( [] )
        self.cur_act = -1

        f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/request.dat' , 'w' )
        f.write( "0\n" )
        f.close()
        

    def add(self):
        if self.prv_inp.size == 0:
            return
        
        reward = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/reward.dat' , 'r' ).readline();
        if( len(reward) <= 10 ):
            return
        reward = float( reward.split( ' ' )[0] )

        t = np.array( self.prv_out )

        if self.cur_act == -1:
            t[ self.prv_act ] = reward
        else:
            t[ self.prv_act ] = reward + self.gamma * np.max( self.cur_out )

        self.ins.append( np.array( self.prv_inp ) )
        self.tcs.append( t )

        print( len( self.ins ) )
        print( self.cur_out )

    def update(self):
        self.prv_inp = np.array( self.cur_inp )
        self.prv_out = np.array( self.cur_out )
        self.prv_act = self.cur_act

        
    def learn(self):
        dataset = chainer.datasets.TupleDataset( np.array( self.ins, np.float32 ), self.tcs )

        train_iter = iterators.SerialIterator( dataset, batch_size = 100, shuffle = True )
        
        updater = training.StandardUpdater( train_iter, self.optimizer )
        trainer = training.Trainer( updater, (10, 'epoch'), out = 'result' )

        trainer.extend( extensions.LogReport() )
        trainer.extend( extensions.PrintReport( ['epoch', 'main/loss'] ) )
        trainer.run()
            
        self.data_reset()
        
        self.save( '/home/joisino/work/le2sw/marioAI/cnn/dat/ai.dat' )

        f = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/request.dat' , 'w' )
        f.write( "0\n" )
        f.close()

        print( "Done" )
