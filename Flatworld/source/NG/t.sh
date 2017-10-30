#!/bin/bash
I=100
while [ $I -ne 0 ]; do
	./test32 >> ./results/model32.dat
	echo I $I
	let I-=1
done
echo "Complete"
