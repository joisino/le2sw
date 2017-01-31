import numpy as np
from numpy.random import *
from mlp import MLP
import chainer
from chainer import Variable, training
from chainer import iterators, optimizers, serializers
import chainer.functions as F
import chainer.links as L
from chainer.training import extensions

class NeuralAI:
    def __init__(self, file = None, lr = 0.01, size = 9):
        self.size = size
        self.S = size * size
        self.mlp = MLP(self.size)
        self.lr = lr
        self.model = L.Classifier( self.mlp, F.mean_squared_error, F.mean_squared_error )
        self.optimizer = optimizers.Adam()
        self.optimizer.setup( self.model )
        self.data_reset()
        if file:
            serializers.load_npz(file, self.mlp)

    def save(self, file):
        serializers.save_npz(file, self.mlp)

    def load(self, file):
        serializers.load_npz(file, self.mlp)

    def start_game(self, turn):
        self.turn = turn
        self.history = [ [], [], [] ]

    def data_reset(self):
        self.states = []
        self.actions = []
        self.results = []
        
    def update(self):

        data_size = len( self.states )

        xs = []
        ys = []
        for state, action, result in zip( self.states, self.actions, self.results ):
            res = self.mlp( Variable(np.array([state],dtype=np.float32)) ).data[0]
            t = np.array( res )
            if result == 1:
                t[action] = 1
            else:
                t[action] = 0
            ys.append( t )

        dataset = chainer.datasets.TupleDataset( np.array( self.states, np.float32 ), ys )

        train_iter = iterators.SerialIterator( dataset, batch_size = 100, shuffle = True )

        updater = training.StandardUpdater( train_iter, self.optimizer )
        trainer = training.Trainer( updater, (100, 'epoch'), out = 'result' )

        trainer.extend( extensions.LogReport() )
        trainer.extend( extensions.PrintReport( ['epoch', 'main/loss'] ) )
        trainer.run()
            
        self.data_reset()

    def append(self, win_history, lose_history, draw=False):
        self.states += win_history[0] + lose_history[0]
        self.actions += win_history[1] + lose_history[1]
        if draw:
            self.results += [-1] * ( len(win_history[0]) + len(lose_history[0]) )
        else:
            self.results += [1] * len(win_history[0]) + [-1] * len(lose_history[0])

    def calc_state(self, bd):
        s = np.array( [] )
        s = np.append(s, bd.board[self.turn])
        s = np.append(s, bd.board[1-self.turn])
        s = np.append(s, bd.empty_position())
        s = np.append(s, np.ones([bd.size, bd.size]))
        s = s.astype( np.float32 )
        s = s.reshape( [1, 4, bd.size, bd.size] )
        return s

    def get_probs(self, output, va):
        res = np.array( [] )
        for s in va:
            res = np.append( res, output[s] )
        res /= res.sum()
        return res
        
    def put(self, bd):
        s = self.calc_state(bd)
        va = bd.valid_array(self.turn)

        if len(va) == 0:
            # print( 'pass' )
            bd.pas()
            return

        res = self.mlp( Variable(s) ).data[0]
        # print( res )
        res = self.get_probs( res, va )
        
        act = np.random.choice( va, p=res )

        y = int(act/bd.size)
        x = int(act%bd.size)
        # print( y, x )
        bd.put( x, y, self.turn )
            
        self.history[0].append( s[0] )
        self.history[1].append( act )
