import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CodeGenerator {
    public static void main(String[] args) {
		    // 讀取文件
        if (args.length == 0) {
            System.err.println("請輸入檔案名稱");
            return;
        }
        String fileName = args[0];
        System.out.println("File name: " + fileName);
        String mermaidCode = "";
        try {
            mermaidCode = Files.readString(Paths.get(fileName));
            ClassClassify obj1=new ClassClassify();
            int classNum=obj1.classNames(mermaidCode);

            Parser obj2=new Parser();
            HashMap<Integer,String> classifiedMermaidCode=obj1.classify(mermaidCode);
            
            /*System.out.println(classNum);
            for(int i=0;i<classNum;i++){
                System.out.print(classifiedMermaidCode.get(i));
                System.out.print("\n");
                
            }*/

            String[] outputMermaidCode=new String[classNum];
            for(int i=0;i<classNum;i++){
                outputMermaidCode[i]=obj2.mermadToJava(classifiedMermaidCode, classNum,i);
            }

            /*for(int i=0;i<classNum;i++){
                System.out.print(outputMermaidCode[i]);
                System.out.print("\n");
            }*/

            for(int i=0;i<classNum;i++){
                writeTheFile(obj1.className.get(i),outputMermaidCode[i]);
            }
        }
        catch (IOException e) {
            System.err.println("無法讀取文件 " + fileName);
            e.printStackTrace();
            return;
        } 
    }
    // 寫入文件
    public static void writeTheFile(String output, String content) {
        try {
        output=output.concat(".java");
        File file = new File(output);
        if (!file.exists()) {
             file.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
             bw.write(content);
        }
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
    }
}

class ClassClassify{
    HashMap<Integer,String> className;
    HashSet<String> observedClassNames = new HashSet<>();
    int classNum=0;

    public ClassClassify() {
        this.className = new HashMap<>(); 
    }

    public int classNames(String mermaidCode){  //知道有幾個class且把class名稱存到className[]
        try (BufferedReader reader = new BufferedReader(new StringReader(mermaidCode))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.equals("classDiagram")){
                    continue;
                }
                else if(line.contains("class") && !line.contains("+") && !line.contains("-")){
                    Pattern pattern = Pattern.compile("class\\s*(\\w+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String foundClassName = matcher.group(1);
                        // Only add the class name if it hasn't been observed yet
                        if (!observedClassNames.contains(foundClassName)) {
                            //System.out.println("Matched line: " + line);
                            //System.out.println("Found class name: " + foundClassName);

                            className.put(classNum, foundClassName);
                            observedClassNames.add(foundClassName); // Mark this class name as observed
                            classNum++;
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return classNum;
    }
    
    public HashMap<Integer,String> classify(String mermaidCode){
        HashMap<Integer,String> returnStr=new HashMap<Integer,String>();
        ArrayList<String> classifiedStr0=new ArrayList<String>();
        ArrayList<String> classifiedStr1=new ArrayList<String>();
        ArrayList<String> classifiedStr2=new ArrayList<String>();
//int a=0;
//int b=0;
//int c=0;
        try (BufferedReader reader = new BufferedReader(new StringReader(mermaidCode))) {
            String line;
            boolean contiClass=false;
            boolean repetedApper0=false;
            boolean repetedApper1=false;
            boolean repetedApper2=false;
//System.out.println(classNum);
            int storeClassNum=0;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("\\s+", " ");
                line=line.trim();
                String containLine=new String();
                if(line.contains(":")){    //處理xxx:xxx類型
                    containLine=line.substring(0,line.indexOf(":"));
                    containLine=containLine.trim();
                }
                if(line.equals("classDiagram")){
                    continue;
                }

                for(int i=0;i<classNum;i++){
                    
                    if(line.contains("{")){
                        //處理class xxx{}類型
                        if(line.contains(className.get(i))&& i==0){
                            if(repetedApper0){
                                storeClassNum=i;
                                repetedApper0=true;
                                contiClass=true;
                                break;
                                 
                            }
                            else{
                                line=line.replaceFirst(".$", "");  //remove {
                                classifiedStr0.add(line.replaceAll("(?<=class)(?=[A-Z])", " "));
                                storeClassNum=i;
                                repetedApper0=true;
                                contiClass=true;
                                break;
                            }
                             
                        }
                        else if(line.contains(className.get(i)) && i==1 ){
                            if(repetedApper1){
                                storeClassNum=i;
                                repetedApper1=true;
                                contiClass=true;
                                //a++;
                                break; 
                            }
                            else{
                                line=line.replaceFirst(".$", "");  //remove {
                                classifiedStr1.add(line.replaceAll("(?<=class)(?=[A-Z])", " "));
                                storeClassNum=i;
                                repetedApper1=true;
                                contiClass=true;
                                //c++;
                                break; 
                            } 
                        }
                        else if(line.contains(className.get(i))&& i==2){
                            if(repetedApper2){
                                storeClassNum=i;
                                repetedApper2=true;
                                contiClass=true;
                                break; 
                            }
                            else{
                                line=line.replaceFirst(".$", "");  //remove {
                                classifiedStr2.add(line.replaceAll("(?<=class)(?=[A-Z])", " "));
                                storeClassNum=i;
                                repetedApper2=true;
                                contiClass=true;
                                break; 
                            }
                        }
                    }
                    else if(contiClass){               //if the attribute and method in {}
                        if(line.startsWith("}")){
                            contiClass=false;
                            break;
                        }
                        else if(storeClassNum==0){
                            if(line.contains("(")){
                                line=line.replaceAll("\\s*\\(", "(");
                                line=line.replaceAll("\\(\\s*", "(").replaceAll("\\s*\\)", ")");
                                line=line.replaceAll("\\s*,\\s*", ", ");
                            }
                            if(line.contains("[")){
                                line=line.replaceAll("\\s*\\[", "[") 
                                         .replaceAll("\\[\\s*", "[")  
                                         .replaceAll("\\]\\s*\\[", "][");
                            }
                            if(line.contains("<")){
                                line=line.replaceAll("\\s*<\\s*", "<")
                                         .replaceAll("\\s*>\\s*", ">")
                                         .replaceAll("\\s*,\\s*", ", ")
                                         .replaceAll("(>)(\\w)", "$1 $2");
                            }
                            line=line.replaceAll("([+-])\\s*", "$1");
                            classifiedStr0.add(line);
                            contiClass=true;
                            break;
                        }
                        else if(storeClassNum==1){
                            if(line.contains("(")){
                                line=line.replaceAll("\\s*\\(", "(");
                                line=line.replaceAll("\\(\\s*", "(").replaceAll("\\s*\\)", ")");
                                line=line.replaceAll("\\s*,\\s*", ", ");
                            }
                            if(line.contains("[")){
                                line=line.replaceAll("\\s*\\[", "[") 
                                         .replaceAll("\\[\\s*", "[")  
                                         .replaceAll("\\]\\s*\\[", "][");
                            }
                            if(line.contains("<")){
                                line=line.replaceAll("\\s*<\\s*", "<")
                                         .replaceAll("\\s*>\\s*", ">")
                                         .replaceAll("\\s*,\\s*", ", ")
                                         .replaceAll("(>)(\\w)", "$1 $2");
                            }
                            line=line.replaceAll("([+-])\\s*", "$1");
                            classifiedStr1.add(line);
                            contiClass=true;
                            //b++;
                            break; 
                        }
                        else if(storeClassNum==2){
                            if(line.contains("(")){
                                line=line.replaceAll("\\s*\\(", "(");
                                line=line.replaceAll("\\(\\s*", "(").replaceAll("\\s*\\)", ")");
                                line=line.replaceAll("\\s*,\\s*", ", ");
                            }
                            if(line.contains("[")){
                                line=line.replaceAll("\\s*\\[", "[") 
                                         .replaceAll("\\[\\s*", "[")  
                                         .replaceAll("\\]\\s*\\[", "][");
                            }
                            if(line.contains("<")){
                                line=line.replaceAll("\\s*<\\s*", "<")
                                         .replaceAll("\\s*>\\s*", ">")
                                         .replaceAll("\\s*,\\s*", ", ")
                                         .replaceAll("(>)(\\w)", "$1 $2");
                            }
                            line=line.replaceAll("([+-])\\s*", "$1");
                            classifiedStr2.add(line);
                            contiClass=true; 
                            break;
                        }
                    }
                    else if(line.startsWith("class") && !line.contains("{")){
                        if(line.contains(className.get(i))&& i==0){
                            classifiedStr0.add(line.replaceAll("(?<=class)(?=[A-Z])", " "));
                        }
                        else if(line.contains(className.get(i)) && i==1){
                            classifiedStr1.add(line.replaceAll("(?<=class)(?=[A-Z])", " "));
                        }
                        else if(line.contains(className.get(i))&& i==2){
                            classifiedStr2.add(line.replaceAll("(?<=class)(?=[A-Z])", " "));
                        }
                    }
                    
                    else{
                        if(containLine.equals(className.get(i))&& i==0){
                            if(line.contains("(")){
                                line=line.replaceAll("\\s*\\(", "(");
                                line=line.replaceAll("\\(\\s*", "(").replaceAll("\\s*\\)", ")");
                                line=line.replaceAll("\\s*,\\s*", ", ");
                            }
                            if(line.contains("[")){
                                line=line.replaceAll("\\s*\\[", "[") 
                                         .replaceAll("\\[\\s*", "[")  
                                         .replaceAll("\\]\\s*\\[", "][");
                            }
                            if(line.contains("<")){
                                line=line.replaceAll("\\s*<\\s*", "<")
                                         .replaceAll("\\s*>\\s*", ">")
                                         .replaceAll("\\s*,\\s*", ", ")
                                         .replaceAll("(>)(\\w)", "$1 $2");
                            }
                            line=line.replaceAll("([+-])\\s*", "$1");
                            classifiedStr0.add(line);
                        }
                        else if(containLine.equals(className.get(i))&& i==1){
                            if(line.contains("(")){
                                line=line.replaceAll("\\s*\\(", "(");
                                line=line.replaceAll("\\(\\s*", "(").replaceAll("\\s*\\)", ")");
                                line=line.replaceAll("\\s*,\\s*", ", ");
                            }
                            if(line.contains("[")){
                                line=line.replaceAll("\\s*\\[", "[") 
                                         .replaceAll("\\[\\s*", "[")  
                                         .replaceAll("\\]\\s*\\[", "][");
                            }
                            if(line.contains("<")){
                                line=line.replaceAll("\\s*<\\s*", "<")
                                         .replaceAll("\\s*>\\s*", ">")
                                         .replaceAll("\\s*,\\s*", ", ")
                                         .replaceAll("(>)(\\w)", "$1 $2");
                            }
                            line=line.replaceAll("([+-])\\s*", "$1");
                            classifiedStr1.add(line);
                        }
                        else if(containLine.equals(className.get(i))&& i==2){
                            if(line.contains("(")){
                                line=line.replaceAll("\\s*\\(", "(");
                                line=line.replaceAll("\\(\\s*", "(").replaceAll("\\s*\\)", ")");
                                line=line.replaceAll("\\s*,\\s*", ", ");
                            }
                            if(line.contains("[")){
                                line=line.replaceAll("\\s*\\[", "[") 
                                         .replaceAll("\\[\\s*", "[")  
                                         .replaceAll("\\]\\s*\\[", "][");
                            }
                            if(line.contains("<")){
                                line=line.replaceAll("\\s*<\\s*", "<")
                                         .replaceAll("\\s*>\\s*", ">")
                                         .replaceAll("\\s*,\\s*", ", ")
                                         .replaceAll("(>)(\\w)", "$1 $2");
                            }
                            line=line.replaceAll("([+-])\\s*", "$1");
                            classifiedStr2.add(line);
                        }
                    }
                    
                }
                
            }
                
        }

        catch (IOException e) {
            e.printStackTrace();
        }
//System.out.println(a+" "+b+" "+c);
        returnStr.put(0, String.join("\n", classifiedStr0));
        returnStr.put(1, String.join("\n", classifiedStr1));
        returnStr.put(2, String.join("\n", classifiedStr2));

        return returnStr;
    }

}


class Parser{
    public String mermadToJava(HashMap<Integer,String> str, int classNum, int key) throws IOException{
        try (BufferedReader reader = new BufferedReader(new StringReader(str.get(key).toString()))){

            String line;
            ArrayList<String> modifiedStr=new ArrayList<String>();
            
            while ((line = reader.readLine()) != null) {
                
                line = line.trim();
                if(line.contains("class ")){
                    modifiedStr.add("public "+line+" {");
                }
    
                else if(line.contains("(")){     //method
                    if(line.matches(".*get[A-Z].*")){      //get:this.xxx = xxx
                        if(line.contains("+")){
                            line=line.substring(line.indexOf("+")+1);
                            String[] rearrangeLine=line.split("\\)");
                            String attribute=line.substring(line.indexOf("get")+3,line.indexOf("("));
                            char firstChar=Character.toLowerCase(attribute.charAt(0));
                            if(attribute.length()==1){
                                attribute=String.valueOf(firstChar);
                            }
                            else{
                                attribute=firstChar+attribute.substring(1);
                            } 
                            if (rearrangeLine.length < 2) {
                                rearrangeLine = new String[]{rearrangeLine[0], " void"};
                            }
                            modifiedStr.add("    public"+rearrangeLine[1]+" "+rearrangeLine[0]+") {");
                            modifiedStr.add("        return "+attribute+";");
                            modifiedStr.add("    }");
                        }
                        else if(line.contains("-")){
                            line=line.substring(line.indexOf("-")+1);
                            String[] rearrangeLine=line.split("\\)");
                            String attribute=line.substring(line.indexOf("get")+3,line.indexOf("("));
                            char firstChar=Character.toLowerCase(attribute.charAt(0));
                            if(attribute.length()==1){
                                attribute=String.valueOf(firstChar);
                            }
                            else{
                                attribute=firstChar+attribute.substring(1);
                            } 
                            if (rearrangeLine.length < 2) {
                                rearrangeLine = new String[]{rearrangeLine[0], " void"};
                            }
                            modifiedStr.add("    private"+rearrangeLine[1]+" "+rearrangeLine[0]+") {");
                            modifiedStr.add("        return "+attribute+";");
                            modifiedStr.add("    }");
                       }
                    }
                    else if(line.matches(".*set[A-Z].*")){
                        if(line.contains("+")){
                            line=line.substring(line.indexOf("+")+1);
                            String[] rearrangeLine=line.split("\\)");
                            String attribute=line.substring(line.indexOf("set")+3,line.indexOf("("));
                            char firstChar=Character.toLowerCase(attribute.charAt(0));
                            if(attribute.length()==1){
                                attribute=String.valueOf(firstChar);
                            }
                            else{
                                attribute=firstChar+attribute.substring(1);
                            }                            
                            if (rearrangeLine.length < 2) {
                                rearrangeLine = new String[]{rearrangeLine[0], " void"};
                            }
                            String attribute2[]=rearrangeLine[0].split("\\s|\\w+<\\w+,\\s\\w+>\\s");
                            modifiedStr.add("    public"+rearrangeLine[1]+" "+rearrangeLine[0]+") {");
                            modifiedStr.add("        "+"this."+attribute+" = "+attribute2[1]+";");
                            modifiedStr.add("    }");
                        }
                        else if(line.contains("-")){
                            line=line.substring(line.indexOf("-")+1);
                            String[] rearrangeLine=line.split("\\)");
                            String attribute=line.substring(line.indexOf("set")+3,line.indexOf("("));
                            char firstChar=Character.toLowerCase(attribute.charAt(0));
                            if(attribute.length()==1){
                                attribute=String.valueOf(firstChar);
                            }
                            else{
                                attribute=firstChar+attribute.substring(1);
                            } 
                            if (rearrangeLine.length < 2) {
                                rearrangeLine = new String[]{rearrangeLine[0], " void"};
                            }
                            String attribute2[]=rearrangeLine[0].split("\\s|\\w+<\\w+,\\s\\w+>\\s");
                            modifiedStr.add("    private"+rearrangeLine[1]+" "+rearrangeLine[0]+") {");
                            modifiedStr.add("        "+"this."+attribute+" = "+attribute2[1]+";");
                            modifiedStr.add("    }");
                       }
                    }
                    else{
                        if(line.contains("+")){
                            line=line.substring(line.indexOf("+")+1);
                            String[] rearrangeLine=line.split("\\)");
                            if (rearrangeLine.length < 2) {
                                rearrangeLine = new String[]{rearrangeLine[0], " void"};
                            }
                            if(rearrangeLine[1].contains("String")){
                                modifiedStr.add("    public"+rearrangeLine[1]+" "+rearrangeLine[0]+") {return \"\";}");
                            }
                            else if(rearrangeLine[1].contains("int")){
                                modifiedStr.add("    public"+rearrangeLine[1]+" "+rearrangeLine[0]+") {return 0;}");
                            }
                            else if(rearrangeLine[1].contains("boolean")){
                                modifiedStr.add("    public"+rearrangeLine[1]+" "+rearrangeLine[0]+") {return false;}");
                            }
                            else{
                                modifiedStr.add("    public"+rearrangeLine[1]+" "+rearrangeLine[0]+") {;}");
                            }
                        }
                        else if(line.contains("-")){
                            line=line.substring(line.indexOf("-")+1);
                            String[] rearrangeLine=line.split("\\)");
                            if (rearrangeLine.length < 2) {
                                rearrangeLine = new String[]{rearrangeLine[0], " void"};
                            }
                            if(rearrangeLine[1].contains("String")){
                                modifiedStr.add("    private"+rearrangeLine[1]+" "+rearrangeLine[0]+") {return \"\";}");
                            }
                            else if(rearrangeLine[1].contains("int")){
                                modifiedStr.add("    private"+rearrangeLine[1]+" "+rearrangeLine[0]+") {return 0;}");
                            }
                            else if(rearrangeLine[1].contains("boolean")){
                                modifiedStr.add("    private"+rearrangeLine[1]+" "+rearrangeLine[0]+") {return false;}");
                            }
                            else{
                                modifiedStr.add("    private"+rearrangeLine[1]+" "+rearrangeLine[0]+") {;}");
                            }
                           
                       }
                    }
                }
                else{      //attribtues
                    if(line.contains("+")){
                        line=line.substring(line.indexOf("+")+1);
                        line = line.replaceAll("\s+$", "");
                        modifiedStr.add("    public "+line+";");
                    }
                    else if(line.contains("-")){
                        line=line.substring(line.indexOf("-")+1);
                        line = line.replaceAll("\s+$", "");
                        modifiedStr.add("    private "+line+";");
                   }
                }
                
            }

            modifiedStr.add("}");
            reader.close();
            return String.join("\n", modifiedStr);

        } 
    }
}