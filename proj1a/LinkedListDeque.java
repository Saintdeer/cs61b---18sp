public class LinkedListDeque<T> {
    private final Node sentinel; //sentinel node
    private int size;

    public class Node {
        public Node prev;
        public T item;
        public Node next;

        public Node(Node prev, T item, Node next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }

        public Node() {
            this.prev = null;
            this.item = null;
            this.next = null;
        }
    }

    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        size += 1;
        Node oldFirst = sentinel.next;
        Node newFirst = new Node(sentinel, item, oldFirst);
        oldFirst.prev = newFirst;
        sentinel.next = newFirst;
    }

    public void addLast(T item) {
        size += 1;
        Node oldLast = sentinel.prev;
        Node newLast = new Node(oldLast, item, sentinel);
        oldLast.next = newLast;
        sentinel.prev = newLast;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if (this.isEmpty()) {
            System.out.println();
        } else {
            for (Node n = sentinel.next; !n.equals(sentinel); n = n.next) {
                System.out.print(n.item);
                if (!n.next.equals(sentinel)) {
                    System.out.print(" ");
                } else {
                    System.out.println();
                }
            }
        }
    }

    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        } else {
            size -= 1;
            Node oldFirst = sentinel.next;
            Node newFirst = oldFirst.next;
            newFirst.prev = sentinel;
            sentinel.next = newFirst;
            return oldFirst.item;
        }
    }

    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        } else {
            size -= 1;
            Node oldLast = sentinel.prev;
            Node newLast = oldLast.prev;
            newLast.next = sentinel;
            sentinel.prev = newLast;
            return oldLast.item;
        }
    }

    public T get(int index) {
        if (this.isEmpty() || size <= index) {
            return null;
        } else {
            Node n = sentinel.next;
            while (index > 0) {
                n = n.next;
                index--;
            }
            return n.item; // index == 0
        }
    }

    public T getRecursive(int index) {
        if (this.isEmpty() || size <= index) {
            return null;
        } else if (index == 0) {
            return sentinel.next.item;
        } else {
            LinkedListDeque<T> d = new LinkedListDeque<>();
            d.sentinel.next = sentinel.next.next;
            return d.getRecursive(index - 1);
        }
    }
}
