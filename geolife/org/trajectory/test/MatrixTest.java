package org.trajectory.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.trajectory.util.Matrix;

public class MatrixTest {

	Matrix mat1 = new Matrix(3, 4, 5);
	Matrix mat2 = new Matrix(4, 1, 4);
	Matrix mat3 = new Matrix(3, 1, 80);
	
	@Test
	public void test1() throws Exception {
		
		assert(mat3 == mat1.multiply(mat2));
		
	}
	
	@Test
	public void test2() throws Exception {
		Matrix mat4 = new Matrix(3, 5, 12);
		Matrix mat5 = new Matrix(3, 5, -12);
		Matrix res = new Matrix(3, 5, 24);
		
		assert(res == mat4.subtract(mat5));
	}
	
	@Test
	public void test3() throws Exception {
		Matrix mat6 = new Matrix(4, 5, 10);
		Matrix mat7 = new Matrix(4, 5, 8);
		
		mat7.setElement(10, 30);
		
		System.out.println(mat7.toString());
		
		System.out.println(mat6.subtract(mat7).maxAbs());
		assert(mat6.subtract(mat7).maxAbs() > 2);
	}
	

}
