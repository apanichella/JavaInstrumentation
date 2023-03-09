package nl.tudelft.instrumentation.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Word<T> {

    private List<T> word;

    
    Word(List<T> word) {
        this.word = new ArrayList<>(word);
    }

    Word(T... symbols) {
        this.word = Arrays.asList(symbols);
    }


    @Override
    public boolean equals(Object other) {
        if(other instanceof Word<?>){
            Word<?> that = (Word<?>) other;
            return that.word.equals(this.word);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    public List<T> asList() {
        return new ArrayList<T>(this.word);
    }

    public Word<T> append(List<T> suffix) {
        List<T> symbols = new ArrayList<T>(word);
        symbols.addAll(suffix);
        return new Word<T>(symbols);
    }

    public Word<T> append(T suffix) {
        List<T> symbols = new ArrayList<T>(word);
        symbols.add(suffix);
        return new Word<T>(symbols);
    }

    public Word<T> append(Word<T> suffix) {
        return append(suffix.word);
    }

    public String toString() {
        return String.join(",", word.stream().map(x -> x.toString()).collect(Collectors.toList()));
    }
}
