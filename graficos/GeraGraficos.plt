set term postscript eps enhanced color
set output "custo.eps"
set datafile separator ";"
set xlabel "numBlocos"
set ylabel "custo" 
set title "Custo x numero de blocos"
plot "BrFS.dat" using 2:3 title "BrFS" with lines,\
     "DFS.dat" using 2:3 title "DFS" with lines,\
     "UCS.dat" using 2:3 title "UCS" with lines,\
     "IDS.dat" using 2:3 title "IDS" with lines,\
     "A*.dat" using 2:3 title "A*" with lines,\
     "IDA*.dat" using 2:3 title "IDA*" with lines

set term postscript eps enhanced color
set output "profundidade.eps"
set datafile separator ";"
set ylabel "profundidade" 
set title "Profundidade x numero de blocos"
plot "BrFS.dat" using 2:4 title "BrFS" with lines,\
     "DFS.dat" using 2:4 title "DFS" with lines,\
     "UCS.dat" using 2:4 title "UCS" with lines,\
     "IDS.dat" using 2:4 title "IDS" with lines,\
     "A*.dat" using 2:4 title "A*" with lines,\
     "IDA*.dat" using 2:4 title "IDA*" with lines

set term postscript eps enhanced color
set output "tamanho.eps"
set datafile separator ";"
set ylabel "tamanho" 
set title "Tamanho x numero de blocos"

plot "BrFS.dat" using 2:5 title "BrFS" with lines,\
     "DFS.dat" using 2:5 title "DFS" with lines,\
     "UCS.dat" using 2:5 title "UCS" with lines,\
     "IDS.dat" using 2:5 title "IDS" with lines,\
     "A*.dat" using 2:5 title "A*" with lines,\
     "IDA*.dat" using 2:5 title "IDA*" with lines

set term postscript eps enhanced color
set output "visitados.eps"
set datafile separator ";"
set ylabel "visitados" 
set title "Nos visitados x numero de blocos"

plot "BrFS.dat" using 2:6 title "BrFS" with lines,\
     "DFS.dat" using 2:6 title "DFS" with lines,\
     "UCS.dat" using 2:6 title "UCS" with lines,\
     "IDS.dat" using 2:6 title "IDS" with lines,\
     "A*.dat" using 2:6 title "A*" with lines,\
     "IDA*.dat" using 2:6 title "IDA*" with lines

set term postscript eps enhanced color
set output "gerados.eps"
set datafile separator ";"
set ylabel "gerados" 
set title "Nos gerados x numero de blocos"

plot "BrFS.dat" using 2:7 title "BrFS" with lines,\
     "DFS.dat" using 2:7 title "DFS" with lines,\
     "UCS.dat" using 2:7 title "UCS" with lines,\
     "IDS.dat" using 2:7 title "IDS" with lines,\
     "A*.dat" using 2:7 title "A*" with lines,\
     "IDA*.dat" using 2:7 title "IDA*" with lines

set term postscript eps enhanced color
set output "fatorramificacao.eps"
set datafile separator ";"
set ylabel "Fator de ramificacao" 
set title "Fator de ramificacao x numero de blocos"

plot "BrFS.dat" using 2:8 title "BrFS" with lines,\
     "DFS.dat" using 2:8 title "DFS" with lines,\
     "UCS.dat" using 2:8 title "UCS" with lines,\
     "IDS.dat" using 2:8 title "IDS" with lines,\
     "A*.dat" using 2:8 title "A*" with lines,\
     "IDA*.dat" using 2:8 title "IDA*" with lines

set term postscript eps enhanced color
set output "tempo.eps"
set datafile separator ";"
set ylabel "tempo" 
set title "Tempo de execucao x numero de blocos"

plot "BrFS.dat" using 2:9 title "BrFS" with lines,\
     "DFS.dat" using 2:9 title "DFS" with lines,\
     "UCS.dat" using 2:9 title "UCS" with lines,\
     "IDS.dat" using 2:9 title "IDS" with lines,\
     "A*.dat" using 2:9 title "A*" with lines,\
     "IDA*.dat" using 2:9 title "IDA*" with lines

set term postscript eps enhanced color
set output "compAeIDA.eps"
set datafile separator ";"
set ylabel "A* x IDA*" 
set title "Fator de ramificacao x numero de blocos"

plot "A*.dat" using 2:8 title "A*" with lines,\
     "IDA*.dat" using 2:8 title "IDA*" with lines

set term postscript eps enhanced color
set output "compAeIDAGerados.eps"
set datafile separator ";"
set ylabel "A* x IDA*"
set title "Nos visitados x numero de blocos"

plot "A*.dat" using 2:6 title "A*" with lines,\
     "IDA*.dat" using 2:6 title "IDA*" with lines

