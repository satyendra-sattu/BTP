package com.compiler;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class Utils {
	
	
	 int lCount =0;
	 int nEntry =0;
	
	 char look;      //look ahead
	 char token;     //encoded token
	 String value;	//unencoded token
	
	 final int maxEntry=100;
	
	 String[] sT=new String[maxEntry];
	 char[] sType=new char[maxEntry];
	
	 final int nKW=9;
	 final int nKW1=10;
	
	 final String[] kwList={"IF","ELSE","ENDIF","WHILE","ENDWHILE","READ","WRITE","VAR","END"};
	 final String kwCode="xileweRWve";
	
	public  void printError(String msg){
		System.out.println(msg);
	}
	
	public  void getChar(){
		try {
			look=(char)System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
			abort("Error reading character");
		}
	}
	
	public  void abort(String msg){
		printError(msg);
		System.exit(1);
	}
	
	public  void expected(String msg){
		abort(msg+" Expected");
	}
	
	public  void undefined(String n){
		abort("Undefined Identifier "+n);
	}
	
	public  void duplicate(String n){
		abort("Duplicate Identifier "+n);
	}
	
	public  void checkIdent(){
		if(token!='x'){
			expected("Identifier");
		}
	}

	public  void matchChar(char x){
		if(look==x){
			getChar();
			skipWhite();
		}
		else
			expected("'"+x+"'");
	}
	
	public  boolean isAlpha(char x){
		char x1=Character.toUpperCase(x);
		if(x1<='Z' && x1>='A')
			return true;
		return false;
	}
	
	public  boolean isDigit(char x){
		if(x<='9' && x>='0')
			return true;
		return false;
	}
	
	public  boolean isAlphaNumeric(char c){
		return isAlpha(c) || isDigit(c);
	}
	
	public  boolean isAddop(char c){
		if(c=='+' || c=='-'){
			return true;
		}
		return false;
	}
	
	public  boolean isMulop(char c){
		if(c=='*' || c=='/'){
			return true;
		}
		return false;
	}
	
	public  boolean isOrOp(char c){
		if(c=='|' || c=='~'){
			return true;
		}
		return false;
		
	}
	
	public  boolean isRelOp(char c){
		if(c=='=' || c=='#' || c=='<' || c=='>'){
			return true;
		}
		return false;
	}
	
	
	public  boolean isWhite(char c){
		if(c==' ' || c=='\t' || c=='\n')
			return true;
		return false;
	}
	
	public  void skipWhite(){
		while(isWhite(look))
			getChar();
	}
	
	public  int lookup(String[] T,String s,int n){
		int i;
		boolean found=false;
		i=n-1;
		while(i>=0 && !found){
			if(s.equalsIgnoreCase(T[i]))
				found=true;
			else
				i--;
		}
		return i;
	}
	
	public  int locate(String n){
		return lookup(sT, n, nEntry);
	}
	
	public  boolean inTable(String n){
		return lookup(sT, n, nEntry)!=0;
	}
	
	public  void checkTable(String n){
		if(!inTable(n))
			undefined(n);
	}
	
	public  void checkDup(String n){
		if(inTable(n))
			duplicate(n);
	}
	
	public  void addEntry(String n,char t){
		checkDup(n);
		if(nEntry==maxEntry){
			abort("System Table Full");
		}
		nEntry++;
		sT[nEntry]=n;
		sType[nEntry]=t;
	}
	
//	public  boolean isOp(char c){
//		if(c=='+' || c=='-' || c=='*' || c=='/' || c=='<' || c=='>' || c==':' || c=='='){
//			return true;
//		}
//		return false;
//	}
	
//	public  void fin(){
//		if(look=='\n')
//			getChar();
//		skipWhite();
//	}
	
	public  void getName(){
		skipWhite();
		if(!isAlpha(look))
			expected("Identifier");
		token='x';
		value="";
		do{
			value+=Character.toUpperCase(look);
			getChar();
		}while(isAlphaNumeric(look));
		skipWhite();
		
	}
	
	public  void getNum(){
		skipWhite();
		if(!isDigit(look))
			expected("Number");
		token='#';
		value="";
		do{
			value+=look;
			getChar();
		}while(isDigit(look));
		
		
	}
	
	public  void getOp(){
		skipWhite();
		token=look;
		value=look+"";
		getChar();
	}
	
	public  void next(){
		skipWhite();
		if(isAlpha(look))
			getName();
		else if(isDigit(look))
			getNum();
		else
			getOp();
	}
	
	public  void scan(){
		if(token=='x')
			token=kwCode.charAt(lookup(kwList, value, nKW)+1);
	}
	
	public  void matchString(String x){
		if(!value.equalsIgnoreCase(x)){
			expected("'"+x+"'");
		}
		next();
	}
	
	
	
	
	public  String newLabel(){
		String s;
		s="L"+lCount;
		lCount++;
		return s;
	}
	
	public  void postLabel(String label){
		System.out.println(label+":");
	}
	
	public  void clear(){
		emitLn("XOR EAX,EAX");
	}
	
	public  void negate(){
		emitLn("NEG EAX");
	}
	
	public  void notIt(){
		emitLn("NOT EAX");
	}
	
	public  void loadConst(String n){
		emitLn("MOV EAX,"+n);
	}
	
	public  void loadVar(String name){
		if(!inTable(name))
			undefined(name);
		emitLn("MOV EAX,["+name+"]");
	}
	
	public  void push(){
		emitLn("PUSH EAX");
	}
	
	public  void popAdd(){
		emitLn("POP EBX");
		emitLn("ADD EAX,EBX");
	}
	
	public  void popSub(){
		emitLn("POP EBX");
		emitLn("SUB EAX,EBX");
		emitLn("NEG EAX");;
	}
	
	public  void popMul(){
		emitLn("POP EBX");
		emitLn("IMUL EAX,EBX");
	}
	
	public  void popDiv(){
		emitLn("POP EBX");
		emitLn("IMUL EAX,EBX");
	}
	
	public  void popAnd(){
		emitLn("POP EBX");
		emitLn("AND EAX,EBX");
	}
	
	
	public  void popOr(){
		emitLn("POP EBX");
		emitLn("OR EAX,EBX");
	}
	
	public  void popXor(){
		emitLn("POP EBX");
		emitLn("XOR EAX,EBX");
	}
	
	public  void popCompare(){
		emitLn("POP EBX");
		emitLn("CMP EAX,EBX");
	}
	
	public  void setEqual(){
		emitLn("if equal");
	}
	
	public  void setNEqual(){
		emitLn("if not equal");
	}
	
	public  void setGreater(){
		emitLn("if greater");
	}
	
	public  void setLess(){
		emitLn("if less");
	}
	
	public  void setLessOrEqual(){
		emitLn("if less or equal");
	}
	
	public  void setGreaterOrEqual(){
		emitLn("if greater or equal");
	}
	
	public  void store(String Name){
		emitLn("MOV ["+Name+"],EAX");
	}
	
	public  void branch(String l){
		emitLn("JMP "+l);
	}
	
	public  void branchFalse(String l){
		emitLn("JNE "+l);
	}
	
	public  void readIt(String name){
		emitLn("CALL READ");
		store(name);
	}
	
	public  void writeIt(){
		emitLn("CALL WRITE");
	}
	
	public  void header(){
		System.out.println("header");
	}
	
	public  void proLog(){
		postLabel("MAIN");
	}
	
	public  void epiLog(){
		emitLn("END MAIN");
	}
	
	public  void allocate(String name,String value){
		System.out.println("allocate "+name+" "+value);
	}
	
	public  void skipComma(){
		skipWhite();
		if(look==','){
			getChar();
			skipWhite();
		}
	}
	
	public  void emit(String s){
		System.out.print("\t"+s);
	}
	
	public  void emitLn(String s){
		emit(s);
		System.out.println("");
	}
	
	
	public  boolean isBoolean(char c){
		if(Character.toUpperCase(c)=='T' || Character.toUpperCase(c)=='F'){
			return true;
		}else{
			return false;
		}
	}
	
	public  boolean getBoolean(){
		char c;
		if(!isBoolean(look)){
			expected("Boolean Literal");
		}
		c=look;
		getChar();
		return Character.toUpperCase(c)=='T';
	}
	

	public  void init(){
		getChar();
		next();
	}
	
}