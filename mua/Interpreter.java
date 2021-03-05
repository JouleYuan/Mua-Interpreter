package mua;

import java.io.*;
import java.util.*;

public class Interpreter {
	private int cnt;
	private String[] element;
	private final Map<String, Variable> map;
	private final Map<String, Variable> localMap;
	private final Map<String, String> argNameMap;
	private final TypeClassifier typeClassifier;

	Scanner input=new Scanner(System.in);

	Interpreter(){
		map= new HashMap<>();
		localMap = new HashMap<>();
		argNameMap = new HashMap<>();
		typeClassifier=new TypeClassifier();
	}

	Interpreter(Map<String, Variable> map, Map<String, Variable> argMap, Map<String, String> argNameMap){
		this.map = map;
		localMap = argMap;
		this.argNameMap = argNameMap;
		typeClassifier=new TypeClassifier();
	}

	public boolean init() {
		if(input.hasNext()) {
			cnt=0;
			int left=0, right=0;
			StringBuilder sentenceBuilder = new StringBuilder();
			do{
				String line = input.nextLine();
				left += line.length() - line.replaceAll("\\[", "").length();
				right += line.length() - line.replaceAll("]", "").length();
				sentenceBuilder.append(" ").append(
						line.replaceAll("\\[", " \\[ ")
								.replaceAll("]", " ] "));
			}while(left != right);
			String sentence = sentenceBuilder.toString();
			element= separateElement(sentence.replaceAll("\t", " "));
			return true;
		}else return false;
	}

	private boolean init(Queue<String> lines){
		if(!lines.isEmpty()){
			cnt=0;
			int left=0, right=0;
			StringBuilder sentenceBuilder = new StringBuilder();
			do{
				String line = lines.poll();
				left += line.length() - line.replaceAll("\\[", "").length();
				right += line.length() - line.replaceAll("]", "").length();
				sentenceBuilder.append(" ").append(
						line.replaceAll("\\[", " \\[ ")
								.replaceAll("]", " ] "));
			}while(left != right);
			String sentence = sentenceBuilder.toString();
			element= separateElement(sentence.replaceAll("\t", " "));
			return true;
		}else return false;
	}

	public void init(int start, String[] functionElement) {
		cnt=start;
		element = functionElement;
	}

	public Pair parse() {
		if(cnt>=element.length) init();
		String thisElement=element[cnt++];
		switch(thisElement) {
			case "make": return new Pair(make(), cnt);
			case "erase": return new Pair(erase(), cnt);
			case "read": return new Pair(read(), cnt);
			case "print": return new Pair(print(), cnt);
			case "thing": return new Pair(thing(), cnt);
			case "isname": return new Pair(isname(), cnt);
			case "run": return new Pair(run(), cnt);
			case "if": return new Pair(brunch(), cnt);
			case "add": return new Pair(parse().getKey().add(parse().getKey()), cnt);
			case "sub": return new Pair(parse().getKey().sub(parse().getKey()), cnt);
			case "mul": return new Pair(parse().getKey().mul(parse().getKey()), cnt);
			case "div": return new Pair(parse().getKey().div(parse().getKey()), cnt);
			case "mod": return new Pair(parse().getKey().mod(parse().getKey()), cnt);
			case "eq": return new Pair(parse().getKey().equal(parse().getKey()), cnt);
			case "gt": return new Pair(parse().getKey().greater(parse().getKey()), cnt);
			case "lt": return new Pair(parse().getKey().less(parse().getKey()), cnt);
			case "and": return new Pair(parse().getKey().and(parse().getKey()), cnt);
			case "or": return new Pair(parse().getKey().or(parse().getKey()), cnt);
			case "not": return new Pair(parse().getKey().not(), cnt);
			case "true": return new Pair(new Variable(true), cnt);
			case "false": return new Pair(new Variable(false), cnt);
			case "isbool": return new Pair(new Variable(parse().getKey().type.equals("bool")), cnt);
			case "isnumber": return new Pair(new Variable(parse().getKey().type.equals("number")), cnt);
			case "isword": return new Pair(new Variable(parse().getKey().type.equals("word")),cnt);
			case "islist": return new Pair(new Variable(parse().getKey().type.equals("list")), cnt);
			case "isempty": return new Pair(parse().getKey().isempty(), cnt);
			case "return": return new Pair(parse().getKey(), -1);
			case "export": return new Pair(export(), cnt);
			case "sentence": return new Pair(sentence(), cnt);
			case "list": return new Pair(list(), cnt);
			case "join": return new Pair(join(), cnt);
			case "first": return new Pair(first(), cnt);
			case "last": return new Pair(last(), cnt);
			case "butfirst": return new Pair(butfirst(), cnt);
			case "butlast": return new Pair(butlast(), cnt);
			case "save": return new Pair(save(), cnt);
			case "load": return new Pair(load(), cnt);
			case "erall": return new Pair(erall(), cnt);
			default:
				if(thisElement.startsWith(":")) return new Pair(getVariable(thisElement.substring(1)), cnt);
				else if(thisElement.startsWith("\"")) return new Pair(new Variable(thisElement.substring(1)), cnt);
				else if(typeClassifier.isNumber(thisElement)) return new Pair(new Variable(thisElement), cnt);
				else if(thisElement.startsWith("[")) return new Pair(list(thisElement.substring(1)), cnt);
				else if(thisElement.startsWith("(")) return new Pair(expression(thisElement), cnt);
				else if(localMap.containsKey(thisElement) || map.containsKey(thisElement))
					return new Pair(function(getVariable(thisElement)), cnt);
		}
		return new Pair(new Variable(""), cnt);
	}

	private String[] separateElement(String sentence){
		ArrayList<String> bracket=new ArrayList<>();
		int start=0, end;
		while(true){
			end=sentence.indexOf('(', start);
			if(end==-1) break;
			else bracket.add(sentence.substring(start,end));
			start=end;
			for(int i=start+1, cnt=0;i<sentence.length();i++){
				if(sentence.charAt(i)=='(') cnt++;
				else if(sentence.charAt(i)==')'){
					if(cnt==0) {
						end=i+1;
						break;
					}
					else cnt--;
				}
			}
			bracket.add(sentence.substring(start,end));
			start=end;
		}
		bracket.add(sentence.substring(start));

		ArrayList<String> element=new ArrayList<>();
		for (String s : bracket) {
			String subSentence = s.trim();
			if (!subSentence.equals("")){
				if(subSentence.startsWith("(")) element.add(subSentence);
				else{
					String[] subElement = subSentence.split(" +");
					element.addAll(Arrays.asList(subElement));
				}
			}
		}
		if(element.isEmpty()) element.add("");
		return element.toArray(new String[0]);
	}

	private Variable getVariable(String name){
		if(localMap.containsKey(name)) return localMap.get(name);
		else if(map.containsKey(name)) return map.get(name);
		else return new Variable("");
	}

	private Variable removeVariable(String name){
		if(localMap.containsKey(name)) return localMap.remove(name);
		else if(map.containsKey(name)) return map.remove(name);
		else return new Variable("");
	}

	private Variable list(String first){
		Variable list=new Variable();
		String thisElement;
		int left = 1, right = 0;
		int count = cnt;
		for(thisElement=first;;thisElement=element[count++]){
			left += thisElement.length() - thisElement.replaceAll("\\[","").length();
			right += thisElement.length() - thisElement.replaceAll("]","").length();
			if(left<=right) break;
		}
		for(thisElement = first;cnt<count; thisElement=element[cnt++]){
			if(thisElement.equals("")) continue;
			if(thisElement.startsWith("[")) list.element.add(list(thisElement.substring(1)));
			else list.element.add(new Variable(thisElement));
		}
		int index=thisElement.indexOf("]");
		element[--cnt]=thisElement.substring(index+1);
		if(element[cnt].equals("")) cnt++;
		if(!thisElement.substring(0,index).equals(""))
			list.element.add(new Variable(thisElement.substring(0,index)));
		return list;
	}

	private Variable expression(String sentence){
		ArrayList<String> element=new ArrayList<>(Arrays.asList(separateElement(sentence.substring(1, sentence.length() - 1))));
		for(int i=0;i<element.size();i++){
			switch(element.get(i)){
				case "add": preToIn(element,i,"+"); break;
				case "sub": preToIn(element,i,"-"); break;
				case "mul": preToIn(element,i,"*"); break;
				case "div": preToIn(element,i,"/"); break;
				case "mod": preToIn(element,i,"%"); break;
				default:
					if(element.get(i).startsWith("(")) element.set(i,String.valueOf(expression(element.get(i)).value));
					else{
						StringBuilder str=new StringBuilder(element.get(i));
						for(int start=str.indexOf(":");start!=-1;start=str.indexOf(":",start+1)){
							int end=str.length();
							int tmp=str.indexOf("+",start);
							if(tmp!=-1) end=tmp;
							tmp=str.indexOf("-",start);
							if(tmp!=-1&&tmp<end) end=tmp;
							tmp=str.indexOf("*",start);
							if(tmp!=-1&&tmp<end) end=tmp;
							tmp=str.indexOf("/",start);
							if(tmp!=-1&&tmp<end) end=tmp;
							tmp=str.indexOf("%",start);
							if(tmp!=-1&&tmp<end) end=tmp;
							str.insert(end,getVariable(str.substring(start+1,end)).value);
							str.delete(start,end);
						}
						element.set(i,str.toString());
					}
					break;
			}
		}
		for(int count=0; count<element.size(); count++){
			if(localMap.containsKey(element.get(count)) || map.containsKey(element.get(count))){
				Interpreter functionInterpreter = new Interpreter(map, localMap, argNameMap);
				functionInterpreter.init(count, element.toArray(new String[element.size()]));
				Pair pair = functionInterpreter.parse();
				int end = pair.getValue();
				Variable result = pair.getKey();
				if (end > count) {
					element.subList(count, end).clear();
				}
				element.add(count, result.value);
			}
		}
		StringBuilder expression=new StringBuilder();
		for(String thisElement:element) expression.append(thisElement);
		Operation operation=new Operation();
		return operation.eval(expression.toString());
	}

	private void preToIn(ArrayList<String> element, int i, String operator){
		element.remove(i);
		element.set(i,"("+element.get(i)+")");
		element.set(i+1,"("+element.get(i+1)+")");
		element.add(i+1,operator);
	}

	private Variable export(){
		Variable name = parse().getKey();
		Variable value = getVariable(name.value);
		if(argNameMap.containsKey(name.value)) name = new Variable(argNameMap.get(name.value));
		map.put(name.value, value);
		return value;
	}

	private Variable make() {
		Variable name=parse().getKey();
		Variable value=parse().getKey();
		localMap.put(name.value, value);
		return value;
	}

	private Variable erase(){
		Variable name=parse().getKey();
		return removeVariable(name.value);
	}

	private Variable read() {
		String in=input.nextLine();
		return new Variable(in);
	}

	private Variable print() {
		Variable value=parse().getKey();
		if(value.type.equals("list")){
			String[] sentence = value.toSentence();
			for(int i=0; i< sentence.length; i++){
				if(i!=0&&!sentence[i-1].equals("[")&&!sentence[i].equals("]")) System.out.print(" ");
				System.out.print(sentence[i]);
			}
			System.out.println();
		}else System.out.println(value.value);
		return value;
	}

	private Variable thing() {
		Variable name=parse().getKey();
		return getVariable(name.value);
	}

	private Variable isname(){
		Variable word=parse().getKey();
		return new Variable(localMap.containsKey(word.value) || map.containsKey(word.value));
	}

	private Variable run(){
		Variable list=parse().getKey();
		return execute(list);
	}

	private Variable brunch(){
		boolean bool=Boolean.parseBoolean(parse().getKey().value);
		Variable[] list={parse().getKey(), parse().getKey()};
		Variable value;
		if(bool) value=execute(list[0]);
		else value=execute(list[1]);
		if(!value.type.equals("list") && value.value.equals("")) return new Variable();
		else return value;
	}

	private Variable execute(Variable list){
		String[] sentenceElement=Arrays.copyOf(element, element.length);
		int sentenceCnt=cnt;
		element=list.toSentence();
		cnt=0;
		Variable value=new Variable("");
		while(cnt<element.length) {
			Variable tmpValue=parse().getKey();
			if(tmpValue.type.equals("list") || !tmpValue.value.equals("")) value=tmpValue;
		}
		element=Arrays.copyOf(sentenceElement, sentenceElement.length);
		cnt=sentenceCnt;
		return value;
	}

	private Variable function(Variable list){
		final Map<String, Variable> argMap = new HashMap<>();
		final Map<String, String> argNameMap = new HashMap<>();
		Variable argList = list.element.get(0);
		for(Variable arg : argList.element){
			if(cnt < element.length && element[cnt].startsWith("\""))
				argNameMap.put(arg.value, element[cnt].substring(1));
			argMap.put(arg.value, parse().getKey());
		}
		String[] sentenceElement=list.element.get(1).toSentence();

		final Map<String, Variable> functionMap = new HashMap<>(map);
		functionMap.putAll(localMap);
		Interpreter functionInterpreter = new Interpreter(functionMap, argMap, argNameMap);
		int count = 0;
		Variable result = new Variable("");
		while(count<sentenceElement.length && count != -1) {
			functionInterpreter.init(count, sentenceElement);
			Pair pair = functionInterpreter.parse();
			count = pair.getValue();
			result = pair.getKey();
		}

		for(Map.Entry<String, Variable> entry : functionMap.entrySet()){
			String name = entry.getKey();
			if(localMap.containsKey(name)) localMap.put(name, entry.getValue());
			else map.put(name, entry.getValue());
		}

		return result;
	}

	private Variable sentence(){
		Variable v1 = parse().getKey();
		Variable v2 = parse().getKey();
		Variable list;
		if(v1.type.equals("list")){
			list=v1;
			if(v2.type.equals("list")) list.element.addAll(v2.element);
			else list.element.add(v2);
		}else if(v2.type.equals("list")){
			list=v2;
			list.element.add(0, v1);
		}else{
			list=new Variable();
			list.element.add(v1);
			list.element.add(v2);
		}
		return list;
	}

	private Variable list(){
		Variable v1 = parse().getKey();
		Variable v2 = parse().getKey();
		Variable list=new Variable();
		list.element.add(v1);
		list.element.add(v2);
		return list;
	}

	private Variable join(){
		Variable v1 = parse().getKey();
		Variable v2 = parse().getKey();
		Variable list;
		if(v1.type.equals("list")){
			list=v1;
			list.element.add(v2);
		}else{
			list=new Variable();
			list.element.add(v1);
			list.element.add(v2);
		}
		return list;
	}

	private Variable first(){
		Variable v = parse().getKey();
		if(v.type.equals("list")) return v.element.get(0);
		else return new Variable(v.value.substring(0, 1));
	}

	private Variable last(){
		Variable v = parse().getKey();
		if(v.type.equals("list")) return v.element.get(v.element.size()-1);
		else return new Variable(v.value.substring(v.value.length()-1));
	}

	private Variable butfirst(){
		Variable v = new Variable(parse().getKey());
		if(v.type.equals("list")){
			v.element.remove(0);
			return v;
		}else return new Variable(v.value.substring(1));
	}

	private Variable butlast(){
		Variable v = new Variable(parse().getKey());
		if(v.type.equals("list")){
			v.element.remove(v.element.size()-1);
			return v;
		}else return new Variable(v.value.substring(0, v.value.length()-1));
	}

	private Variable save() {
		Variable file = parse().getKey();

		StringBuilder builder = new StringBuilder();
		for(Map.Entry<String, Variable> entry : localMap.entrySet()){
			builder.append("make \"").append(entry.getKey()).append(" ");
			Variable v = entry.getValue();
			if(v.type.equals("list")){
				builder.append("[");
				String[] sentence = v.toSentence();
				for(String word : sentence) builder.append(" ").append(word);
				builder.append(" ]");
			}else builder.append(v.value);
			builder.append("\n");
		}
		String data = builder.toString();

		try{
			FileWriter out = new FileWriter(new File(file.value), false);
			out.write(data);
			out.close();
		}catch (IOException e){
			e.getStackTrace();
		}

		return file;
	}

	private Variable load(){
		Variable file = parse().getKey();

		Queue<String> lines=new LinkedList<>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(file.value));
			String line;
			while((line=in.readLine())!=null) lines.offer(line);
		}catch (IOException e){
			e.getStackTrace();
		}

		while(init(lines)) {
			try{
				parse();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return new Variable(true);
	}

	private Variable erall(){
		localMap.clear();
		return new Variable(true);
	}
}
