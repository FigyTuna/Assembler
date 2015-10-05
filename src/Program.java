import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Program {
	
	public static String output = "";
	public static String[] symbols;
	public static int[] table;
	public static int ramTaken = 0;
	public static int spot = 0;
	
	public static void main(String[] args) throws FileNotFoundException{
		
		output = "";
		symbols = new String[100];
		table = new int[100];
		spot = 0;
		String s = "";

		try{
			File f = new File("program.asm");
			Scanner makeString = new Scanner(f);
			while(makeString.hasNextLine()){
				s += makeString.nextLine()+"\n";
			}
			makeString.close();
        }catch(Exception e){
			s = "(loop)\n@first\nD=M\n@end\nD;JLE\n@second\nD=M\n@result\nM=D+M\n@first\nM=M-1\n@loop\n0;JMP\n@end\n(end)\n0;JMP\n";
        }

		
		
		
		s = firstPass(s);
		parser(s);
		
		p(output);
	}
	public static void addSymbol(String symbol, int value){
		//p("Added symbol ("+ symbol +" " + value +")");
		symbols[spot] = symbol;
		table[spot] = value;
		spot++;
	}
	public static String firstPass(String s){
		
		String out = "";
		
		Scanner reader = new Scanner(s);
		int lineNumber = 0;
		
		while(reader.hasNextLine()){
			String next = reader.nextLine();
			if(!removeLine(next, lineNumber)){
				out += next+"\n";
				lineNumber++;
			}
		}
		
		reader.close();
		
		return out;
	}
	public static boolean removeLine(String s, int l){
		if(s.charAt(0) == '(' && s.charAt(s.length()-1) == ')'){
			addSymbol(s.substring(1, s.length()-1), l);
			
			return true;
		}
		return false;
	}
	
	public static void parser(String s){
		
		Scanner reader = new Scanner(s);
		int lineNumber = 1;
		
		while(reader.hasNextLine()){
			translate(reader.nextLine(), lineNumber);
			lineNumber++;
		}
		
		reader.close();
		
	}
	public static void translate(String s, int l){
		if(s.charAt(0) == '@'){//A instruction
			if(s.length() < 2){
				p("Error at: " + l);
			}else{
				try{
					int a = Integer.parseInt(s.substring(1));
					printA(a);
					
				}catch(Exception e){
					boolean done = false;
					for(int i = 0; i < spot; i++){
						if(symbols[i].equals(s.substring(1))){
							printA(table[i]);
							done = true;
							break;
						}
					}
					if(!done){
						addSymbol(s.substring(1), ramTaken);
						ramTaken++;
						printA(table[spot-1]);
					}
				}
			}
		}else{//C instruction
			String ram = "0";
			String compute = "000000";
			String dest = "000";
			String jump = "000";
			
			if(s.contains("=")){//destination
				if(s.charAt(0) == 'A'){
					dest = "100";
				}else if(s.charAt(0) == 'D'){
					dest = "010";
				}else if(s.charAt(0) == 'M'){
					dest = "001";
				}else{
					p("Error at: " + l);
				}
				s = s.substring(s.indexOf('=') + 1);
			}
			
			if(s.contains(";")){//jump
				if(s.substring(s.indexOf(";") + 1).equals("JGT")){
					jump = "001";
				}else if(s.substring(s.indexOf(";") + 1).equals("JEQ")){
					jump = "010";
				}else if(s.substring(s.indexOf(";") + 1).equals("JGE")){
					jump = "011";
				}else if(s.substring(s.indexOf(";") + 1).equals("JLT")){
					jump = "100";
				}else if(s.substring(s.indexOf(";") + 1).equals("JNE")){
					jump = "101";
				}else if(s.substring(s.indexOf(";") + 1).equals("JLE")){
					jump = "110";
				}else if(s.substring(s.indexOf(";") + 1).equals("JMP")){
					jump = "111";
				}else{
					p("Error at: " + l);
				}
				s = s.substring(0, s.indexOf(";"));
			}
			
			if(s.contains("M")){//ram
				ram = "1";
				String temp = "";
				for(int i = 0; i < s.length(); i++){
					if(s.charAt(i) == 'M'){
						temp += 'A';
					}else{
						temp += s.charAt(i);
					}
				}
				s = temp;
			}
			
			if(s.equals("0")){
				compute = "101010";
			}else if(s.equals("1")){
				compute = "111111";
			}else if(s.equals("-1")){
				compute = "111010";
			}else if(s.equals("D")){
				compute = "001100";
			}else if(s.equals("A")){
				compute = "110000";
			}else if(s.equals("!D")){
				compute = "001101";
			}else if(s.equals("!A")){
				compute = "110001";
			}else if(s.equals("-D")){
				compute = "001111";
			}else if(s.equals("-A")){
				compute = "110011";
			}else if(s.equals("D+1")){
				compute = "011111";
			}else if(s.equals("A+1")){
				compute = "110111";
			}else if(s.equals("D-1")){
				compute = "001110";
			}else if(s.equals("A-1")){
				compute = "110010";
			}else if(s.equals("D+A")){
				compute = "000010";
			}else if(s.equals("D-A")){
				compute = "010011";
			}else if(s.equals("A-D")){
				compute = "000111";
			}else if(s.equals("D&A")){
				compute = "000000";
			}else if(s.equals("D|A")){
				compute = "010101";
			}else{
				p("Error at: " + l);
			}
			
			printC(ram, compute, dest, jump);
		}
	}
	public static void printA(int a){
		String toOut = Integer.toBinaryString(a);
		while(toOut.length() < 15){
			toOut = "0"+toOut;
		}
		toOut = "0"+toOut;
		
		output += toOut+"\n";
	}
	public static void printC(String a, String c, String d, String j){
		output += "111" + a + c + d + j + "\n";
	}
	
	public static void p(String s){
		System.out.println(s);
	}
}