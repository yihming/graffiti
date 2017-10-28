package org.bnuminer.server;
/**
 * r39: 去除易引起混淆之l与1验证字符（2010-04-28 11:18）
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int width = 70;
		int height = 36;
		
		BufferedImage buffimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffimg.createGraphics();
		
		// 创建一个随机数生成器
		Random random = new Random();
		
		// 设定图像的背景色
		g.setColor(getRandColor(180, 250));
		g.fillRect(0, 0, width, height);
		
		// 创建字体，字体之大小应根据图片之高度设定
		Font font = new Font("Times New Roman", Font.PLAIN, 28);
		// 设置字体
		g.setFont(font);
		
		// 画边框
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width - 1, height - 1);
		
		// 随机产生160条干扰线，使图像中的认证码不易被其他程序探测到
		g.setColor(Color.GRAY);
		
		for (int i = 0; i < 50; ++i) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int x1 = random.nextInt(12);
			int y1 = random.nextInt(12);
			g.drawLine(x, y, x + x1, y + y1);
		}
		
		// 保存验证码
		StringBuffer randomCode = new StringBuffer();
		
		// 设置默认生成4个验证码
		int length = 4;
		// 设置备选验证码
		String base = "abcdefghijkmnopqrstuvwxyz023456789";
		
		int size = base.length();
		
		// 随机产生4位验证码
		for (int i = 0; i < length; ++i) {
			int start = random.nextInt(size);
			String strRand = base.substring(start, start + 1);
			
			// 生成随机颜色
			g.setColor(getRandColor(1, 100));
			g.drawString(strRand, 13 * i + 6, 28);
			
			// 将产生的4个随机数组合在一起
			randomCode.append(strRand);
		}
		
		// 将4位验证码保存至Session中
		request.getSession().setAttribute("rand", randomCode.toString());
		
		
		// 禁止图像缓存
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		
		// 将图像输出到Servlet输出流
		ServletOutputStream sos = response.getOutputStream();
		ImageIO.write(buffimg, "jpeg", sos);
		sos.close();
	}
	
	Color getRandColor(int fc, int bc) { // 给定范围获得颜色
		Random random = new Random();
		
		if (fc > 255) fc = 255;
		if (bc > 255) bc = 255;
		
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		
		return new Color(r, g, b);
	}
}
