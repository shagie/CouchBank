package net.shagie.couchbank.load;

public class Tuple<A, B> {
    private A first;
    private B second;

    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public Tuple<A, B> setFirst(A first) {
        this.first = first;
        return this;
    }

    public B getSecond() {
        return second;
    }

    public Tuple<A, B> setSecond(B second) {
        this.second = second;
        return this;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
