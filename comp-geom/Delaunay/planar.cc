#include "planar.h"
#include <iostream>

// For Vector2D
double Vector2D::magnitude(void) {
	return sqrt(coordinate[0] * coordinate[0] + coordinate[1] * coordinate[1]);
}

double Vector2D::innerProduct(Vector2D v) {
	return coordinate[0] * v.getX() + coordinate[1] * v.getY();
}

double Triangle::getAngle1(void) {
	Vector2D v12(endpoint[0], endpoint[1]);
	Vector2D v13(endpoint[0], endpoint[2]);

	return acos(v12.innerProduct(v13) / (v12.magnitude() * v13.magnitude()));
}

double Triangle::getAngle2(void) {
	Vector2D v21(endpoint[1], endpoint[0]);
	Vector2D v23(endpoint[1], endpoint[2]);

	return acos(v21.innerProduct(v23) / (v21.magnitude() * v23.magnitude()));
}

double Triangle::getAngle3(void) {
	Vector2D v31(endpoint[2], endpoint[0]);
	Vector2D v32(endpoint[2], endpoint[1]);

	return acos(v31.innerProduct(v32) / (v31.magnitude() * v32.magnitude()));
}

// For DAG
void DAG::dump(void) {
	
}
