#include <iostream>



using namespace std;


class Force {
private:
	float  member;
public:
	Force() {}
	Force(const float newMember): member(newMember) {}
	operator float() { return member; }
	Force& operator=(const float newMember) {
		member = newMember;
		return *this;
	}
};

class Mass {
private:
	float  member;
public:
	Mass() {}
	Mass(const float newMember): member(newMember) {}
	operator float() { return member; }
	Mass& operator=(const float newMember) {
		member = newMember;
		return *this;
	}
};

class Acc {
private:
	float  member;
public:
	Acc() {}
	Acc(const float newMember): member(newMember) {}
	operator float() { return member; }
	Acc& operator=(const float newMember) {
		member = newMember;
		return *this;
	}
};


Force comp_force( Mass  m, Acc  a  ) {
    return m * a;

}



int main(  ) {
    cout << comp_force(4.0, 2.3) << endl;
    return 0;

}

