import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Stack;


public class HtmlHighlighterSolution {

	public static void main(String[] args) {
		
		//Get the input and output file names from console (ensure that the files are already in the correct directory)
		Scanner inputFileReader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter a input file name to read from (please add .txt extention): ");
		String inputFileName = inputFileReader.nextLine();
		
		Scanner outputFileReader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter a ouput file name to output result to (please add .txt extention): ");
		String outputFileName = outputFileReader.nextLine();
		
		try {
			String input = getInput(inputFileName);
			input = input.replace("\n", "").replace("\r", "");
			
			//find all tags in current HTML input that do not have closing tags
			HashSet<String> tagsWithNoClosing = findTagsWithNoClosing(input);
			
			String result = solve(input, tagsWithNoClosing);
			//add newLine characters to make it easier to read
			result = result.replace("\\color", "\r\n\\color");
			
			Path file = Paths.get(outputFileName); 
			byte[] buf = result.getBytes();
			Files.write(file, buf);
			System.out.println("Success: Check your results in " + outputFileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//parameters: name of input file
	//purpose: read the input from file and return input as a string
	//assumption: file is small enough to be read into a StringBuilder
	public static String getInput(String inputFileName) throws FileNotFoundException, IOException{
		try(BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    return everything;
		}
	}
	
	//parameters: input HTML string
	//purpose: return all tags that don't have matching closing tags in html string 
	//assumption: html tags closed with <\XX> not <XX/>
	public static HashSet<String> findTagsWithNoClosing(String str){
		HashSet<String> set = new HashSet<String>();
		char[] charArr = str.toCharArray();
		
		for(int i = 0; i < charArr.length; i++){
			if (charArr[i]=='<'){
				int j = i+1;
				//case 1: you have found a closing tag so remove it from the set
				if (charArr[j] == '/'){
					while (j<charArr.length && charArr[j] != '>'){
						j++;
					}
					if (set.contains(str.substring(i+2, j))){
						set.remove(str.substring(i+2, j));
					}
					i=j;
					continue;
				}
				
				//case 2: you have found a opening tag so add it to the set
				while(Character.toString((charArr[j])).matches("([a-zA-Z*]|[\\d*])")){
					j++;
				}
				set.add(str.substring(i+1, j));
			}
		}
		return set;
	}
	
	//parameters: input HTML string
	//purpose: add colors to HTML tags
	//assumption: only using list of 140 CSS colors and assuming HTML content has no comments and rest can be discussed in interview
	public static String solve(String str, HashSet<String> set){
		String[] cssColorList = {"AliceBlue","AntiqueWhite","Aqua","Aquamarine","Azure","Beige","Bisque","Black","BlanchedAlmond","Blue","BlueViolet","Brown","BurlyWood","CadetBlue","Chartreuse","Chocolate","Coral","CornflowerBlue","Cornsilk","Crimson","Cyan","DarkBlue","DarkCyan","DarkGoldenRod","DarkGray","DarkGrey","DarkGreen","DarkKhaki","DarkMagenta","DarkOliveGreen","Darkorange","DarkOrchid","DarkRed","DarkSalmon","DarkSeaGreen","DarkSlateBlue","DarkSlateGray","DarkSlateGrey","DarkTurquoise","DarkViolet","DeepPink","DeepSkyBlue","DimGray","DimGrey","DodgerBlue","FireBrick","FloralWhite","ForestGreen","Fuchsia","Gainsboro","GhostWhite","Gold","GoldenRod","Gray","Grey","Green","GreenYellow","HoneyDew","HotPink","IndianRed","Indigo","Ivory","Khaki","Lavender","LavenderBlush","LawnGreen","LemonChiffon","LightBlue","LightCoral","LightCyan","LightGoldenRodYellow","LightGray","LightGrey","LightGreen","LightPink","LightSalmon","LightSeaGreen","LightSkyBlue","LightSlateGray","LightSlateGrey","LightSteelBlue","LightYellow","Lime","LimeGreen","Linen","Magenta","Maroon","MediumAquaMarine","MediumBlue","MediumOrchid","MediumPurple","MediumSeaGreen","MediumSlateBlue","MediumSpringGreen","MediumTurquoise","MediumVioletRed","MidnightBlue","MintCream","MistyRose","Moccasin","NavajoWhite","Navy","OldLace","Olive","OliveDrab","Orange","OrangeRed","Orchid","PaleGoldenRod","PaleGreen","PaleTurquoise","PaleVioletRed","PapayaWhip","PeachPuff","Peru","Pink","Plum","PowderBlue","Purple","Red","RosyBrown","RoyalBlue","SaddleBrown","Salmon","SandyBrown","SeaGreen","SeaShell","Sienna","Silver","SkyBlue","SlateBlue","SlateGray","SlateGrey","Snow","SpringGreen","SteelBlue","Tan","Teal","Thistle","Tomato","Turquoise","Violet","Wheat","White","WhiteSmoke","Yellow","YellowGreen"};
		int currColorListIndex = 0; 
		
		char[] charArr = str.toCharArray();
		//map tag to color
		HashMap<String, Integer> seenTags = new HashMap<String,Integer>();
		Stack<String> tagStack = new Stack<String>();
		StringBuilder output = new StringBuilder();
		
		
		for (int i = 0; i < charArr.length; i++){
			//if the next character is the beginning of a tag
			if (charArr[i] == '<'){
				int j = i+1;
				//closing tag is found
				if (charArr[j] == '/'){
					//find end of closing tag
					while (j<charArr.length && charArr[j] != '>'){
						j++;
					}
					//case: a closing tag previous to it thus add the color string
					if (charArr[i-1] == '>'){
						String colorAssociatedWithTag = cssColorList[seenTags.get(str.substring(i+2, j))];
						output.append("\\color[" + colorAssociatedWithTag + "]");
					}
					
					//remove tag from stack 
					if (!tagStack.peek().equals(str.substring(i+2, j))){
						return "invalid HTML input";
					}
					else{
						tagStack.pop();
					}
					
					//append the closing tag to the output
					output.append(str.substring(i, j+1));
					i = j;
					continue;
				}
				
				//continue until a tag is found
				while(Character.toString((charArr[j])).matches("([a-zA-Z*]|[\\d*])")){
					j++;
				}
				
				//case 1: opening tag found that we have seen before
				if (seenTags.containsKey(str.substring(i+1, j))){
					String tag = str.substring(i+1, j);
					
					//only add to stack if it will eventually find a closing tag
					if (!set.contains(tag)){
						tagStack.push(tag);
					}
					
					String tagColor = cssColorList[seenTags.get(tag)];
					output.append("\\color[" + tagColor + "]");
					
					//continue until the beginning of a new tag
					while (j<charArr.length && (charArr[j]) != '<'){
						j++;
					}
					output.append(str.substring(i, j));
					i=j-1;
					continue;
				}
				//case 2: opening tag found that we have not seen before
				else{
					
					//assuming that only 140 colors will be needed, a better way would be to pull this data from another data file
					String newTagColor = cssColorList[currColorListIndex];
					String tag = str.substring(i+1, j);
					
					//only add to stack if it will eventually find a closing tag
					if (!set.contains(tag)){
						tagStack.push(tag);
					}
					
					seenTags.put(tag, currColorListIndex);
					currColorListIndex++;
					output.append("\\color[" + newTagColor + "]");
					
					while (j<charArr.length && (charArr[j]) != '<'){
						j++;
					}
					output.append(str.substring(i, j));
					i=j-1;
					continue;
				}	
			}
			//edge case: if the next character is a text not a tag then append color to it based on its parent tag found in stack
			else{
				String tagForThisText = tagStack.peek();
				String newTagColor = cssColorList[seenTags.get(tagForThisText)];
				output.append("\\color[" + newTagColor + "]");
				
				int j = i; 
				while (j<charArr.length && (charArr[j]) != '<'){
					j++;
				}
				output.append(str.substring(i, j));
				i=j-1;
				continue;
			}
		}
		return output.toString();
	}
}
