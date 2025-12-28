package estaticos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import variables.Mapa;
import variables.Mapa.Celda;
import variables.Pelea;
import variables.Pelea.Trampa;

class AstarPathfinding {
	private Map<Integer, Node> openList;
	private Map<Integer, Node> closeList;
	private Mapa map;
	private Pelea fight;
	private int cellStart;
	private int cellEnd;

	AstarPathfinding(Mapa map, Pelea fight, int cellStart, int cellEnd) {
		openList = new TreeMap<>();
		closeList = new LinkedHashMap<>();
		setMap(map);
		setFight(fight);
		setCellStart(cellStart);
		setCellEnd(cellEnd);
	}

	ArrayList<Celda> getShortestPath(int value) {
		Node nodeStart = new Node(getCellStart(), null);
		openList.put(this.getCellStart(), nodeStart);
		while (!openList.isEmpty() && !closeList.containsKey(getCellEnd())) {
			char[] dirs = { 'b', 'd', 'f', 'h' };
			Node nodeCurrent = bestNode();
			if (nodeCurrent.getCellId() == getCellEnd()
					&& !Camino.cellArroundCaseIDisOccuped(getFight(), nodeCurrent.getCellId())) {
				return getPath();
			}
			addListClose(nodeCurrent);
			for (int loc0 = 0; loc0 < 4; ++loc0) {
				int cell = Camino.getSigIDCeldaMismaDir(nodeCurrent.getCellId(), dirs[loc0], getMap(), false);
				Node node = new Node(cell, nodeCurrent);
				boolean trampa = false;
				if (fight.getTipoPelea() == 4 && fight.LucActual != null) {
					for (Trampa tr : fight.getTrampas()) {
						if (tr.getCelda().getID() == cell
								&& tr.getLanzador().getEquipoBin() == fight.LucActual.getEquipoBin()) {
							trampa = true;
							break;
						}
					}
				}
				if (getMap() == null) {
					break;
				}
				if (getMap().getCelda(cell) != null && !trampa) {
					if (getMap().getCelda(cell).esCaminable(true) || cell == getCellEnd()) {
						if (!Camino.haveFighterOnThisCell(cell, getFight()) || cell == getCellEnd()) {
							if (!closeList.containsKey(cell)) {
								if (openList.containsKey(cell)) {
									if (openList.get(cell).getCountG() > getCostG(node)) {
										nodeCurrent.setChild(openList.get(cell));
										openList.get(cell).setParent(nodeCurrent);
										openList.get(cell).setCountG(getCostG(node));
										openList.get(cell).setHeristic(
												Camino.distanciaEntreDosCeldas(getMap(), cell, getCellEnd()) * 10);
										openList.get(cell).setCountF(
												openList.get(cell).getCountG() + openList.get(cell).getHeristic());
									}
								} else {
									if (value == 0 && Camino.siCeldasEstanEnMismaLinea(this.getMap(), cell,
											getCellEnd(), dirs[loc0])) {
										node.setCountF(node.getCountG() + node.getHeristic() - 10);
									}
									openList.put(cell, node);
									nodeCurrent.setChild(node);
									node.setParent(nodeCurrent);
									node.setCountG(getCostG(node));
									node.setHeristic(Camino.distanciaEntreDosCeldas(getMap(), cell, getCellEnd()) * 10);
									node.setCountF(node.getCountG() + node.getHeristic());
								}
							}
						}
					}
				}
			}
		}
		return getPath();
	}

	private ArrayList<Celda> getPath() {
		Node current = getLastNode(closeList);
		if (current == null) {
			return null;
		}
		final ArrayList<Celda> path = new ArrayList<>();
		final Map<Integer, Celda> path2 = new TreeMap<>();
		int index = closeList.size();
		while (current.getCellId() != getCellStart()) {
			if (current.getCellId() != getCellStart()) {
				path2.put(index, this.getMap().getCelda(current.getCellId()));
				current = current.getParent();
			}
			--index;
		}
		index = -1;
		while (path.size() != path2.size()) {
			++index;
			if (path2.get(index) == null) {
				continue;
			}
			path.add(path2.get(index));
		}
		return path;
	}

	private Node getLastNode(final Map<Integer, Node> list) {
		Node node = null;
		for (Entry<Integer, Node> entry : list.entrySet()) {
			node = entry.getValue();
		}
		return node;
	}

	private Node bestNode() {
		int bestCountF = 150000;
		Node bestNode = null;
		for (Node node : openList.values()) {
			if (node.getCountF() < bestCountF) {
				bestCountF = node.getCountF();
				bestNode = node;
			}
		}
		return bestNode;
	}

	private void addListClose(Node node) {
		if (openList.containsKey(node.getCellId())) {
			openList.remove(node.getCellId());
		}
		if (!closeList.containsKey(node.getCellId())) {
			closeList.put(node.getCellId(), node);
		}
	}

	private int getCostG(Node node) {
		int costG;
		for (costG = 0; node.getCellId() == getCellStart(); node = node.getParent(), costG += 10) {
		}
		return costG;
	}

	public Mapa getMap() {
		return map;
	}

	public void setMap(Mapa _map) {
		map = _map;
	}

	public Pelea getFight() {
		return fight;
	}

	public void setFight(Pelea _fight) {
		fight = _fight;
	}

	public int getCellStart() {
		return cellStart;
	}

	public void setCellStart(int _cellStart) {
		cellStart = _cellStart;
	}

	public int getCellEnd() {
		return this.cellEnd;
	}

	public void setCellEnd(int _cellEnd) {
		cellEnd = _cellEnd;
	}
}
