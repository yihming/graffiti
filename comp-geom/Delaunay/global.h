#ifndef GLOBAL_H
#define GLOBAL_H

#define EPSILON 0.00001 
#define PI 3.1415926
#define INFTY 1.7E100

inline bool doubleEqual(double a, double b) {
	if (fabs(a - b) < EPSILON) return true;
	else return false;
}

inline bool doubleGT(double a, double b) {
	if (a - b >= EPSILON) return true;
	else return false;
}

inline bool doubleLT(double a, double b) {
	if (a - b <= -EPSILON) return true;
	else return false;
}

#endif
