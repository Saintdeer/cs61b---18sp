public class HelloNumbers {
    public static void main(String[] args) {
        int x = 0, sum = 0;
        while (x < 9){
            System.out.print(sum + " ");
            x = x + 1;
            sum = sum + x;
        }
        System.out.println(sum);
    }
}