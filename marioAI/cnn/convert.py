A = [ -24, 2, -60, -62, -85, 61, 5, 0 ]
B = [ 0, -31, 80, 95, 82, 97, 81, 96, 84, 93, 99, 91, 13, 2, 3, 25 ]

def convert( x, C ):
    res = []
    for c in C:
        for w in x:
            if c == w:
                res.append( 1 )
            else:
                res.append( 0 )
    return res

def convert_field( x ):
    return convert( x , A )

def convert_enemy( x ):
    return convert( x , B )
