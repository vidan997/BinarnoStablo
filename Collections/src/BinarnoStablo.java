import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public class BinarnoStablo<T> implements Map<Integer, T> {

	private class Node {
		private Node left;
		private Node right;
		private T value;
		private int key;

		public Node(Node left, Node right, T value, int key) {
			this.left = left;
			this.right = right;
			this.value = value;
			this.key = key;
		}

	}

	private class MySet<Integer, T> extends AbstractSet<T> {
		ArrayList<T> values;
		ArrayList<Integer> keys;

		public MySet() {
		}

		public MySet(ArrayList<T> values, ArrayList<Integer> keys) {
			this.values = values;
			this.keys = keys;
		}

		@Override
		public Iterator<T> iterator() {
			return values.iterator();
		}

		@Override
		public int size() {
			return values.size();
		}

	}

	public enum Side {
		left, right
	}

	private Node root;
	private int numberOfElements = 0;
	private boolean A;

	public BinarnoStablo(boolean A) {
		this.A = A;
	}

	public BinarnoStablo(BinarnoStablo<T>.Node root, boolean A) {
		super();
		this.root = root;
		this.A = A;
		numberOfElements++;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	@Override
	public void clear() {
		root = null;
		numberOfElements = 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return searchKey(root, (int) key);
	}

	private boolean searchKey(Node node, int i) {
		if (node == null) {
			return false;
		}
		if (node.key == i) {
			return true;
		}
		if (node.key < i) {
			return searchKey(node.right, i);
		} else {
			return searchKey(node.left, i);
		}
	}

	@Override
	public boolean containsValue(Object value) throws ClassCastException {

		return SearchValue(root, (T) value);
	}

	private boolean SearchValue(Node node, T value) {
		if (node == null) {
			return false;
		}
		if (node.value.equals(value)) {
			return true;
		}
		return (SearchValue(node.left, value) || SearchValue(node.right, value));
	}

	@Override
	public Set<java.util.Map.Entry<Integer, T>> entrySet() {
		ArrayList<Integer> keyList = new ArrayList<>();
		ArrayList<T> valueList = new ArrayList<>();

		addToLists(root, keyList, valueList);

		MySet<Integer, T> ms = new MySet<>(valueList, keyList);

		return (Set<java.util.Map.Entry<java.lang.Integer, T>>) ms;

	}

	private void addToLists(BinarnoStablo<T>.Node node, ArrayList<java.lang.Integer> keyList, ArrayList<T> valueList) {
		if (node == null) {
			return;
		}
		addToLists(node.left, keyList, valueList);
		keyList.add(node.key);
		valueList.add(node.value);
		addToLists(node.right, keyList, valueList);
	}

	@Override
	public T get(Object key) throws ClassCastException {

		return find(root, (int) key);
	}

	private T find(Node node, int key) {
		if (node == null) {
			return null;
		}
		if (node.key == key) {
			return node.value;
		}
		if (node.key > key) {
			return find(node.right, key);
		} else {
			return find(node.left, key);
		}
	}

	@Override
	public boolean isEmpty() {
		return (root == null);
	}

	@Override
	public Set<Integer> keySet() {
		Set<Integer> set = new HashSet<>();
		putInSet(root, set);
		return set;
	}

	private void putInSet(BinarnoStablo<T>.Node node, Set<java.lang.Integer> set) {
		if (node == null) {
			return;
		}
		putInSet(node.left, set);
		set.add(node.key);
		putInSet(node.right, set);
	}

	@Override
	public T put(Integer key, T value) {
		if (root == null) {
			root = new Node(null, null, value, key);
			numberOfElements++;
			return null;
		}
		if (containsKey(key)) {
			Node n = findNode(root, key);
			T v = n.value;
			n.value = value;
			return v;
		}
		Node n = whereToPut(root, key);
		if (key > n.key) {
			n.right = new Node(null, null, value, key);
		} else {
			n.left = new Node(null, null, value, key);

		}
		if (A == true) {
			balans();
		}
		numberOfElements++;
		return null;
	}

	private Node whereToPut(Node node, Integer key) {
		if (key > node.key) {
			if (node.right == null) {
				return node;
			} else {
				return whereToPut(node.right, key);
			}
		} else {
			if (node.left == null) {
				return node;
			} else {
				return whereToPut(node.left, key);
			}
		}
	}

	private Node findNode(Node node, int key) {
		if (node == null) {
			return null;
		}
		if (node.key == key) {
			return node;
		}
		if (node.key < key) {
			return findNode(node.right, key);
		} else {
			return findNode(node.left, key);
		}
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends T> m) {
		Iterator it = m.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			put((Integer) pair.getKey(), (T) pair.getValue());
		}
	}

	@Override
	public T remove(Object key) {
		try {
			if (numberOfElements == 1) {
				T returnValue = root.value;
				root = null;
				return returnValue;

			}

			Node forRemove = findNode(root, (int) key);
			T returnValue = forRemove.value;

			if (forRemove.left == null && forRemove.right == null) {
				deleteNode(root, forRemove);
			} else {
				Node forSwap = findMinForSwap(forRemove);
				int keyForSwap = forSwap.key;
				T valueForSwap = forSwap.value;

				Node pom = forSwap.right;

				deleteNode(root, forSwap);

				if (forSwap.right != null) {
					put(pom.key, forSwap.right.value);
					numberOfElements--;
				}

				forRemove.key = keyForSwap;
				forRemove.value = valueForSwap;

			}
			numberOfElements--;

			return returnValue;
		} finally {
			if (A == true) {
				balans();
			}
		}
	}

	private void deleteNode(BinarnoStablo<T>.Node root, BinarnoStablo<T>.Node forRemove) {
		Node parent = null;

		parent = findParent(root, forRemove.key);

		if (parent.left == forRemove) {
			parent.left = null;
		} else {
			parent.right = null;
		}
	}

	private Node findMinForSwap(Node node) {
		if (node == null) {
			return null;
		}
		if (node.right == null) {
			return node.left;
		}

		Node pom = node.right;

		while (true) {
			if (pom.left == null) {
				return pom;
			}
			pom = pom.left;

		}
	}

	private Node findParent(Node node, int key) {
		if (node.key == key) {
			return null;
		}
		if ((node.left != null && node.left.key == key) || (node.right != null && node.right.key == key)) {
			return node;
		}
		if (node.key < key) {
			return findParent(node.right, key);
		} else {
			return findParent(node.left, key);
		}

	}

	@Override
	public int size() {
		return numberOfElements;
	}

	@Override
	public Collection<T> values() {
		Collection<T> col = new ArrayList<>();

		addToCollection(root, col);

		return col;
	}

	private void addToCollection(BinarnoStablo<T>.Node node, Collection<T> col) {
		if (node == null) {
			return;
		}
		addToCollection(node.left, col);
		col.add(node.value);
		addToCollection(node.right, col);

	}

	public void balans() {
		balansTree(root);
	}
	

	private void allElemntsToStr(BinarnoStablo<T>.Node node) {
		if (node == null) {
			return;
		}
		System.out.println(node.value.toString());
		allElemntsToStr(node.left);
		allElemntsToStr(node.right);
	}

	private void allKeysToStr(BinarnoStablo<T>.Node node) {
		if (node == null) {
			return;
		}
		System.out.println(node.key);
		allKeysToStr(node.left);
		allKeysToStr(node.right);
	}
	
	private void balansTree(BinarnoStablo<T>.Node node) {

		if (node == null) {
			return;
		}
		balansTree(node.left);
		balansTree(node.right);

		int left = countChildren(node, Side.left);
		int right = countChildren(node, Side.right);

		if (left - right > 1) {
			node = turn(node, Side.right);
			if (node == root) {
				balans();
			}
		}
		if (right - left > 1) {
			node = turn(node, Side.left);
			if (node == root) {
				balans();
			}
		}

		left = countChildren(node, Side.left);
		right = countChildren(node, Side.right);

		if (left - right > 1) {
			node = turn(node, Side.right);

		}
		if (right - left > 1) {
			node = turn(node, Side.left);
		}

	}

	private Node turn(BinarnoStablo<T>.Node node, Side side) {
		if (side.equals(Side.left)) {
			return turnLeft(node);
		} else {
			return turnRight(node);
		}
	}

	private Node turnRight(BinarnoStablo<T>.Node node) {
		Node returnNode = null;
		Node pom = node;

		Node parent = findParent(root, node.key);

		if (parent == null) {
			root = node.left;
			node.left = null;
			returnNode = root;
		} else {
			if (parent.left == node) {
				parent.left = node.left;
				returnNode = parent.left;
			} else {
				parent.right = node.left;
				returnNode = parent.right;
			}
			pom.left = null;
			pom.right = null;
		}

		Node place = whereToPut(root, pom.key);
		if (pom.key > place.key) {
			place.right = pom;
		} else {
			place.left = pom;
		}

		return returnNode;
	}

	private Node turnLeft(BinarnoStablo<T>.Node node) {
		Node returnNode = null;
		Node pom = node;
		Node parent = findParent(root, node.key);

		if (parent == null) {
			root = node.right;
			node.right = null;
			returnNode = root;

		} else {

			if (parent.left == node) {
				parent.left = node.right;
				returnNode = parent.left;
			} else {
				parent.right = node.right;
				returnNode = parent.right;
			}

			pom.left = null;
			pom.right = null;
		}
		Node place = whereToPut(root, pom.key);
		if (pom.key > place.key) {
			place.right = pom;
		} else {
			place.left = pom;
		}

		return returnNode;
	}

	private int countChildren(BinarnoStablo<T>.Node node, Side side) {
		if (node == null) {
			return 0;
		}
		if (side.equals(Side.left)) {
			return countC(node.left);
		}
		return countC(node.right);
	}

	private int countC(BinarnoStablo<T>.Node node) {
		if (node == null) {
			return 0;
		}
		return Math.max(1 + countC(node.left), 1 + countC(node.right));
	}

	public void writeElements() {
		allElemntsToStr(root);
	}

	public void writeKeys() {
		allKeysToStr(root);
	}
}
