package com.compiler;

public class Main {
	
	public static void main(String[] args){
		Parser p=new Parser(); 
				
		p.init();
		//Parser.assignment();
		//Parser.doProgram();
		//Parser.boolExpression();
		p.matchString("PROGRAM");
		p.header();
		p.topDecls();
		p.matchString("BEGIN");
		p.proLog();
		p.block();
		p.matchString("END");
		p.epiLog();
	}

}
