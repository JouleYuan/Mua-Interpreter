package mua;

public class Pair {
    private Variable key;
    private Integer value;

    Pair(Variable key, Integer value){
        this.key = key;
        this.value = value;
    }

    public Variable getKey(){
        return key;
    }

    public Integer getValue(){
        return value;
    }
}
