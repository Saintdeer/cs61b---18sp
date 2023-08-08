public class LinkedListDeque<T> {
    private final Node sentinel;
    private int size;
    public class Node{
        public Node prev;
        public T item;
        public Node next;
        public Node(Node prev, T item, Node next){
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
    public LinkedListDeque(){
        sentinel = new Node();
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    public void addFirst(T item){
        size += 1;
        Node OldFirst = sentinel.next;
        Node NewFirst =  new Node(sentinel, item, OldFirst);
        OldFirst.prev = NewFirst;
        sentinel.next = NewFirst;
    }
    public void addLast(T item){
        size += 1;
        Node OldLast = sentinel.prev;
        Node NewLast =  new Node(OldLast, item, sentinel);
        OldLast.next = NewLast;
        sentinel.prev = NewLast;
    }
    public boolean isEmpty(){
        return (size == 0);
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        if(size == 0) {
            System.out.println();
        }else{
            for(Node n = sentinel.next; ! n.equals(sentinel); n = n.next){
                System.out.print(n.item);
                if(! n.next.equals(sentinel)){
                    System.out.print(" ");
                }else{
                    System.out.println();
                }
            }
        }
    }
    public T removeFirst(){
        if(size == 0){
            return null;
        }else{
            size -= 1;
            Node OldFirst = sentinel.next;
            Node NewFirst = OldFirst.next;
            NewFirst.prev = sentinel;
            sentinel.next = NewFirst;
            return OldFirst.item;
        }
    }
    public T removeLast(){
        if(size == 0){
            return null;
        }else{
            size -= 1;
            Node OldLast = sentinel.prev;
            Node NewLast = OldLast.prev;
            NewLast.next = sentinel;
            sentinel.prev = NewLast;
            return OldLast.item;
        }
    }
    public T get(int index){
        if(size == 0 || size <= index){
            return null;
        }else{
            Node n = sentinel.next;
            while( index > 0){
                n = n.next;
                index--;
            }
            return n.item; // index==0
        }
    }
    public T getRecursive(int index){
        if(size == 0 || size <= index){
            return null;
        }else if(index == 0){
            return sentinel.next.item;
        }else{
            LinkedListDeque<T> d = new LinkedListDeque<>();
            d.sentinel.next = sentinel.next.next;
            return d.getRecursive(index-1);
        }
    }
}
