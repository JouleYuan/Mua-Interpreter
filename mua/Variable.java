package mua;

import java.util.ArrayList;

public class Variable {
    public String type;
    public String value;
    public ArrayList<Variable> element;

    public Variable(){
        type="list";
        value=null;
        element= new ArrayList<>();
    }

    public Variable(boolean val){
        type="bool";
        value=String.valueOf(val);
    }

    public Variable(double val){
        type="number";
        value=String.valueOf(val);
    }

    public Variable(String val){
        TypeClassifier typeClassifier=new TypeClassifier();
        if(typeClassifier.isBool(val)) type="bool";
        else if(typeClassifier.isNumber(val)) type="number";
        else type="word";
        value=val;
    }

    public Variable(Variable val){
        type=val.type;
        value=val.value;
        element=new ArrayList<>();
        if(val.element!=null) element.addAll(val.element);
    }

    public Variable add(Variable operand){
        return new Variable(Double.parseDouble(value)+Double.parseDouble(operand.value));
    }

    public Variable sub(Variable operand){
        return new Variable(Double.parseDouble(value)-Double.parseDouble(operand.value));
    }

    public Variable mul(Variable operand){
        return new Variable(Double.parseDouble(value)*Double.parseDouble(operand.value));
    }

    public Variable div(Variable operand){
        return new Variable(Double.parseDouble(value)/Double.parseDouble(operand.value));
    }

    public Variable mod(Variable operand){
        return new Variable(Double.parseDouble(value)%Double.parseDouble(operand.value));
    }

    public Variable equal(Variable operand){
        if(type.equals("list") || operand.type.equals("list")) return new Variable(false);
        if(type.equals("number") || operand.type.equals("number"))
            return new Variable(Double.parseDouble(value) == Double.parseDouble(operand.value));
        else return new Variable(value.equals(operand.value));
    }

    public Variable greater(Variable operand){
        if(type.equals("number") || operand.type.equals("number"))
            return new Variable(Double.parseDouble(value) > Double.parseDouble(operand.value));
        else return new Variable(value.compareTo(operand.value)>0);
    }

    public Variable less(Variable operand){
        if(type.equals("number") || operand.type.equals("number"))
            return new Variable(Double.parseDouble(value) < Double.parseDouble(operand.value));
        else return new Variable(value.compareTo(operand.value)<0);
    }

    public Variable and(Variable operand){
        return new Variable(Boolean.parseBoolean(value)&&Boolean.parseBoolean(operand.value));
    }

    public Variable or(Variable operand){
        return new Variable(Boolean.parseBoolean(value)||Boolean.parseBoolean(operand.value));
    }

    public Variable not(){
        return new Variable(!Boolean.parseBoolean(value));
    }

    public Variable isempty(){
        if(type.equals("word")) return new Variable(value.equals(""));
        else if(type.equals("list")) return new Variable(element.isEmpty());
        else return new Variable(false);
    }

    public String[] toSentence(){
        ArrayList<String> listElement=new ArrayList<>();
        for (Variable thisElement : element) {
            if (thisElement.type.equals("list")) thisElement.toSentence(listElement);
            else listElement.add(thisElement.value);
        }
        return listElement.toArray(new String[0]);
    }

    private void toSentence(ArrayList<String> listElement){
        listElement.add("[");
        for (Variable thisElement : element) {
            if (thisElement.type.equals("list")) thisElement.toSentence(listElement);
            else listElement.add(thisElement.value);
        }
        listElement.add("]");
    }
}
