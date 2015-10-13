import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
        
        	//Default string if file can't be loaded. (phone development)
        
			s = "(loop)\n@first\nD=M\n@end\nD;JLE\n@second\nD=M\n@result\nM=D+M\n@first\nM=M-1\n@loop\n0;JMP\n@end\n(end)\n0;JMP\n";
        }
		
		s = firstPass(s);
		parser(s);
		
		try{
			
			File f = new File("output.hack");
			PrintStream out = new PrintStream(f);
			out.print(output);
			out.close();
			System.out.println("Saved as \"output.hack\"");
			
		}catch(Exception e){
			System.out.println("Couldn't save file. Printing instead:\n\n" + output);
		}
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
				switch(s.charAt(0)){
				case('A'):
					dest = "100";
				break;
				case('D'):
					dest = "010";
				break;
				case('M'):
					dest = "001";
				break;
				default:
					errorAt(l);
				}
				s = s.substring(s.indexOf('=') + 1);
			}
			
			if(s.contains(";")){//jump
				switch(s.substring(s.indexOf(";") + 1)){
				case("JGT"):
					jump = "001";
				break;
				case("JEQ"):
					jump = "010";
				break;
				case("JGE"):
					jump = "011";
				break;
				case("JLT"):
					jump = "100";
				break;
				case("JNE"):
					jump = "101";
				break;
				case("JLE"):
					jump = "110";
				break;
				case("JMP"):
					jump = "111";
				break;
				default:
					errorAt(l);
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
			
			switch(s){
			case("0"):
				compute = "101010";
			break;
			case("1"):
				compute = "111111";
			break;
			case("-1"):
				compute = "111010";
			break;
			case("D"):
				compute = "001100";
			break;
			case("A"):
				compute = "110000";
			break;
			case("!D"):
				compute = "001101";
			break;
			case("!A"):
				compute = "110001";
			break;
			case("-D"):
				compute = "001111";
			break;
			case("-A"):
				compute = "110011";
			break;
			case("D+1"):
				compute = "011111";
			break;
			case("A+1"):
				compute = "110111";
			break;
			case("D-1"):
				compute = "001110";
			break;
			case("A-1"):
				compute = "110010";//
			break;
			case("D+A"):
				compute = "000010";
			break;
			case("D-A"):
				compute = "010011";
			break;
			case("A-D"):
				compute = "000111";
			break;
			case("D&A"):
				compute = "000000";
			break;
			case("D|A"):
				compute = "010101";
			break;
			default:
				errorAt(l);
			}
			
			printC(ram, compute, dest, jump);
		}
	}
	public static void errorAt(int line){
		p("Error at: " + line);
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