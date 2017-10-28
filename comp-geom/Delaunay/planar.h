#ifndef PLANAR_H
#define PLANAR_H

#include <vector>
#include <map>
#include <utility>
#include <cmath>

#include "global.h"



class Point {
private:
	double x;
	double y;
public:
	Point() {}
	Point(double newX, double newY): x(newX), y(newY) {}
	~Point() {}
	double getX(void) { return x; }
	double getY(void) { return y; }
	void setX(double newX) { x = newX; }
	void setY(double newY) { y = newY; }
	void setCoordinate(double newX, double newY) { setX(newX); setY(newY); }
	void operator= (Point p) { this->x = p.getX(); this->y = p.getY(); }

	bool operator== (Point& p) {
		if (fabs(p.getX() - x) < EPSILON && fabs(p.getY() - y) < EPSILON)  return true;
		else false;
	}
};

class Vector2D {
private:
	double coordinate[2];
public:
	Vector2D(double a, double b) { coordinate[0] = a; coordinate[1] = b; }
	
	Vector2D(Point p1, Point p2) {
		coordinate[0] = p1.getX() - p2.getX();
		coordinate[1] = p1.getY() - p2.getY();
	}

	~Vector2D() {  }
	double getX(void)  { return coordinate[0]; }
	double getY(void)  { return coordinate[1]; }
	void setCoordinate(double a, double b)  { coordinate[0] = a; coordinate[1] = b; }
	void setCoordinate(Point p1, Point p2) { 
		coordinate[0] = p1.getX() - p2.getX();
		coordinate[1] = p1.getY() - p2.getY(); 
	}
	void operator= (Vector2D v) { this->coordinate[0] = v.getX(); this->coordinate[1] = v.getY(); }

	double magnitude(void);
	double innerProduct(Vector2D v);
};

class Edge {
private:
	Point endpoint[2];
public:
	Edge(Point p1, Point p2)  { endpoint[0] = p1;  endpoint[1] = p2; }

	~Edge() {  }

	Point getEndPoint1(void)  { return endpoint[0]; }
	Point getEndPoint2(void)  { return endpoint[1]; }

	void setEndPoint1(Point p)  { endpoint[0] = p; }
	void setEndPoint2(Point p)  { endpoint[1] = p; }
};

class Triangle {
private:
	Point endpoint[3];
public:
	Triangle() {  }
	Triangle(Point p1, Point p2, Point p3) { endpoint[0] = p1; endpoint[1] = p2; endpoint[2] = p3; }
	~Triangle() { }

	void setVertex1(Point p) { endpoint[0] = p; }
	void setVertex2(Point p) { endpoint[1] = p; }
	void setVertex3(Point p) { endpoint[2] = p; }

	Point getVertex1(void) { return endpoint[0]; }
	Point getVertex2(void) { return endpoint[1]; }
	Point getVertex3(void) { return endpoint[2]; }
	
	double getAngle1(void);
	double getAngle2(void);
	double getAngle3(void);

	bool hasVertex(Point& p) {
		if (endpoint[0] == p || endpoint[1] == p || endpoint[2] == p)  return true;
		else false;
	}

	void operator= (Triangle t) {
		endpoint[0] = t.getVertex1();
		endpoint[1] = t.getVertex2();
		endpoint[2] = t.getVertex3();
	}

	bool operator== (Triangle t) {
		if (t.hasVertex(endpoint[0]) && t.hasVertex(endpoint[1]) && t.hasVertex(endpoint[2]))  return true;
		else  return false;
	}
};

class Node {
private:
	Triangle element;
	bool leafFlag;
public:
	Node() { leafFlag = false; }
	Node(Triangle t) { element = t; leafFlag = false; }
	~Node() {  }
	Triangle getElement(void)  { return element; }
	void setElement(Triangle t)  { element = t; }
	bool isLeaf(void) { return leafFlag; }
	void setLeaf(void)  { leafFlag = true; }
};

class DAG {
private:
	std::map<int, Node> nodeMap;
	std::map<int, std::vector<int> > adjacencyMap;
	Node root;
	int idxCnt;
public:
	DAG() { idxCnt = 0; }
	DAG(Triangle t) {
		root.setElement(t);
		root.setLeaf();
		nodeMap.insert(std::pair<int, Node>(0, root));
		std::vector<int> vec;
		vec.clear();
		adjacencyMap.insert(std::pair<int, std::vector<int> >(0, vec));
		++idxCnt;
	}
	~DAG() { nodeMap.clear(); adjacencyMap.clear(); idxCnt = 0; }

	void insertNode(Node n);	// TODO
	void insertEdge(Node startN, Node endN);	// TODO
	
	void removeNode(Node n);
	void removeEdge(Node startN, Node endN);

	void dump(void);

	std::vector<Triangle> extractLeafTriangles(void);
};

#endif
