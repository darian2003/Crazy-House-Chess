/* clasa Pair va fi folosita pentru a retine 2 pozitii:
    - prima este cea a piesei care ataca
    - a doua este pozitia atacata */

public class Pair<T, U> {
    private T first;
    private U second;

    Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(U second) {
        this.second = second;
    }
}
