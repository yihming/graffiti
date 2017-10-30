#include <iostream>
using namespace std;

//$ @retval quantity=Force, unit=Si;
//$ @param m quantity = Mass, unit = Si;
//$ @param a quantity = Acc, unit=Si;
float comp_force(float m, float a) {
	return m * a;
}

int main() {
	cout << comp_force(4.0, 2.3) << endl;
	return 0;
}
