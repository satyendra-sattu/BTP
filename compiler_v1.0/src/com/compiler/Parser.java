package com.compiler;



//Add space error handler
public class Parser {

	
	public static void factor(){
		if(Utils.token=='('){
			Utils.next();
			boolExpression();
			Utils.matchString(")");
		}else{
			if(Utils.token=='x')
				Utils.loadVar(Utils.value);
			else if(Utils.token=='#')
				Utils.loadConst(Utils.value);
			else
				Utils.expected("Math Factor");
			Utils.next();
		}
	}
	
	public static void term(){
		factor();
		while(Utils.isMulop(Utils.token)){
			Utils.push();
			switch(Utils.token){
			case '*':multiply();break;
			case '/':divide();break;
			}
		}
	}
	
	
	public static void expression(){
		if(Utils.isAddop(Utils.token))
			Utils.clear();
		else
			term();
		while(Utils.isAddop(Utils.token)){
			Utils.push();
			switch(Utils.token){
			case '+': add();break;
			case '-': subtract();break;
			}
		}
	}
	
	public static void compareExpression(){
		expression();
		Utils.popCompare();
	}
	
	public static void nextExpression(){
		Utils.next();
		compareExpression();
	}
	
	public static void equal(){
		nextExpression();
		Utils.setEqual();
	}
	
	public static void lessOrEqual(){
		nextExpression();
		Utils.setLessOrEqual();
	}
	
	public static void notEqual(){
		nextExpression();
		Utils.setNEqual();
	}
	
	public static void less(){
		Utils.next();
		switch(Utils.token){
		case '=':lessOrEqual();break;
		case '>': notEqual();break;
		default:compareExpression();Utils.setLess();break;
		}
	}
	
	public static void greater(){
		Utils.next();
		if(Utils.token=='='){
			nextExpression();
			Utils.setGreaterOrEqual();
		}else{
			compareExpression();
			Utils.setGreater();
		}
	}
	
	
	public static void relation(){
		expression();
		if(Utils.isRelOp(Utils.token)){
			Utils.push();
			switch(Utils.token){
			case '=': equal();break;
			case '<': less();break;
			case '>': greater();break;
			}
		}
	}
	
	public static void notFactor(){
		if(Utils.look=='!'){
			Utils.next();
			relation();
			Utils.notIt();
		}else{
			relation();
		}
	}
	
	public static void boolTerm(){
		notFactor();
		while(Utils.token=='&'){
			Utils.push();
			Utils.next();
			notFactor();
			Utils.popAnd();
			
		}
	}
	
	public static void boolOr(){
		Utils.next();
		boolTerm();
		Utils.popOr();
	}
	
	
	public static void boolXor(){
		Utils.next();
		boolTerm();
		Utils.popXor();
	}
	
	public static void boolExpression(){
		boolTerm();
		while(Utils.isOrOp(Utils.token)){
			Utils.push();
			switch(Utils.look){
			case '|':boolOr();break;
			case '~':boolXor();break;
			}
		}
	}
	
	public static void assignment(){
		String name;
		Utils.checkTable(Utils.value);
		name=Utils.value;
		Utils.next();
		Utils.matchString("=");
		boolExpression();
		Utils.store(name);
	}
	
	public static void doIf(){
		String l1,l2;
		
		Utils.next();
		boolExpression();
		l1=Utils.newLabel();
		l2=l1;
		Utils.branchFalse(l1);
		block();
		if(Utils.token=='l'){
			Utils.next();
			l2=Utils.newLabel();
			Utils.branch(l2);
			Utils.postLabel(l1);
			block();
		}
		
		Utils.postLabel(l2);
		Utils.matchString("ENDIF");
	}
	
	public static void doWhile(){
		String l1,l2;
		Utils.next();
		l1=Utils.newLabel();
		l2=Utils.newLabel();
		Utils.postLabel(l1);
		boolExpression();
		Utils.branchFalse(l2);
		block();
		Utils.matchString("ENDWHILE");
		Utils.branch(l1);
		Utils.postLabel(l2);
	}
	
	
	
	public static void readVar(){
		Utils.checkIdent();
		Utils.checkTable(Utils.value);
		Utils.readIt(Utils.value);
		Utils.next();
	}
	
	public static void doRead(){
		Utils.next();
		Utils.matchString("(");
		readVar();
		while(Utils.token==','){
			Utils.next();
			readVar();
		}
		Utils.matchString(")");
	}
	
	public static void doWrite(){
		Utils.next();
		Utils.matchString("(");
		expression();
		Utils.writeIt();
		while(Utils.token==','){
			Utils.next();
			expression();
			Utils.writeIt();
		}
		Utils.matchString(")");
		
	}
	
	public static void block(){
		Utils.scan();
		while(Utils.token!='e' && Utils.token!='l'){
			//Utils.fin();
			switch(Utils.token){
			case 'i': doIf();break;
			case 'w':doWhile();break;
			case 'R': doRead();break;
			case 'W': doWrite();break;
			default:assignment();
			}
			Utils.scan();		
		}
	}
	
	public static void alloc(){
		Utils.next();
		if(Utils.token!='x')
			Utils.expected("Variable Name");
		Utils.checkDup(Utils.value);
		Utils.addEntry(Utils.value, 'v');
		Utils.allocate(Utils.value, "0");
		Utils.next();
	}
	
	public static void topDecls(){
		Utils.scan();
		while(Utils.token=='v')
			alloc();
		while(Utils.token==',')
			alloc();
	}
	
	
	public static void add(){
		Utils.next();
		term();
		Utils.popAdd();
	}
	
	public static void subtract(){
		Utils.next();
		term();
		Utils.popSub();
	}
	
	public static void multiply(){
		Utils.next();
		factor();
		Utils.popMul();
	}
	
	public static void divide(){
		Utils.next();
		factor();
		Utils.popDiv();
	}
	
}