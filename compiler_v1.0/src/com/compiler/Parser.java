package com.compiler;



//Add space error handler
public class Parser extends Utils {

	
	public  void factor(){
		if(token=='('){
			next();
			boolExpression();
			matchString(")");
		}else{
			if(token=='x')
				loadVar(value);
			else if(token=='#')
				loadConst(value);
			else
				expected("Math Factor");
			next();
		}
	}
	
	public  void term(){
		factor();
		while(isMulop(token)){
			push();
			switch(token){
			case '*':multiply();break;
			case '/':divide();break;
			}
		}
	}
	
	
	public  void expression(){
		if(isAddop(token))
			clear();
		else
			term();
		while(isAddop(token)){
			push();
			switch(token){
			case '+': add();break;
			case '-': subtract();break;
			}
		}
	}
	
	public  void compareExpression(){
		expression();
		popCompare();
	}
	
	public  void nextExpression(){
		next();
		compareExpression();
	}
	
	public  void equal(){
		nextExpression();
		setEqual();
	}
	
	public  void lessOrEqual(){
		nextExpression();
		setLessOrEqual();
	}
	
	public  void notEqual(){
		nextExpression();
		setNEqual();
	}
	
	public  void less(){
		next();
		switch(token){
		case '=':lessOrEqual();break;
		case '>': notEqual();break;
		default:compareExpression();setLess();break;
		}
	}
	
	public  void greater(){
		next();
		if(token=='='){
			nextExpression();
			setGreaterOrEqual();
		}else{
			compareExpression();
			setGreater();
		}
	}
	
	
	public  void relation(){
		expression();
		if(isRelOp(token)){
			push();
			switch(token){
			case '=': equal();break;
			case '<': less();break;
			case '>': greater();break;
			}
		}
	}
	
	public  void notFactor(){
		if(look=='!'){
			next();
			relation();
			notIt();
		}else{
			relation();
		}
	}
	
	public  void boolTerm(){
		notFactor();
		while(token=='&'){
			push();
			next();
			notFactor();
			popAnd();
			
		}
	}
	
	public  void boolOr(){
		next();
		boolTerm();
		popOr();
	}
	
	
	public  void boolXor(){
		next();
		boolTerm();
		popXor();
	}
	
	public  void boolExpression(){
		boolTerm();
		while(isOrOp(token)){
			push();
			switch(look){
			case '|':boolOr();break;
			case '~':boolXor();break;
			}
		}
	}
	
	public  void assignment(){
		String name;
		checkTable(value);
		name=value;
		next();
		matchString("=");
		boolExpression();
		store(name);
	}
	
	public  void doIf(){
		String l1,l2;
		
		next();
		boolExpression();
		l1=newLabel();
		l2=l1;
		branchFalse(l1);
		block();
		if(token=='l'){
			next();
			l2=newLabel();
			branch(l2);
			postLabel(l1);
			block();
		}
		
		postLabel(l2);
		matchString("ENDIF");
	}
	
	public  void doWhile(){
		String l1,l2;
		next();
		l1=newLabel();
		l2=newLabel();
		postLabel(l1);
		boolExpression();
		branchFalse(l2);
		block();
		matchString("ENDWHILE");
		branch(l1);
		postLabel(l2);
	}
	
	
	
	public  void readVar(){
		checkIdent();
		checkTable(value);
		readIt(value);
		next();
	}
	
	public  void doRead(){
		next();
		matchString("(");
		readVar();
		while(token==','){
			next();
			readVar();
		}
		matchString(")");
	}
	
	public  void doWrite(){
		next();
		matchString("(");
		expression();
		writeIt();
		while(token==','){
			next();
			expression();
			writeIt();
		}
		matchString(")");
		
	}
	
	public  void block(){
		scan();
		while(token!='e' && token!='l'){
			//fin();
			switch(token){
			case 'i': doIf();break;
			case 'w':doWhile();break;
			case 'R': doRead();break;
			case 'W': doWrite();break;
			default:assignment();
			}
			scan();		
		}
	}
	
	public  void alloc(){
		next();
		if(token!='x')
			expected("Variable Name");
		checkDup(value);
		addEntry(value, 'v');
		allocate(value, "0");
		next();
	}
	
	public  void topDecls(){
		scan();
		while(token=='v')
			alloc();
		while(token==',')
			alloc();
	}
	
	
	public  void add(){
		next();
		term();
		popAdd();
	}
	
	public  void subtract(){
		next();
		term();
		popSub();
	}
	
	public  void multiply(){
		next();
		factor();
		popMul();
	}
	
	public  void divide(){
		next();
		factor();
		popDiv();
	}
	
}