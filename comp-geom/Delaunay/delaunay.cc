#include <iostream>
#include <algorithm>
#include <cstddef>
#include <cstdlib>
#include <ctime>
#include <vector>
#include <utility>
#include <cstdio>

#include "planar.h"
#include "plotfunc.h"

// random generator function.
ptrdiff_t myrandom(ptrdiff_t i) { 
	srand(time(NULL));
	return rand() % i; 
}

// pointer object to it.
ptrdiff_t (*p_myrandom)(ptrdiff_t) = myrandom;

int highestPoint(Point* p, int length, Point& p0) {
	int maxIdx = -1;
	int i;

	p0.setX(-INFTY);
	p0.setY(-INFTY);

	for (i = 0; i < length; ++i) {
		if (doubleGT(p[i].getY(), p0.getY())) {
			p0 = p[i];
			maxIdx = i;
		} else if (doubleEqual(p[i].getY(), p0.getY()) && doubleGT(p[i].getX(), p0.getX())) {
			p0 = p[i];
			maxIdx = i;
		}
	}

	return maxIdx;
}

Point setPointMinusOne(Point* p, int length) {
	int i;
	Point res;
	double minY = INFTY;

	res.setX(INFTY);
	for (i = 0; i < length; ++i) {
		if (p[i].getY() < minY) 
			minY = p[i].getY();
	}
	res.setY(minY - 5);
	
	return res;
}

Point setPointMinusTwo(Point* p, int length) {
	int i;
	Point res;
	double maxY = -INFTY;

	res.setX(-INFTY);
	for (i = 0; i < length; ++i) {
		if (p[i].getY() > maxY) 
			maxY = p[i].getY();
	}
	res.setY(maxY + 5);

	return res;
} 

std::vector<Triangle> delaunayTriangulation(Point* p, int n, std::vector<int>& mapP) {
	int r, i;
	Point p0;

	int indexP0 = highestPoint(p, n, p0);		
	Point pm1 = setPointMinusOne(p, n);	
	Point pm2 = setPointMinusTwo(p, n);


	Triangle t0(p0, pm1, pm2);
	
	std::vector<Triangle> resTriangles;
	DAG searchStructure(t0);
	
/*
	for (r = 0; r < n; ++r) {
		Triangle curTriangle;
		int curIdx = mapP.at(r);

		curTriangle = searchStructure.searchTriangleContainsPoint(p[r]);	// TODO



		if (curTriangle.interior(p[r])) {
			// p_r lies in the interior of the triangle.
			Triangle newTriangle[3];
			
			newTriangle = curTriangle.splitByInteriorPoint(p[curIdx]);		// TODO
	
			Edge edge1(curTriangle.getVertex1(), curTriangle.getVertex2());
			Edge edge2(curTriangle.getVertex2(), curTriangle.getVertex3());
			Edge edge3(curTriangle.getVertex3(), curTriangle.getVertex1());

			legalizeEdge(p[curIdx], edge1, searchStructure);		// TODO
			legalizeEdge(p[curIdx], edge2, searchStructure);
			legalizeEdge(p[curIdx], edge3, searchStructure);			
		} else {
			// p_r lies on the boundary of the triangle.
			Edge commonEdge = curTriangle.getIntersectEdge(p[curIdx]);	// TODO

			Triangle adjacentTriangle = searchStructure.searchByCommonEdge(curTriangle, commonEdge);		// TODO

			Point pk = curTriangle.getVertexByEdge(commonEdge);	// TODO
			Point pl = adjacentTriangle.getVertexByEdge(commonEdge);	// TODO

			Triangle newTriangle1[2];
			Triangle newTriangle2[2];

			newTriangle1 = curTriangle.splitByBoundaryPoint(pk, p[curIdx]);	// TODO
			newTriangle2 = adjacentTriangle.splitByBoundaryPoint(pl, p[curIdx]);

			Edge e1(commonEdge.getEndpoint1(), pl);
			Edge e2(pl, commonEdge.getEndpoint2());
			Edge e3(commonEdge.getEndpoint2(), pk);
			Edge e4(pk, commonEdge.getEndpoint1());

			legalizeEdge(p[curIdx], e1, searchStructure);
			legalizeEdge(p[curIdx], e2, searchStructure);
			legalizeEdge(p[curIdx], e3, searchStructure);
			legalizeEdge(p[curIdx], e4, searchStructure);
		}
	}

	resTriangles = searchStructure.extractLeafTriangles();

	removeVertexAndIncidentEdges(resTriangles, pm1);	// TODO
	removeVertexAndIncidentEdges(resTriangles, pm2);
*/
	return resTriangles;
}


int main() {
	int n;
	double tmp1, tmp2;
	int i;
	Point* p;
	std::vector<int> mapP;

	std::vector<Triangle> resTriangles;

	std::cin >> n;
	while (n != 0) {
		p = new Point[n];
		// Input the coordinates of points.
		for (i = 0; i < n; ++i) {
			std::cin >> tmp1 >> tmp2;
			p[i].setX(tmp1);
			p[i].setY(tmp2);
			mapP.push_back(i);
		}

		// Plot the initial setting.
		plotInputPoints(p, n);

		// Random shuffle the indices of points.
		random_shuffle(mapP.begin(), mapP.end(), p_myrandom);

		// Perform the Delaunay Triangulation.
		resTriangles = delaunayTriangulation(p, n, mapP);

		// Plot the generated Delaunay Triangulation.
		//plotDelaunayTriangles(resTriangles);


		// Clear for processing the next input set.
		delete[] p;
		mapP.clear();
		
		std::cin >> n;
	}



	return 0;
}
