all: delaunay

delaunay:  delaunay.o planar.o
		g++ -o delaunay delaunay.o planar.o -lboost_system -lboost_iostreams

delaunay.o:  delaunay.cc
		g++ -c delaunay.cc

planar.o:  planar.cc
		g++ -c planar.cc


clean:
	rm -rf *.o delaunay
