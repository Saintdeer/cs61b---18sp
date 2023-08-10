public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int size;
    private int sentinel; // front position.
    // (sentinel + size - 1)%items.length is last position
    private static final int SIZE_OF_BOXES = 8;

    /**
     * Creates an empty list.
     */
    public ArrayDeque() {
        items = (T[]) new Object[SIZE_OF_BOXES];
        size = 0;
        sentinel = 0;
    }

    private void resize() {
        T[] newItems = (T[]) new Object[size * 2]; // FACTOR is 2.
        System.arraycopy(items, sentinel, newItems, sentinel, (items.length - sentinel));
        System.arraycopy(items, 0, newItems, items.length, sentinel);
        items = newItems;
    }

    @Override
    public void addFirst(T item) {
        if (this.isFull()) {
            this.resize();
        }
        int frontIndex = (sentinel - 1 + items.length) % items.length;
        items[frontIndex] = item;
        size += 1;
        sentinel = frontIndex;
    }

    @Override
    public void addLast(T item) {
        if (this.isFull()) {
            this.resize();
        }
        items[(this.computeLastPosition() + 1) % items.length] = item;
        size += 1;
    }

    private boolean isFull() {
        return (size == this.items.length);
    }

    private int computeLastPosition() {
        return (sentinel + size - 1) % items.length;
    }

    /**
     * halve the size of the array when ratio falls to less than 0.25.
     */
    private void downsizingArraySize() {
        double ratio = (double) size / items.length; // ratio of usage.
        if (ratio < 0.25 && (items.length > SIZE_OF_BOXES * 2)) {
            T[] newItems = (T[]) new Object[items.length / 2]; // halve the items.length

            int lastPos = this.computeLastPosition(); // last position
            boolean seeComment = lastPos < sentinel; // lastPos is on the left of sentinel or not.
            int lengthOfRightAfterSentinel = seeComment ? (items.length - sentinel) : size;

            System.arraycopy(items, sentinel, newItems, 0, lengthOfRightAfterSentinel);

            if (seeComment) {
                System.arraycopy(items, 0, newItems, lengthOfRightAfterSentinel, lastPos + 1);
            }

            items = newItems;
            sentinel = 0;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (this.isEmpty()) {
            System.out.println();
        } else {
            int index = sentinel, lastIndex = this.computeLastPosition();
            for (; true; index = (index + 1) % items.length) {
                System.out.print(items[index]);
                if (index < lastIndex) {
                    System.out.print(" ");
                } else {
                    System.out.println();
                    break; // last one to print out
                }
            }
        }
    }

    @Override
    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        T first = items[sentinel];
        items[sentinel] = null;
        size -= 1;
        sentinel = (sentinel + 1) % items.length;
        this.downsizingArraySize();
        return first;
    }

    @Override
    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        int lastPosition = this.computeLastPosition();
        T last = items[lastPosition];
        items[lastPosition] = null;
        size -= 1;
        this.downsizingArraySize();
        return last;
    }

    @Override
    public T get(int index) {
        index = sentinel + index;
        if (this.isEmpty() || (index < sentinel && index > this.computeLastPosition())) {
            return null;
        }
        return items[index % items.length];
    }
}
