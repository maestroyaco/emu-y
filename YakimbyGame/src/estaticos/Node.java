package estaticos;

class Node {
	private int countG;
	private int countF;
	private int heristic;
	private int cellId;
	private Node parent;
	private Node child;

	Node(int cellId, Node parent) {
		countG = 0;
		countF = 0;
		heristic = 0;
		setCellId(cellId);
		setParent(parent);
	}

	public int getCountG() {
		return countG;
	}

	public void setCountG(int _countG) {
		countG = _countG;
	}

	public int getCountF() {
		return countF;
	}

	public void setCountF(final int countF) {
		this.countF = countF;
	}

	public int getHeristic() {
		return heristic;
	}

	public void setHeristic(int _heristic) {
		heristic = _heristic;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int _cellId) {
		cellId = _cellId;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node _parent) {
		parent = _parent;
	}

	public Node getChild() {
		return child;
	}

	public void setChild(Node _child) {
		child = _child;
	}
}