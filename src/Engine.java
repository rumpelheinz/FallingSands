import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;



public class Engine extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int DELAY = 1;
	private int width;
	private int height;

	private PixelComponent[][] board;
	Random random = new Random();
	BufferedImage image;
	private PixelType curType = PixelType.FIRE;
	private Timer timer;


	public Engine(int width, int height) {
		this.width = width;
		this.height = height;
		board = new PixelComponent[width][height];
		PixelComponent.boardWidth = width;
		PixelComponent.boardHeight = height;
		PixelComponent.board = board;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				board[x][y] = new PixelComponent(PixelType.AIR, x, y);
			}
		}
//		for (int x = 0; x < width; x++) {
//			board[x][height - 1].type = PixelType.STONE;
//		}
//		for (int x = 0; x < width / 2; x++) {
//			board[x][0].type = PixelType.WATERSPOUT;
//		}
//
//		for (int x = 0; x < width; x++) {
//			int randint = random.nextInt(15);
//			for (int y = 0; y < randint; y++) {
//				board[x][y].type = PixelType.WATER;
//			}
//		}
		for (int x = 100; x < 120; x++) {
			board[x][0].setType(PixelType.WATERSPOUT);
		}
		for (int x = 200; x < 202; x++) {
			board[x][height / 2].setType(PixelType.FIRESPOUT);
		}
		for (int x = 0; x < 3; x++) {
			board[x][height - 3].setType(PixelType.PLANTSPOUT);
		}
		for (int x = 0; x < width; x++) {
			board[x][height - 1].setType(PixelType.STONE);
			board[x][height - 2].setType(PixelType.STONE);
		}

//		for (int x = 0; x < width; x++) {
//			//int randint = random.nextInt(15);
//			for (int y = 0; y < height; y++) {
//				board[x][y].type = PixelType.WATERSPOUT;
//			}
//		}

//		board[width - 1][height - 2].type = PixelType.PLANT;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				PixelComponent pixel = board[x][y];
				pixel.setPixel(image);
			}
		}

		initBoard();
	}

	public Engine(BufferedInputStream in) throws IOException {
		char a = ' ';
		String str = "";
		while (a != '\n') {
			a = (char) in.read();
			if (a=='\n')continue;
			str += a;
		}
	//	in.read();

		System.out.println(str);
		width = Integer.parseInt(str);
		str = "";
		a = ' ';
		while (a != '\n') {
			a = (char) in.read();
			if (a=='\n')continue;
			str += a;
		}
		System.out.println(str);
	//	in.read();
		height = Integer.parseInt(str);
		board = new PixelComponent[width][height];
		PixelComponent.boardWidth = width;
		PixelComponent.boardHeight = height;
		PixelComponent.board = board;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				a = (char) in.read();
				PixelType t = null;
				switch (a) {
				case ' ':
					t = PixelType.AIR;
					break;
				case 'f':
					t = PixelType.FIRE;
					break;
				case 'F':
					t = PixelType.FIRESPOUT;
					break;
				case 'g':
					t = PixelType.GAS;
					break;
				case 'p':
					t = PixelType.PLANT;
					break;
				case 'P':
					t = PixelType.PLANTSPOUT;
					break;
				case 's':
					t = PixelType.STEAM;
					break;
				case 'r':
					t = PixelType.STONE;
					break;
				case 'w':
					t = PixelType.WATER;
					break;
				case 'W':
					t = PixelType.WATERSPOUT;
					break;
				default:
					break;

				}
				board[x][y] = new PixelComponent(t, x, y);

			}
			in.read();
		}
		in.close();

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				PixelComponent pixel = board[x][y];
				pixel.setPixel(image);
			}
		}

		initBoard();
	}

	private void initBoard() {

		addKeyListener(new TAdapter());
		MouseAdapter m = new MAdapter();
		addMouseListener(m);
		addMouseMotionListener(m);
		setBackground(Color.black);
		setFocusable(true);

		timer = new Timer(DELAY, this);
		timer.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		doDrawing(g);

		Toolkit.getDefaultToolkit().sync();
	}

	private void doDrawing(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		g2d.finalize();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		step();

	}

	public synchronized void step() {
		for (int i = 0; i < 100000; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			PixelComponent pixel = board[x][y];
			// pixel.type = PixelType.WATER;
			LinkedList<PixelComponent> targets = pixel.step(5);
			pixel.setPixel(image);
//			repaint(x * pixelSize, y * pixelSize, x * pixelSize + pixelSize, y * pixelSize + pixelSize);
			for (PixelComponent target : targets) {
				if (target != null) {
//				repaint(target.x * pixelSize, target.y * pixelSize, target.x * pixelSize + pixelSize,
//						target.y * pixelSize + pixelSize);
					target.setPixel(image);
				}
			}
		}

//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
////            int x = random.nextInt(width);
////            int y = random.nextInt(height);
//				PixelComponent pixel = board[x][y];
//				// pixel.type = PixelType.WATER;
//				LinkedList<PixelComponent> targets = pixel.step(1);
//				pixel.setPixel(image);
////			repaint(x * pixelSize, y * pixelSize, x * pixelSize + pixelSize, y * pixelSize + pixelSize);
//				for (PixelComponent target : targets) {
//					if (target != null) {
////				repaint(target.x * pixelSize, target.y * pixelSize, target.x * pixelSize + pixelSize,
////						target.y * pixelSize + pixelSize);
//						target.setPixel(image);
//					}
//				}
//			}
//		}

		// repaint(x * pixelSize, y * pixelSize, x * pixelSize + pixelSize, y *
		// pixelSize + pixelSize);

		repaint();

	}

	protected void setType(PixelType newtype) {
		curType = newtype;
	}

	private class TAdapter extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			if (key == KeyEvent.VK_LEFT) {
				board[200][height - 2].type = PixelType.FIRE;
			}
		}
	}

	synchronized void  save(BufferedOutputStream bufferedOutputStream) throws IOException {

		bufferedOutputStream.write((width + "\n").getBytes());
		bufferedOutputStream.write((height + "\n").getBytes());
		String out = "";
		System.out.println("saving");
		
		byte[] bytes=new byte[width*(height+1)];
		int i=0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				switch (board[x][y].type) {
				case AIR:
					bytes[i]= (' ');
					break;
				case FIRE:
					bytes[i]= ('f');
					break;
				case FIRESPOUT:
					bytes[i]= ('F');
					break;
				case GAS:
					bytes[i]= ('g');
					break;
				case PLANT:
					bytes[i]= ('p');
					break;
				case PLANTSPOUT:
					bytes[i]= ('P');
					break;
				case STEAM:
					bytes[i]= ('s');
					break;
				case STONE:
					bytes[i]= ('r');
					break;
				case WATER:
					bytes[i]= ('w');
					break;
				case WATERSPOUT:
					bytes[i]= ('W');
					break;
				default:
					break;

				}
				i++;
			}
			bytes[i]= '\n';
			i++;
		}
		System.out.println("done");
		bufferedOutputStream.write(bytes);
		bufferedOutputStream.close();
		System.out.println("closed");

	}

	private class MAdapter extends MouseAdapter {
		private boolean pressed = false;
		private int oldMouseX, oldMouseY = 0;

		@Override
		public void mousePressed(MouseEvent e) {
			System.out.println(e.getX() + " " + e.getY());
			int x = (int) ((e.getX()) / (getWidth() * 1.0 / width));
			int y = (int) ((e.getY()) / (getHeight() * 1.0 / height));
			if (x >= 0 && x < width && y >= 0 && y < height)
				board[x][y].setType(curType);
			if (x + 1 >= 0 && x + 1 < width && y >= 0 && y < height)
				board[x + 1][y].setType(curType);
			if (x + 1 >= 0 && x + 1 < width && y + 1 >= 0 && y + 1 < height)
				board[x + 1][y + 1].setType(curType);
			if (x >= 0 && x < width && y + 1 >= 0 && y + 1 < height)
				board[x][y + 1].setType(curType);

			pressed = true;
			oldMouseX = (int) (e.getX() / (getWidth() * 1.0 / width));
			oldMouseY = (int) (e.getY() / (getHeight() * 1.0 / height));

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			pressed = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!pressed)
				return;
			int mouseX = (int) (e.getX() / (getWidth() * 1.0 / width));
			int mouseY = (int) (e.getY() / (getHeight() * 1.0 / height));
			int xmax = Math.max(mouseX, oldMouseX);
			int xmin = Math.min(mouseX, oldMouseX);
			int ymax = Math.max(oldMouseY, mouseY);
			int ymin = Math.min(oldMouseY, mouseY);
			int dx = xmax - xmin;
			if (dx == 0)
				dx = 1;
			float dy = (ymax - ymin) * 1.0f / dx;
			System.out.println("dx " + dx + " dy " + dy);

			if (((mouseX - oldMouseX) >= 0 && (mouseY - oldMouseY) >= 0)
					|| ((mouseX - oldMouseX) < 0 && (mouseY - oldMouseY) < 0)) {
				int i = -1;
				for (int x = xmin; x <= xmax; x++) {
					System.out.println("x " + x);
					i++;
					if (x < 0)
						continue;
					if (x > width - 1)
						break;
					for (int y = (int) Math.floor(ymin + dy * i); y <= Math.floor(ymin + dy * (i + 1)); y++) {
						System.out.println("x " + x + " - y " + y);
						if (y < 0)
							continue;
						if (y > height - 1)
							break;
						board[x][y].setType(curType);
					}
				}
			} else {
				System.out.println("Wrongdir");
				int i = 0;
				dy = -dy;
				for (int x = xmin; x <= xmax; x++) {
					System.out.println("x " + x);
					i++;
					if (x < 0)
						continue;
					if (x > width - 1)
						break;
					for (int y = (int) Math.floor(ymax + dy * i); y <= Math.floor(ymax + dy * (i - 1)); y++) {
						System.out.println("x " + x + " - y " + y);
						if (y < 0)
							continue;
						if (y > height - 1)
							break;
						board[x][y].setType(curType);
					}
				}

			}
			oldMouseX = mouseX;
			oldMouseY = mouseY;
			System.out.println("cur" + mouseX + " " + mouseY);
			System.out.println("dx " + dx + " dy " + (ymax - ymin) + " " + dy);


		}

	}

//	}
}
