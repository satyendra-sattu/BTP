package com.compiler;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class Utils {
	
	
	static int lCount =0;
	static int nEntry =0;
	
	static char look;      //look ahead
	static char token;     //encoded token
	static String value;	//unencoded token
	
	static final int maxEntry=100;
	
	static String[] sT=new String[maxEntry];
	static char[] sType=new char[maxEntry];
	
	static final int nKW=9;
	static final int nKW1=10;
	
	static final String[] kwList={"IF","ELSE","ENDIF","WHILE","ENDWHILE","READ","WRITE","VAR","END"};
	static final String kwCode="xileweRWve";
	
	public static void printError(String msg){
		System.out.println(msg);
	}
	
	public static void getChar(){
		try {
			look=(char)System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
			abort("Error reading character");
		}
	}
	
	public static void abort(String msg){
		printError(msg);
		System.exit(1);
	}
	
	public static void expected(String msg){
		abort(msg+" Expected");
	}
	
	public static void undefined(String n){
		abort("Undefined Identifier "+n);
	}
	
	public static void duplicate(String n){
		abort("Duplicate Identifier "+n);
	}
	
	public static void checkIdent(){
		if(token!='x'){
			expected("Identifier");
		}
	}

	public static void matchChar(char x){
		if(look==x){
			getChar();
			skipWhite();
		}
		else
			expected("'"+x+"'");
	}
	
	public static boolean isAlpha(char x){
		char x1=Character.toUpperCase(x);
		if(x1<='Z' && x1>='A')
			return true;
		return false;
	}
	
	public static boolean isDigit(char x){
		if(x<='9' && x>='0')
			return true;
		return false;
	}
	
	public static boolean isAlphaNumeric(char c){
		return isAlpha(c) || isDigit(c);
	}
	
	public static boolean isAddop(char c){
		if(c=='+' || c=='-'){
			return true;
		}
		return false;
	}
	
	public static boolean isMulop(char c){
		if(c=='*' || c=='/'){
			return true;
		}
		return false;
	}
	
	public static boolean isOrOp(char c){
		if(c=='|' || c=='~'){
			return true;
		}
		return false;
		
	}
	
	public static boolean isRelOp(char c){
		if(c=='=' || c=='#' || c=='<' || c=='>'){
			return true;
		}
		return false;
	}
	
	
	public static boolean isWhite(char c){
		if(c==' ' || c=='\t' || c=='\n')
			return true;
		return false;
	}
	
	public static void skipWhite(){
		while(isWhite(look))
			getChar();
	}
	
	public static int lookup(String[] T,String s,int n){
		int i;
		boolean found=false;
		i=n;
		while(i>0 && !found){
			if(s.equalsIgnoreCase(T[i]))
				found=true;
			else
				i--;
		}
		return i;
	}
	
	public static int locate(String n){
		return lookup(sT, n, nEntry);
	}
	
	public static boolean inTable(String n){
		return lookup(sT, n, nEntry)!=0;
	}
	
	public static void checkTable(String n){
		if(!inTable(n))
			undefined(n);
	}
	
	public static void checkDup(String n){
		if(inTable(n))
			duplicate(n);
	}
	
	public static void addEntry(String n,char t){
		checkDup(n);
		if(nEntry==maxEntry){
			abort("System Table Full");
		}
		nEntry++;
		sT[nEntry]=n;
		sType[nEntry]=t;
	}
	
//	public static boolean isOp(char c){
//		if(c=='+' || c=='-' || c=='*' || c=='/' || c=='<' || c=='>' || c==':' || c=='='){
//			return true;
//		}
//		return false;
//	}
	
//	public static void fin(){
//		if(look=='\n')
//			getChar();
//		skipWhite();
//	}
	
	public static void getName(){
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
	
	public static void getNum(){
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
	
	public static void getOp(){
		skipWhite();
		token=look;
		value=look+"";
		getChar();
	}
	
	public static void next(){
		skipWhite();
		if(isAlpha(look))
			getName();
		else if(isDigit(look))
			getNum();
		else
			getOp();
	}
	
	public static void scan(){
		if(token=='x')
			token=kwCode.charAt(lookup(kwList, value, nKW)+1);
	}
	
	public static void matchString(String x){
		if(!value.equalsIgnoreCase(x)){
			expected("'"+x+"'");
		}
		next();
	}
	
	
	
	
	public static String newLabel(){
		String s;
		s="L"+lCount;
		lCount++;
		return s;
	}
	
	public static void postLabel(String label){
		System.out.println(label+":");
	}
	
	public static void clear(){
		emitLn("XOR EAX,EAX");
	}
	
	public static void negate(){
		emitLn("NEG EAX");
	}
	
	public static void notIt(){
		emitLn("NOT EAX");
	}
	
	public static void loadConst(String n){
		emitLn("MOV EAX,"+n);
	}
	
	public static void loadVar(String name){
		if(!inTable(name))
			undefined(name);
		emitLn("MOV EAX,["+name+"]");
	}
	
	public static void push(){
		emitLn("PUSH EAX");
	}
	
	public static void popAdd(){
		emitLn("POP EBX");
		emitLn("ADD EAX,EBX");
	}
	
	public static void popSub(){
		emitLn("POP EBX");
		emitLn("SUB EAX,EBX");
		emitLn("NEG EAX");;
	}
	
	public static void popMul(){
		emitLn("POP EBX");
		emitLn("IMUL EAX,EBX");
	}
	
	public static void popDiv(){
		emitLn("POP EBX");
		emitLn("IMUL EAX,EBX");
	}
	
	public static void popAnd(){
		emitLn("POP EBX");
		emitLn("AND EAX,EBX");
	}
	
	
	public static void popOr(){
		emitLn("POP EBX");
		emitLn("OR EAX,EBX");
	}
	
	public static void popXor(){
		emitLn("POP EBX");
		emitLn("XOR EAX,EBX");
	}
	
	public static void popCompare(){
		emitLn("POP EBX");
		emitLn("CMP EAX,EBX");
	}
	
	public static void setEqual(){
		emitLn("if equal");
	}
	
	public static void setNEqual(){
		emitLn("if not equal");
	}
	
	public static void setGreater(){
		emitLn("if greater");
	}
	
	public static void setLess(){
		emitLn("if less");
	}
	
	public static void setLessOrEqual(){
		emitLn("if less or equal");
	}
	
	public static void setGreaterOrEqual(){
		emitLn("if greater or equal");
	}
	
	public static void store(String Name){
		emitLn("MOV ["+Name+"],EAX");
	}
	
	public static void branch(String l){
		emitLn("JMP "+l);
	}
	
	public static void branchFalse(String l){
		emitLn("JNE "+l);
	}
	
	public static void readIt(String name){
		emitLn("CALL READ");
		store(name);
	}
	
	public static void writeIt(){
		emitLn("CALL WRITE");
	}
	
	public static void header(){
		System.out.println("header");
	}
	
	public static void proLog(){
		postLabel("MAIN");
	}
	
	public static void epiLog(){
		emitLn("END MAIN");
	}
	
	public static void allocate(String name,String value){
		System.out.println("allocate "+name+" "+value);
	}
	
	public static void skipComma(){
		skipWhite();
		if(look==','){
			getChar();
			skipWhite();
		}
	}
	
	public static void emit(String s){
		System.out.print("\t"+s);
	}
	
	public static void emitLn(String s){
		emit(s);
		System.out.println("");
	}
	
	
	public static boolean isBoolean(char c){
		if(Character.toUpperCase(c)=='T' || Character.toUpperCase(c)=='F'){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean getBoolean(){
		char c;
		if(!isBoolean(look)){
			expected("Boolean Literal");
		}
		c=look;
		getChar();
		return Character.toUpperCase(c)=='T';
	}
	

	public static void init(){
		getChar();
		next();
	}
	
}