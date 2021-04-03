import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

public class PixelComponent {
	protected static PixelComponent[][] board;
	protected static int boardWidth;
	protected static int boardHeight;
	protected PixelType type;
	protected Color color;
	Direction curdDirection;
	public int x, y;
	protected static Random random = new Random();

	public PixelComponent(PixelType type, int x, int y) {

		super();
		this.type = type;
		this.x = x;
		this.y = y;
		setColor();
		curdDirection = Direction.DOWN;

	}

//	public void paintComponent(Graphics g) {
//		// super.paintComponent(g);
//		switch (this.type) {
//		case STONE:
//			g.setColor(Color.GRAY);
//			break;
//		case AIR:
//			g.setColor(Color.black);
//			break;
//		case WATER:
//			g.setColor(Color.blue);
//			break;
//		}
//		g.fillRect(x * pixelSize, y * pixelSize, x * pixelSize + pixelSize, y * pixelSize + pixelSize);
//	}

	LinkedList<PixelComponent> step(int depth) {

		LinkedList<PixelComponent> targets = new LinkedList<PixelComponent>();
		if (depth < 0)
			return targets;
		if (type == PixelType.WATER) {
			PixelComponent target = null;

			if (y < boardHeight - 1) {
				target = getNeighbour(Direction.DOWN);
				if (canMove(target.type)) {
					int maxdy = random.nextInt(5);
					for (int dy = 1; dy <= maxdy; dy++) {
						if (y + dy < board[0].length - 1 && canMove(board[x][y + dy].type)) {
							target = board[x][y + dy];
						} else
							break;
					}
//					curdDirection = Direction.DOWN;
					interact(target, targets);
					targets.addAll(target.step(depth - 1));
					return targets;
				}
				if (!canMove(target.type)) {
					target = null;
					if (canMove(getNeighbour(Direction.DOWNLEFT)) && canMove(getNeighbour(Direction.DOWNRIGHT))) {
						if (curdDirection == Direction.DOWN) {
							if (random.nextInt(2) == 0) {
								curdDirection = Direction.RIGHT;
								target = getNeighbour(Direction.DOWNRIGHT);
							} else {
								curdDirection = Direction.LEFT;
								target = getNeighbour(Direction.DOWNLEFT);
							}
						}

					} else if (canMove(getNeighbour(Direction.DOWNLEFT))
							|| canMove(getNeighbour(Direction.DOWNRIGHT))) {
						if (canMove(getNeighbour(Direction.DOWNRIGHT))) {
							curdDirection = Direction.RIGHT;
							target = getNeighbour(Direction.DOWNRIGHT);
						} else {
							curdDirection = Direction.LEFT;
							target = getNeighbour(Direction.DOWNLEFT);
						}

					}
					if (target != null) {
						interact(target, targets);
						targets.addAll(target.step(depth ));
						return targets;
					}

					if (canMove(getNeighbour(Direction.RIGHT)) && canMove(getNeighbour(Direction.LEFT))) {
						if (curdDirection == Direction.DOWN) {
							if (random.nextInt(2) == 0)
								curdDirection = Direction.RIGHT;
							else
								curdDirection = Direction.LEFT;
						}
					} else {
						if (canMove(getNeighbour(Direction.RIGHT))) {
							curdDirection = Direction.RIGHT;
						} else if (canMove(getNeighbour(Direction.LEFT))) {
							curdDirection = Direction.LEFT;
						}
					}

					if (canMove(getNeighbour(curdDirection))) {
						target = getNeighbour(curdDirection);
						interact(target, targets);
						targets.addAll(target.step(depth - 1));
						if (curdDirection == Direction.LEFT && getNeighbour(Direction.UPRIGHT) != null
								&& getNeighbour(Direction.UPRIGHT).type == PixelType.WATER) {
							targets.addAll(getNeighbour(Direction.UPRIGHT).step(depth - 1));
						} else if (curdDirection == Direction.RIGHT && getNeighbour(Direction.UPLEFT) != null
								&& getNeighbour(Direction.UPLEFT).type == PixelType.WATER) {
							targets.addAll(getNeighbour(Direction.UPLEFT).step(depth - 1));
						} else if (curdDirection == Direction.LEFT && getNeighbour(Direction.RIGHT) != null
								&& getNeighbour(Direction.RIGHT).type == PixelType.WATER) {
							targets.addAll(getNeighbour(Direction.RIGHT).step(depth - 1));
						} else if (curdDirection == Direction.RIGHT && getNeighbour(Direction.LEFT) != null
								&& getNeighbour(Direction.LEFT).type == PixelType.WATER) {
							targets.addAll(getNeighbour(Direction.LEFT).step(depth - 1));
						}
						return targets;
					}
				}
			} else {
				target = null;
				this.type = PixelType.AIR;
				return targets;
			}

		}
		if (type == PixelType.PLANT) {
			PixelComponent target = null;

			for (Direction dir : new Direction[] { Direction.DOWNLEFT, Direction.DOWN, Direction.DOWNRIGHT }) {
				target = getNeighbour(dir);
				if (target != null && canReact(target.type)) {
					interact(target, targets);
				}
			}
			return targets;
		}
		if (type == PixelType.WATERSPOUT) {
			PixelComponent target = null;

			if (y < board[0].length - 1) {
				int rand = random.nextInt(9);
				if (rand == 0)
					target = getNeighbour(Direction.DOWNLEFT);
				else if (rand == 1)
					target = getNeighbour(Direction.DOWNRIGHT);
				else if (rand == 2)
					target = getNeighbour(Direction.DOWN);

				if (target == null)
					return targets;
				else {
					interact(target, targets);
					targets.add(target);
					return targets;
				}
			} else {

				return targets;
			}

		}
		if (type == PixelType.FIRESPOUT) {
			PixelComponent target = null;

			if (y < board[0].length - 1) {
				int rand = random.nextInt(9);
				if (rand == 0)
					target = getNeighbour(Direction.UP);
				else if (rand == 1)
					target = getNeighbour(Direction.UPLEFT);
				else if (rand == 2)
					target = getNeighbour(Direction.UPRIGHT);
				else if (rand == 3)
					target = getNeighbour(Direction.DOWN);
				else if (rand == 4)
					target = getNeighbour(Direction.DOWNLEFT);
				else if (rand == 5)
					target = getNeighbour(Direction.DOWNRIGHT);
				else if (rand == 6)
					target = getNeighbour(Direction.LEFT);
				else if (rand == 7)
					target = getNeighbour(Direction.RIGHT);
				if (target == null)
					return targets;
				else {
					interact(target, targets);
					targets.add(target);
					return targets;
				}
			} else {
				return targets;
			}

		}

		if (type == PixelType.PLANTSPOUT) {
			PixelComponent target = null;

			if (y < board[0].length - 1) {
				int rand = random.nextInt(9);
				if (rand == 0)
					target = getNeighbour(Direction.UP);
				else if (rand == 1)
					target = getNeighbour(Direction.UPLEFT);
				else if (rand == 2)
					target = getNeighbour(Direction.UPRIGHT);
				else if (rand == 3)
					target = getNeighbour(Direction.DOWN);
				else if (rand == 4)
					target = getNeighbour(Direction.DOWNLEFT);
				else if (rand == 5)
					target = getNeighbour(Direction.DOWNRIGHT);
				else if (rand == 6)
					target = getNeighbour(Direction.LEFT);
				else if (rand == 7)
					target = getNeighbour(Direction.RIGHT);
				if (target == null)
					return targets;
				else {
					interact(target, targets);
					targets.add(target);
					return targets;
				}
			} else {
				return targets;
			}

		}
		if (type == PixelType.STEAM) {
			PixelComponent target = null;

			if (y == 0) {
				setType(PixelType.AIR);
				return targets;
			}

			int randdie = random.nextInt(200);
			if (randdie == 0) {
				setType(PixelType.WATER);
				return targets;
			}

			if (y > 0) {
				int rand = random.nextInt(3);
				if (rand == 0) {
					target = board[x][y - 1];
					if (canMove(target.type)) {
						interact(target, targets);
						return targets;
					}
				}
				if (rand == 1 && x < board.length - 1) {
					target = board[x + 1][y - 1];
					if (canMove(target.type)) {
						interact(target, targets);
						return targets;
					}
				}
				if (rand == 2 && x > 0) {
					target = board[x - 1][y - 1];
					if (canMove(target.type)) {
						interact(target, targets);
						return targets;
					}
				}
			//	if (!(canMove(getNeighbour(Direction.UP))|canMove(getNeighbour(Direction.UPLEFT))|canMove(getNeighbour(Direction.UPRIGHT)))) {
					if(!(canMove(getNeighbour(Direction.UP))|canMove(getNeighbour(Direction.UPLEFT)))){
						Direction dir=Direction.LEFT;
						if (random.nextInt(2)==0) {
							dir=Direction.RIGHT;
						}
						if (canMove(getNeighbour(dir))) {
							interact(target, targets);
							return targets;
						}
				//	}
					
				}

			} else {
				target = null;
				setType(PixelType.AIR);
				return targets;
			}
			}
		if (type == PixelType.FIRE) {
			PixelComponent target = null;

			if (y == 0) {
				setType(PixelType.AIR);
				return targets;
			}
			for (Direction dir : Direction.values()) {
				target = getNeighbour(dir);
				if (target != null && canReact(target.type)) {
					interact(target, targets);
				}
			}
			int randdie = random.nextInt(6);
			if (randdie == 0) {
				setType(PixelType.AIR);
				return targets;
			}

			if (y > 0) {
				int rand = random.nextInt(3);
				if (rand == 0) {
					target = board[x][y - 1];
					if (canMove(target.type)) {
						interact(target, targets);
					}
				}
				if (rand == 1 && x < board.length - 1) {
					target = board[x + 1][y - 1];
					if (canMove(target.type)) {
						interact(target, targets);
					}
				}
				if (rand == 2 && x > 0) {
					target = board[x - 1][y - 1];
					if (canMove(target.type)) {
						interact(target, targets);
					}
				}
			} else {
				target = null;
				setType(PixelType.AIR);
				return targets;
			}

		}

		return targets;
	}

	private boolean canMove(PixelComponent pix) {
		if (pix != null) {
			return canMove(pix.type);
		}
		return false;
	}

	private boolean canReact(PixelComponent pix) {
		if (pix != null) {
			return canReact(pix.type);
		}
		return false;
	}

	private boolean canMove(PixelType type2) {
		return getInteraction(type2) == Interaction.MOVE || getInteraction(type2) == Interaction.REACT;
	}

	private boolean canReact(PixelType type2) {
		return getInteraction(type2) == Interaction.REACT;
	}

	private Interaction getInteraction(PixelType type2) {
		switch (type) {
		case WATER: {
			switch (type2) {
			case AIR:
				return Interaction.MOVE;
			case STEAM:
				return Interaction.MOVE;
			case PLANT:
				return Interaction.REACT;
			case FIRE:
				return Interaction.REACT;
			case WATER:
			case STONE:
			case WATERSPOUT:
			case FIRESPOUT:
				return Interaction.NONE;
			default:
				return Interaction.NONE;
			}
		}
		case FIRE: {
			switch (type2) {
			case AIR:
				return Interaction.MOVE;
			case STEAM:
				return Interaction.MOVE;
			case PLANT:
				return Interaction.REACT;
			case STONE:
			case FIRE:
			case WATER:
			case WATERSPOUT:
				return Interaction.NONE;

			default:
				return Interaction.NONE;
			}
		}
		case WATERSPOUT: {
			switch (type2) {
			case AIR:
				return Interaction.REACT;
			case PLANT:
			case FIRE:
			case WATER:
			case STONE:
			case WATERSPOUT:
			default:
				return Interaction.NONE;
			}
		}
		case FIRESPOUT: {
			switch (type2) {
			case AIR:
				return Interaction.REACT;
			case PLANT:
			case FIRE:
			case WATER:
			case STONE:
			case WATERSPOUT:
			default:
				return Interaction.NONE;
			}
		}
		case PLANT: {
			switch (type2) {
			case WATER:
				return Interaction.REACT;
			default:
				return Interaction.NONE;
			}

		}
		case STONE:
		case AIR:
			switch (type2) {
			case STEAM:
				return Interaction.REACT;
			default:
				return Interaction.NONE;
			}
		case STEAM:
			switch (type2) {
			case AIR:
				return Interaction.MOVE;
			case WATER:
				return Interaction.MOVE;
			default:
				return Interaction.NONE;
			}
		default:
			System.out.println("Missing " + type + " " + type2);
			return Interaction.NONE;

		}

	}

	boolean setType(PixelType type) {
		{
			this.type = type;
			setColor();
			return true;
		}
	}

	private void interact(PixelComponent target, LinkedList<PixelComponent> targets) {

		switch (type) {
		case WATER: {
			switch (target.type) {
			case AIR:
				target.setType(PixelType.WATER);
				setType(PixelType.AIR);
				target.curdDirection = curdDirection;
				targets.add(target);
				return;
			case PLANT:
				if (random.nextInt(10) == 0) {
					setType(PixelType.PLANT);
					targets.add(target);
				}
				return;
			case FIRE:
				targets.add(target);
				target.setType(PixelType.AIR);
				if (random.nextInt(3) == 0) {
					setType(PixelType.STEAM);
				}

				return;
			default:
				return;
			}

		}
		case FIRE: {
			switch (target.type) {
			case AIR:
				target.setType(PixelType.FIRE);
				setType(PixelType.AIR);
				targets.add(target);
				return;
			case PLANT:

				if (random.nextInt(2)==0) {
				target.setType(PixelType.FIRE);
				setType(PixelType.FIRE);
				for (Direction dir : Direction.values()) {
					target = getNeighbour(dir);
					int rand = random.nextInt(3);
					if (rand == 0 && target != null && canMove(target.type)) {
						target.setType(PixelType.FIRE);
						targets.add(target);
					}
					}
				}
				return;
			case WATER:
				target.setType(PixelType.FIRE);
				if (random.nextInt()==0) {
					setType(PixelType.WATER);
				}
				else {
					target.setType(PixelType.STEAM);
				}
				targets.add(target);
				return;

			default:
				return;
			}

		}
		case WATERSPOUT: {
			switch (target.type) {
			case AIR:
				target.setType(PixelType.WATER);
				setType(PixelType.WATERSPOUT);
				targets.add(target);
				return;

			default:
				return;
			}

		}
		case PLANTSPOUT: {
			switch (target.type) {
			case AIR:
			case WATER:
				if (random.nextInt(50) == 0) {
					target.setType(PixelType.PLANT);
					targets.add(target);
				}
				return;
				
			default:
				return;
			}

		}
		case FIRESPOUT: {
			switch (target.type) {
			case AIR:
				target.setType(PixelType.FIRE);
				type = PixelType.FIRESPOUT;
				targets.add(target);
				return;
			case WATER:
				if (random.nextInt(10)==0) {
					target.setType(PixelType.FIRE);
				}
				targets.add(target);
				return;
			}

		}
		case PLANT: {
			switch (target.type) {
			case WATER:

				if (random.nextInt(50) == 0) {
					target.setType(PixelType.PLANT);
					setType(PixelType.PLANT);
					targets.add(target);
				}
				return;
			default:
				return;
			}

		}
		case STEAM: {
			switch (target.type) {
			case AIR:
				target.setType(PixelType.STEAM);
				setType(PixelType.AIR);
				targets.add(target);
				return;
			case WATER:
				target.setType(PixelType.STEAM);
				setType(PixelType.WATER);
				targets.add(target);
				return;
			default:
				return;
			}

		}
		case AIR:
			return;
		case STONE:
			return;
		default:
			return;
		}

	}

	public void setColor() {
		int rand;

		switch (this.type) {
		case STONE:
			color = Color.GRAY;
			break;
		case AIR:
			color = Color.BLACK;
			break;
		case WATER:
			rand = random.nextInt(3);
			if (rand == 3) {
				color = Color.decode("#357EC7");
				;
			}
			if (rand == 1) {
				color = Color.decode("#000080");
				;
			}
			if (rand == 2) {
				color = Color.decode("#342D7E");
				;
			}
			if (rand == 0) {
				color = Color.decode("#0041C2");
				;
			}
			break;
		case PLANT:
			color = Color.GREEN;
			break;
		case STEAM:
			color = Color.decode("#C2DFFF");
			break;
		case PLANTSPOUT:
			color = Color.decode("#013220");
			break;
		case WATERSPOUT:
			color = Color.DARK_GRAY;
			break;
		case FIRESPOUT:
			color = Color.decode("#8b0000");
			break;
		case FIRE:
			rand = random.nextInt(3);
			if (rand == 1) {
				color = Color.RED;
			}
			if (rand == 2) {
				color = Color.YELLOW;
			}
			if (rand == 0) {
				color = Color.ORANGE;
			}
			break;
		}

	}

	public void setPixel(BufferedImage image) {

		switch (this.type) {
		case STONE:
		case AIR:
		case WATER:
		case PLANT:
		case STEAM:
		case PLANTSPOUT:
		case WATERSPOUT:
		case FIRESPOUT:
			break;
		case FIRE:
			setColor();
		}
		image.setRGB(x, y, color.getRGB());

//		for (int dx = 0; dx < pixelSize; dx++) {
//			for (int dy = 0; dy < pixelSize; dy++) {
//				image.setColor(, dy, c);(x * pixelSize + dx, y * pixelSize + dy, color.getRGB());
//
//			}
//		}
		// image.fillRect(x*pixelSize, y*pixelSize, x*pixelSize+pixelSize,
		// y*pixelSize+pixelSize);

	}

	PixelComponent getNeighbour(Direction dir) {
		switch (dir) {
		case UP:
			if (y > 0)
				return board[x][y - 1];
			return null;
		case DOWN:
			if (y < boardHeight - 1)
				return board[x][y + 1];
			return null;
		case UPLEFT:
			if (x > 0 && y > 0)
				return board[x - 1][y - 1];
			return null;
		case UPRIGHT:
			if (x < boardWidth - 1 && y > 0)
				return board[x + 1][y - 1];
			return null;
		case RIGHT:
			if (x < boardWidth - 1)
				return board[x + 1][y];
			return null;
		case LEFT:
			if (x > 0)
				return board[x - 1][y];
			return null;
		case DOWNLEFT:
			if (x > 0 && y < boardHeight - 1)
				return board[x - 1][y + 1];
			return null;
		case DOWNRIGHT:
			if (x < boardWidth - 1 && y < boardHeight - 1)
				return board[x + 1][y + 1];
			return null;
		}
		return null;

	}

}