public class ArrayDeque<T> {
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

    public ArrayDeque(ArrayDeque<T> other) {
        items = (T[]) new Object[other.items.length];
        size = other.size;
        sentinel = other.sentinel;
        System.arraycopy(other.items, 0, items, 0, other.items.length);
    }

    public void resize() {
        T[] newItems = (T[]) new Object[size * 2]; // FACTOR is 2.
        System.arraycopy(items, sentinel, newItems, sentinel, (items.length - sentinel));
        System.arraycopy(items, 0, newItems, items.length, sentinel);
        items = newItems;
    }

    public void addFirst(T item) {
        if (this.isFull()) {
            this.resize();
        }
        items[sentinel - 1] = item;
        size += 1;
        sentinel = (sentinel - 1 + items.length) % items.length;
    }

    public void addLast(T item) {
        if (this.isFull()) {
            this.resize();
        }
        items[sentinel + size] = item;
        size += 1;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    private boolean isFull() {
        return (size == this.items.length);
    }

    /**
     * halve the size of the array when ratio falls to less than 0.25.
     */
    private void downsizingArraySize() {
        double ratio = (double) size / items.length;
        if (ratio < 0.25 && (items.length > SIZE_OF_BOXES)) {
            T[] newItems = (T[]) new Object[items.length / 2]; // halve the items.length

            int sentinelToTailOfSize = Math.min((items.length - sentinel), size);
            int lastPosition = (sentinel + size - 1) % items.length;

            System.arraycopy(items, sentinel, newItems, 0, sentinelToTailOfSize);

            if (lastPosition < sentinelToTailOfSize) {
                System.arraycopy(items, 0, newItems, sentinelToTailOfSize, lastPosition + 1);
            }

            items = newItems;
            sentinel = 0;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if (this.isEmpty()) {
            System.out.println();
        } else {
            int index = sentinel, lastIndex = (sentinel + size - 1) % items.length;
            for (; true; index = (index + 1) % items.length) {
                System.out.print(items[index]);
                if (index < lastIndex) {
                    System.out.print(" ");
                } else {
                    System.out.println();
                    break;
                }
            }
        }
    }

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

    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        int lastPosition = (sentinel + size - 1) % items.length;
        T last = items[lastPosition];
        items[lastPosition] = null;
        size -= 1;
        this.downsizingArraySize();
        return last;
    }

    public T get(int index) {
        int lastPosition = (sentinel + size - 1) % items.length;
        if (this.isEmpty() || (lastPosition < index && index < sentinel)) {
            return null;
        }
        return items[(sentinel + index) % items.length];
    }
}
