import time
from ai import JAI

print( "start" )

ai = JAI( '/home/joisino/work/le2sw/marioAI/cnn/dat/ai.dat' )
# ai = JAI()

frame = 0;

while True:
    state = open( '/home/joisino/work/le2sw/marioAI/cnn/dat/request.dat' , 'r' ).readline();
    data = state.split( ' ' )

    if len( data ) > 0 and len( data[0] ) > 0:
        if int(data[0]) == 1:
            ai.pred()
            if frame % 8 == 0:
                ai.add()
            frame += 1
            ai.update()
        if int(data[0]) == 9:
            ai.end()
            ai.add()
            frame = 1
            ai.update()

        if int(data[0]) == 6:
            ai.teach()

        if int(data[0]) == 4:
            ai.calc_state()

        if int(data[0]) == 2:
            ai.learn()

    time.sleep(0.002)
