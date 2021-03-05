package mua;

public class Operation {
    String exp;
    int pos=-1, ch;

    public Variable eval(String expression){
        exp=expression;
        ch=nextChar();
        return new Variable(parseExpression());
    }

    private double parseExpression(){
        double ret=parseTerm();
        while(true){
            if(eat('+')) ret+=parseTerm();
            else if(eat('-')) ret-=parseTerm();
            else return ret;
        }
    }

    private double parseTerm(){
        double ret=parseFactor();
        while(true){
            if(eat('*')) ret*=parseFactor();
            else if(eat('/')) ret/=parseFactor();
            else if(eat('%')) ret%=parseFactor();
            else return ret;
        }
    }

    private double parseFactor(){
        if(eat('+')) return parseFactor();
        if(eat('-')) return -parseFactor();

        double ret=0;
        int startPos=pos;
        if(eat('(')){
            ret=parseExpression();
            eat(')');
        }else if((ch>='0'&&ch<='9')||ch=='.'){
            while((ch>='0'&&ch<='9')||ch=='.') ch=nextChar();
            ret=Double.parseDouble(exp.substring(startPos,pos));
        }
        return ret;
    }

    private boolean eat(int target){
        while(ch==' ') ch=nextChar();
        if(ch==target){
            ch=nextChar();
            return true;
        }
        else return false;
    }

    private int nextChar(){
        if(++pos<exp.length()) return exp.charAt(pos);
        else return -1;
    }
}
