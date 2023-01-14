package ru.leonidm.datapacktool.entities;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Pair<A, B> {

    protected @Nullable A a;
    protected @Nullable B b;

    public Pair(@Nullable A a, @Nullable B b) {
        this.a = a;
        this.b = b;
    }

    @Nullable
    public A getLeft() {
        return a;
    }

    public void setLeft(@Nullable A a) {
        this.a = a;
    }

    @Nullable
    public B getRight() {
        return b;
    }

    public void setRight(@Nullable B b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "Pair{" + a + ", " + b + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
