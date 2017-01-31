import numpy as np
import chainer
from chainer import Chain
import chainer.functions as F
import chainer.links as L
from chainer import Variable

class CNN(Chain):
    def __init__(self):
        super(CNN, self).__init__(
            l1=L.Convolution2D(26, 64, ksize=5, pad=2, stride=1),
            l2=L.Convolution2D(64, 32, ksize=3, pad=1, stride=1),
            l3=L.Linear(19*19*32, 128),
            l4=L.Linear(128,12),
        )

    def __call__(self, x):
        h1 = F.relu(self.l1(x))
        h2 = F.relu(self.l2(h1))
        h3 = F.relu(self.l3(h2))
        e = self.l4(h3)
        return e
        
