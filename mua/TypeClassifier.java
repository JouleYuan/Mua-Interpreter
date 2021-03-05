package mua;

import java.util.regex.Pattern;

public class TypeClassifier {
    public boolean isBool(String element){
        return element.equals("true") || element.equals("false");
    }

    public boolean isNumber(String element) {
        if (element == null) return false;
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(element).matches();
    }
}
