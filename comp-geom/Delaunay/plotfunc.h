#ifndef PLOTFUNC_H
#define PLOTFUNC_H

#include <vector>
#include <utility>
#include <iostream>
#include "planar.h"
#include "gnuplot-iostream.h"

void plotInputPoints(Point* p, int length) {
	Gnuplot gp;
	int i;
	
	gp << "set terminal png\n";

	std::vector<std::pair<double, double> > data;

	for (i = 0; i < length; ++i) {
		std::pair<double, double> tmp(p[i].getX(), p[i].getY());
		data.push_back(tmp);
	}

	gp << "set output 'output/initial.png'\n";
	gp << "plot '-' using 1:2 title 'Input Point'\n";
	gp.send(data);
}

void plotDelaunayTriangles(std::vector<Triangle> vt) {
	Gnuplot gp;
	int i;

	gp << "set terminal png\n";
	gp << "set output 'output/result.png'\n";

	std::vector<std::vector<std::pair<double, double> > > data;

	for (i = 0; i < vt.size(); ++i) {
		std::vector<std::pair<double, double> > d;
		d.clear();
		std::pair<double, double> tmp1(vt.at(i).getVertex1().getX(), vt.at(i).getVertex1().getY());
		std::pair<double, double> tmp2(vt.at(i).getVertex2().getX(), vt.at(i).getVertex2().getY());
		std::pair<double, double> tmp3(vt.at(i).getVertex3().getX(), vt.at(i).getVertex3().getY());

		d.push_back(tmp1);
		d.push_back(tmp2);
		d.push_back(tmp3);
		d.push_back(tmp1);

		data.push_back(d);

		if (i == 0) {
			gp << "plot '-' with line linecolor rgb 'blue', ";
			
		} else if (i == vt.size() - 1) {
			gp << "'-' with line linecolor rgb 'blue'\n";
		} else {
			gp << "'-' with line linecolor rgb 'blue', ";
		}
	}

	for (i = 0; i < data.size(); ++i) {
		gp.send(data.at(i));
	}
}

#endif
