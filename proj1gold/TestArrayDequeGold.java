import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDequeGold {
    @Test
    public void testAddFirstAndRemoveFirst(){
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        Integer random = StdRandom.uniform(100);

        stu.addFirst(random);
        sol.addFirst(random);

        Integer expect = sol.removeFirst();
        Integer actual = stu.removeFirst();

        assertEquals("\n" + "addFirst("+random+")"+"\n"+"removeFirst()", expect, actual);
    }

    @Test
    public void testAddFirstAndRemoveLast() {
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        Integer random = StdRandom.uniform(100);

        stu.addFirst(random);
        sol.addFirst(random);

        Integer expect = sol.removeLast();
        Integer actual = stu.removeLast();

        assertEquals("\n" + "addFirst("+random+")" + "\n" + "removeLast()", expect, actual);
    }

    @Test
    public void testAddLastAndRemoveFirst() {
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        Integer random = StdRandom.uniform(100);

        stu.addLast(random);
        sol.addLast(random);

        Integer expect = sol.removeFirst();
        Integer actual = stu.removeFirst();

        assertEquals("\n" + "addLast("+random+")" + "\n" + "removeFirst()", expect, actual);
    }


    @Test
    public void testAddLastAndRemoveLastForNineTimes() {
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        String s = "";
        for(int n = 9; n > 0; n--){
            Integer random = StdRandom.uniform(100);
            stu.addLast(random);
            sol.addLast(random);
            s += "\n"+"addLast("+random+")";
        }

        for(int n = 9; n > 0; n--){
            Integer expect = sol.removeLast();
            Integer actual = stu.removeLast();
            s += "\n"+"removeLast()";
            assertEquals(s, expect, actual);
        }
    }

    @Test
    public void testAddLastAndRemoveFirstForNineTimes() {
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        String s = "";
        for(int n = 9; n > 0; n--){
            Integer random = StdRandom.uniform(100);
            stu.addLast(random);
            sol.addLast(random);
            s += "\n"+"addLast("+random+")";
        }

        for(int n = 9; n > 0; n--){
            Integer expect = sol.removeFirst();
            Integer actual = stu.removeFirst();
            s += "\n"+"removeFirst()";
            assertEquals(s, expect, actual);
        }
    }

    @Test
    public void testAddFirstAndRemoveFirstForNineTimes() {
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        String s = "";
        for(int n = 9; n > 0; n--){
            Integer random = StdRandom.uniform(100);
            stu.addFirst(random);
            sol.addFirst(random);
            s += "\n"+"addFirst("+random+")";
        }

        for(int n = 9; n > 0; n--){
            Integer expect = sol.removeFirst();
            Integer actual = stu.removeFirst();
            s += "\n"+"removeFirst()";
            assertEquals(s, expect, actual);
        }
    }

    @Test
    public void testAddFirstAndRemoveLastForNineTimes() {
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        String s = "";
        for(int n = 9; n > 0; n--){
            Integer random = StdRandom.uniform(100);
            stu.addFirst(random);
            sol.addFirst(random);
            s += "\n"+"addFirst("+random+")";
        }

        for(int n = 9; n > 0; n--){
            Integer expect = sol.removeLast();
            Integer actual = stu.removeLast();
            s += "\n"+"removeLast()";
            assertEquals(s, expect, actual);
        }
    }

    @Test
    public void testAddLastAndGet() {
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        String s = "";
        for(int n = 9; n > 0; n--){
            Integer random = StdRandom.uniform(100);
            stu.addLast(random);
            sol.addLast(random);
            s += "\n"+"addLast("+random+")";
        }

        for(int n = 0; n < 9; n++){
            Integer expect = sol.get(n);
            Integer actual = stu.get(n);
            s += "\n"+"get("+n+")";
            assertEquals(s, expect, actual);
        }
    }

    @Test
    public void testIsEmpty(){
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        boolean expect = sol.isEmpty();
        boolean actual = stu.isEmpty();
        assertEquals("\n" + "isEmpty()", expect, actual);
    }

    @Test
    public void testSize(){
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        Integer expect = sol.size();
        Integer actual = stu.size();
        assertEquals("\n" + "size()", expect, actual);

        String s = "";
        for(int n = 9; n > 0; n--){
            Integer random = StdRandom.uniform(100);
            stu.addFirst(random);
            sol.addFirst(random);
            s += "\n"+"addFirst("+random+")";
        }

        for(int n = 9; n > 0; n--){
            sol.removeLast();
            stu.removeLast();
            s += "\n"+"removeLast()";
            assertEquals(s, sol.size(), sol.size());
        }
    }
}
