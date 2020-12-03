import java.util.*;

public class DFS {

    public static boolean hasFound = false, cantBeFound = false;

    public static Stack<Node<Integer>> stack;

    public enum pours{XY, XZ, YX, YZ, ZX, ZY}

    public static int[] cap = {9, 6, 3, 0};

    public static int x, y, z, loops;


    public static void main (String[] args) {

        stack = new Stack<>();
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter in three starting pints, pressing enter after each entry. Format: 0 <= first <= 10, 0 <= second <= 7, 0 <= third <= 4");
        System.out.println("\nFirst: ");
        int ten = scan.nextInt();
        if (ten > 10 || ten < 0)
            throw new IllegalArgumentException("Not allowed. Format: 0 <= first <= 10");
        System.out.println("Second: ");
        int seven = scan.nextInt();
        if (seven > 7 || seven < 0)
            throw new IllegalArgumentException("Not allowed. Format: 0 <= second <= 7");
        System.out.println("Third: ");
        int four = scan.nextInt();
        if (four > 4 || four < 0)
            throw new IllegalArgumentException("Not allowed. Format: 0 <= second <= 4");

        System.out.println("\nEnter in three wanted pints, pressing enter after each entry. Format: 0 <= first <= 10, 0 <= second <= 7, 0 <= third <= 4");
        System.out.println("\nFirst: ");
        int tenWanted = scan.nextInt();
        if (tenWanted > 10 || tenWanted < 0)
            throw new IllegalArgumentException("Not allowed. Format: 0 <= first <= 10");
        System.out.println("Second: ");
        int sevenWanted = scan.nextInt();
        if (sevenWanted > 7 || sevenWanted < 0)
            throw new IllegalArgumentException("Not allowed. Format: 0 <= second <= 7");
        System.out.println("Third: ");
        int fourWanted = scan.nextInt();
        if (fourWanted > 4 || fourWanted < 0)
            throw new IllegalArgumentException("Not allowed. Format: 0 <= second <= 4");

        System.out.println("\nPouring from (" + ten + ", " + seven + ", " + four + ")" + " to " + "(" + tenWanted + ", " + sevenWanted + ", " + fourWanted + "). ");

        Pouring(ten, seven, four, tenWanted, sevenWanted,fourWanted);

    }

    public static Node<Integer> findNode(int ten, int seven, int four) {

        int index = 0;

        Node<Integer> node = new Node<>();

        node.set(ten,seven,four);

        for (int i = 0; i < stack.size(); i++) {
            if (node.equates(stack.get(index)))
                return node;
            index++;
        }
        return null;
    }

    public static boolean pourFromTo(int pourFrom, int pourTo, int other, int tenWanted, int sevenWanted, int fourWanted, pours pour) {

        while (pourFrom <= 10 && pourFrom >= 0 && pourTo >= 0 && pourTo <= 10 && !hasFound && !cantBeFound) {

            boolean cont = false;

            Node<Integer> node = new Node<>();
            switch (pour) {
                case XY:
                    x = pourFrom;
                    y = pourTo;
                    z = other;
                    if ((x != cap[3]) && (y <= cap[1])) {
                        x--;
                        y++;
                    }
                    else {cont = true; break;}
                    break;
                case XZ:
                    x = pourFrom;
                    y = other;
                    z = pourTo;
                    if ((x != cap[3]) && (z <= cap[2])) {
                        x--;
                        z++;
                    }
                    else {cont = true; break;}
                    break;
                case YX:
                    x = pourTo;
                    y = pourFrom;
                    z = other;
                    if ((x <= cap[0]) && (y != cap[3])) {
                        x++;
                        y--;
                    }
                    else {cont = true; break;}
                    break;
                case YZ:
                    x = other;
                    y = pourFrom;
                    z = pourTo;
                    if ((y != cap[3])&&(z <= cap[2])) {
                        y--;
                        z++;
                    }
                    else {cont = true; break;}
                    break;
                case ZX:
                    x = pourTo;
                    y = other;
                    z = pourFrom;
                    if ((x <= cap[0]) && (z != cap[3])) {
                        x++;
                        z--;
                    }
                    else {cont = true; break;}
                    break;
                case ZY:
                    x = other;
                    y = pourTo;
                    z = pourFrom;
                    if ((y <= cap[1]) && (z != cap[3])) {
                        y++;
                        z--;
                    }
                    else {cont = true; break;}
                    break;
            }

            node.set(x, y, z);

            stack.push(node);

            if ((x == tenWanted && y == sevenWanted && z == fourWanted)) {
                hasFound = true;
            }

            else if (loops > 24) {
                cantBeFound = true;
            }

            else {

                if (cont && !hasFound) {

                    switch (pour) {
                        case XY:
                            loops++;
                            pourFromTo(x, z, y, tenWanted, sevenWanted, fourWanted, pours.XZ);
                            break;
                        case XZ:
                            loops++;
                            pourFromTo(y, x, z, tenWanted, sevenWanted, fourWanted, pours.YX);
                            break;
                        case YX:
                            loops++;
                            pourFromTo(y, z, x, tenWanted, sevenWanted, fourWanted, pours.YZ);
                            break;
                        case YZ:
                            loops++;
                            pourFromTo(z, x, y, tenWanted, sevenWanted, fourWanted, pours.ZX);
                            break;
                        case ZX:
                            loops++;
                            pourFromTo(z, y, x, tenWanted, sevenWanted, fourWanted, pours.ZY);
                            break;
                        case ZY:
                            loops++;
                            pourFromTo(x, y, z, tenWanted, sevenWanted, fourWanted, pours.XY);
                            break;
                    }
                }

                else {

                    switch (pour) {
                        case XY:
                            pourFromTo(x, y, z, tenWanted, sevenWanted, fourWanted, pours.XY);
                            break;
                        case XZ:
                            pourFromTo(x, z, y, tenWanted, sevenWanted, fourWanted, pours.XZ);
                            break;
                        case YX:
                            pourFromTo(y, x, z, tenWanted, sevenWanted, fourWanted, pours.YX);
                            break;
                        case YZ:
                            pourFromTo(y, z, x, tenWanted, sevenWanted, fourWanted, pours.YZ);
                            break;
                        case ZX:
                            pourFromTo(z, x, y, tenWanted, sevenWanted, fourWanted, pours.ZX);
                            break;
                        case ZY:
                            pourFromTo(z, y, x, tenWanted, sevenWanted, fourWanted, pours.ZY);
                            break;

                    }
                }
            }
        }

        return false;
    }

    public static void Pouring(int tenPints, int sevenPints, int fourPints, int tenWanted, int sevenWanted, int fourWanted) {

        for(pours a : pours.values()) {
            if (pourFromTo(tenPints, sevenPints, fourPints, tenWanted, sevenWanted, fourWanted, a))
                break;
        }

        Node<Integer> foundN = findNode(tenWanted, sevenWanted, fourWanted);

        if (foundN != null) {

            if (loops == 0)
                loops = 1;
            System.out.println("\nOnly " + loops + " pouring operation(s) required to find the solution:");
            System.out.print("(" + tenPints + ", " + sevenPints + ", " + fourPints + ")" + ", ");
            for (int i = 0; i < stack.size() - 1; i++) {
                Node<Integer> node = stack.get(i);
                if ((node.equates(stack.get(i + 1)))) {
                    stack.remove(i + 1);

                }
                else System.out.print(stack.get(i));
            }
            System.out.println(foundN.toString());
        }
        else
            System.out.println("No sequence of pourings from " + "(" + tenPints + ", " + sevenPints + ", " + fourPints + ") to " + "(" + tenWanted + ", " + sevenWanted + ", " + fourWanted + ")");
    }

}

class Node<T> {
    private T x, y, z;

    public void set(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equates(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(x, node.x) &&
                Objects.equals(y, node.y) &&
                Objects.equals(z, node.z);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")" + " ";
    }

}