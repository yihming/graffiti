package org.trajectory.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Matrix {
	private int xLength;
	private int yLength;
	private double[][] elements;
	
	public Matrix() {
		xLength = 0;
		yLength = 0;
	}
	
	public Matrix(List<List<Double>> mat) {
		xLength = mat.size();
		yLength = mat.get(0).size();
		
		elements = new double[xLength][];
		
		Iterator<List<Double>> row_iter = mat.iterator();
		int i = 0;
		while (row_iter.hasNext()) {
			List<Double> row_list = row_iter.next();
			elements[i] = new double[yLength];
			
			for (int j = 0; j < yLength; ++j) {
				elements[i][j] = row_list.get(j);
			}
			
			++i;
		}
	}
	
	public Matrix(int x, int y, double v) {
		xLength = x;
		yLength = y;
		
		elements = new double[xLength][yLength];
		for (int i = 0; i < xLength; ++i) {
			for (int j = 0; j < yLength; ++j) {
				elements[i][j] = v;
			}
		}
	}
	
	public int size() {
		return xLength * yLength;
	}
	
	public int getRowLength() {
		return xLength;
	}
	
	public void setRowLength(int xLength) {
		this.xLength = xLength;
	}
	
	public int getColumnLength() {
		return yLength;
	}
	
	public void setColumnLength(int yLength) {
		this.yLength = yLength;
	}
	
	public double getElement(int x, int y) throws Exception {
		if (x >= 0 && x < xLength && y >= 0 && y < yLength) {
			return elements[x][y];
		} else throw new NoSuchElementException();
	}
	
	public double getElement(int idx) throws Exception {
		if (idx >= 0 && idx < xLength * yLength) {
			++idx;
			int x, y;
			if (idx % yLength == 0) {
				x = idx / yLength - 1;
				y = yLength - 1;
			} else {
				x = idx / yLength;
				y = idx % yLength - 1;
			}
			
			return elements[x][y];
			
		} else throw new NoSuchElementException();
	}
	
	public void setElement(int idx, double v) throws Exception {
		
		if (idx >= 0 && idx < xLength * yLength) {
			++idx;
			int x, y;
			if (idx % yLength == 0) {
				x = idx / yLength - 1;
				y = yLength - 1;
			} else {
				x = idx / yLength;
				y = idx % yLength - 1;
			}
			
			elements[x][y] = v;
			
		} else throw new NoSuchElementException();
	}
	
	public void setElement(int x, int y, double v) throws Exception {
		if (x >= 0 && x < xLength && y >= 0 && y < yLength) {
			elements[x][y] = v;
		} else throw new NoSuchElementException();
	}
	
	public Matrix transpose() throws Exception {
		Matrix res_mat = new Matrix(this.yLength, this.xLength, 0);
		
		for (int i= 0; i < this.xLength; ++i) {
			for (int j = 0; j < this.yLength; ++j) {
				res_mat.setElement(j, i, this.elements[i][j]);
			}
		}
		
		return res_mat;
	}
	
	public Matrix multiply(Matrix mat) throws Exception {
		if (this.getColumnLength() == mat.getRowLength()) {
			Matrix res_mat = new Matrix(this.getRowLength(), mat.getColumnLength(), 0);
			
			for (int i = 0; i < res_mat.getRowLength(); ++i) {
				for (int j = 0; j < res_mat.getColumnLength(); ++j) {
					double tmp_sum = 0;
					for (int k = 0; k < this.getColumnLength(); ++k) {
						tmp_sum += (this.getElement(i, k) * mat.getElement(k, j));
					}
					res_mat.setElement(i, j, tmp_sum);
				}
			}
			
			return res_mat;
			
		} else throw new Exception();
	}
	
	public List<Double> toList() throws Exception {
		if (this.xLength == 1 || this.yLength == 1) {
			List<Double> res_list = new ArrayList<Double>();
			
			for (int idx = 0; idx < xLength * yLength; ++idx) {
				res_list.add(this.getElement(idx));
			}
			
			return res_list;
		} else throw new Exception();
	}
	
	public Matrix subtract(Matrix mat) throws Exception {
		if (xLength == mat.getRowLength() && yLength == mat.getColumnLength()) {
			Matrix res_mat = new Matrix(xLength, yLength, 0);
			
			for (int i = 0; i < xLength; ++i) {
				for (int j = 0; j < yLength; ++j) {
					res_mat.setElement(i, j, this.getElement(i, j) - mat.getElement(i, j));
				}
			}
			
			return res_mat;
		} else throw new Exception();
	}
	
	public double maxAbs() throws Exception {
		double max_val = Math.abs(elements[0][0]);
		
		for (int i = 0; i < xLength; ++i) {
			for (int j = 0; j < yLength; ++j) {
				if (Math.abs(elements[i][j]) > max_val)
					max_val = Math.abs(elements[i][j]);
			}
		}
		
		return max_val;
	}
	
	public String toString() {
		String res = "[\n";
		for (int i = 0; i < xLength; ++i) {
			res += "\t[ ";
			for (int j = 0; j < yLength; ++j) {
				res += elements[i][j];
				res += " ";
			}
			res += "],\n";
		}
		res += "]";
		
		return res;
	}
	
	public List<Double> getRow(int row_idx) throws Exception {
		if (row_idx >= 0 && row_idx < xLength) {
			List<Double> res_list = new ArrayList<Double>();
			
			for (int j = 0; j < yLength; ++j) {
				res_list.add(elements[row_idx][j]);
			}
			
			return res_list;
		} else throw new NoSuchElementException();
	}
	
	public List<Double> getColumn(int column_idx) throws Exception {
		if (column_idx >= 0 && column_idx < yLength) {
			List<Double> res_list = new ArrayList<Double>();
			
			for (int i = 0; i < xLength; ++i) {
				res_list.add(elements[i][column_idx]);
			}
			
			return res_list;
		} else throw new NoSuchElementException();
	}
	
	public double min() {
		double min_val = elements[0][0];
		
		for (int i = 0; i < xLength; ++i) {
			for (int j = 0; j < yLength; ++j) {
				if (elements[i][j] < min_val) {
					min_val = elements[i][j];
				}
			}
		}
		
		return min_val;
	}
	
	public double max() {
		double max_val = elements[0][0];
		
		for (int i = 0; i < xLength; ++i) {
			for (int j = 0; j < yLength; ++j) {
				if (elements[i][j] > max_val) {
					max_val = elements[i][j];
				}
			}
		}
		
		return max_val;
	}
	
	public Matrix divide(double c) throws Exception {
		if (c != 0) {
			Matrix res_mat = this;
			for (int i = 0; i < res_mat.getRowLength(); ++i) {
				for (int j = 0; j < res_mat.getColumnLength(); ++j) {
					res_mat.setElement(i, j, this.elements[i][j] / c);
				}
			}
			
			return res_mat;
		} else throw new Exception();
	}
}
