import java.util.Scanner;
//main class only for testing program
public class Main {
    //main mrthod
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Menu");
        System.out.println("1 Binary Search Tree");
        System.out.println("(any) Balanced Binary Search Tree");
        System.out.print("Choice : ");
        if(scan.nextInt()==1){
          PersistentDynamic tree = new PersistentDynamic();
          System.out.println("Enter Comma Seprated numbers: ");
          scan.nextLine();
          String[] data = scan.nextLine().split(",");
          for(String input: data){
            tree.add(input.trim());
          }
          tree.display();
        }
        else{
          BalancedPersistentDynamic tree = new BalancedPersistentDynamic();
          System.out.println("Enter Comma Seprated numbers: ");
          scan.nextLine();
          String[] data = scan.nextLine().split(",");
          for(String input: data){
            tree.add(input.trim());
          }
          tree.display();
        }
    }

}
