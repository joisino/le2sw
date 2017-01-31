from board import Board
from neuralai import NeuralAI
from numpy.random import *
from random_test import random_test
import numpy as np

f = open( 'cnt.log', 'w' )
f.close()
f = open( 'bench.log', 'w' )
f.close()

nai = NeuralAI()
# nai.load( 'nai.dat' )

nai.save( 'dat/his0.dat' )

prv_bench = 50
maxn = 1

for i in range(1000000000):
    cnt = [0, 0, 0]

    for j in range(100):
        print(i, j)
        bd = Board()
        
        aai = NeuralAI()
        filen = randint(0,maxn+1)
        filename = "dat/his%s.dat" % filen
        print( filename )

        if filen < maxn:
            aai.load(filename)

        nai_turn = randint(0, 2)
        print( nai_turn )

        nai.start_game( nai_turn )
        aai.start_game( 1-nai_turn )

        ais = []
    
        if nai_turn == 0:
            ais = [ nai, aai ]
        else:
            ais = [ aai, nai ]
        

        while True:
            for k in range(2):
                ais[k].put(bd)
                if bd.end:
                    break
            if bd.end:
                break

        res = bd.judge()

        if res == nai_turn:
            print( 'win' )
            nai.append( nai.history, aai.history )
            cnt[0] += 1
        elif res == 1-nai_turn:
            print( 'lose' )
            nai.append( aai.history, nai.history )
            cnt[1] += 1
        else:
            print( 'draw' )
            nai.append( nai.history, aai.history, True )
            cnt[2] += 1

        print( cnt )
        
        bd.show()


    nai.update()
    nai.save( 'nai.dat' )

    if i % 10 == 9:
        bench_res = random_test()
        
        f = open( 'bench.log', 'a' )
        f.write( '%s\n' % bench_res )
        f.close()
        
        #if bench_res > prv_bench:
        filename = "dat/his%s.dat" % maxn
        nai.save( filename )
        prv_bench = bench_res
        maxn += 1

    f = open( 'cnt.log', 'a' )
    f.write( '%s,%s,%s\n' % ( cnt[0], cnt[1], cnt[2] ) )
    f.close()
