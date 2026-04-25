import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


class HtmlParser {
    public static TreeMap<Integer, Map<String, Double>> dailyStockPrices = new TreeMap<>();
    public static String[] stockNameArr;
    public static void main(String[] args) {

        //去爬資料存到dailyStockPrices
        if("0".equals(args[0])){
            int day = getDailyStockPrices();
            writeToData(day);
            //System.out.println(dailyStockPrices.lastEntry().getValue());
        }
        
        //task0
        else if(args[1].equals("0")){
            createTreeMap();
            writeToCSV(0, "", "");
        }

        //task1
        else if(args[1].equals("1")){
            String trackStockName = args[2];
            int start = Integer.parseInt(args[3]);
            int end = Integer.parseInt(args[4]);
            createTreeMap();
            String title = trackStockName+","+start+","+end;
            String task1Result = simpleMovingAverage(trackStockName, start, end);
            writeToCSV(1,title,task1Result);
        }

        //task2
        else if(args[1].equals("2")){
            String trackStockName = args[2];
            int start = Integer.parseInt(args[3]);
            int end = Integer.parseInt(args[4]);
            createTreeMap();
            String title = trackStockName+","+start+","+end;
            String task2Result = standardDeviation(trackStockName, start, end);
            writeToCSV(2,title,task2Result);
        }

        //task3
        else if(args[1].equals("3")){
            int start = Integer.parseInt(args[3]);
            int end = Integer.parseInt(args[4]);
            createTreeMap();
            TreeMap<Double,String> task3ResultTreeMap = standardDeviationTop3(start, end);
            String[] task3ResultArr = task3ResultTreeMap.keySet().toString().replaceAll(", ", ",").replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            for(int i=0;i<3;i++){
                if(String.valueOf(task3ResultArr[i].charAt(task3ResultArr[i].length()-1)).equals("0")){
                    task3ResultArr[i] = task3ResultArr[i].substring(0,task3ResultArr[i].length()-2);
                }
            }
            String task3Result = task3ResultArr[0]+","+task3ResultArr[1]+","+task3ResultArr[2];
            String title = task3ResultTreeMap.values().toString().replaceAll(", ", ",").replaceAll("\\[", "").replaceAll("\\]", "")+","+start+","+end;
            writeToCSV(2,title,task3Result);
        }

        //task4
        else if(args[1].equals("4")){
            String trackStockName = args[2];
            int start = Integer.parseInt(args[3]);
            int end = Integer.parseInt(args[4]);
            createTreeMap();
            String title = trackStockName+","+start+","+end;
            String task4Result = linearRegression(trackStockName, start, end);
            writeToCSV(4, title, task4Result);
        }
    }
    public static void writeToData(int day){
        String fileName = "data.csv";
        File file = new File(fileName);
        boolean alreadyExits = file.exists() && file.length()>0;
        try (FileWriter fw = new FileWriter(fileName, true);
            PrintWriter out = new PrintWriter(fw)) {
                //股票代碼
                if(!alreadyExits){
                    if(!dailyStockPrices.isEmpty()){
                        Map<String, Double> firstDayStocks = dailyStockPrices.firstEntry().getValue();
                        int stockSize = firstDayStocks.size();
                        int cnt = 0;
                        out.print("   ");
                        for(String stockName : firstDayStocks.keySet()){
                            out.print(stockName);
                            cnt++;
                            if(cnt < stockSize){
                                out.print(",");
                            }
                        }
                        out.println();
                    }
                    
                }

                // 股票對應的價格
                Map<String, Double> dailyStock = dailyStockPrices.get(day);
                int stockSize = dailyStock.size();
                int cnt = 0;
                out.printf("%2d ",day);
                for(Double price: dailyStock.values()){
                    out.print(String.format("%.2f", price));
                    cnt++;
                    if(cnt<stockSize){
                        out.print(",");
                    }
                }
                out.println();
                fw.close();
                out.close();
            }
            catch (IOException e) {
                System.out.println("An error occurred while writing to CSV file.");
                e.printStackTrace();
            }
    }
    public static void createTreeMap(){
        dailyStockPrices.clear();
        try(BufferedReader br = Files.newBufferedReader(Paths.get("data.csv"))){
            String line;
            boolean headerProcessed = true;
            while((line = br.readLine())!=null){

                if(headerProcessed){
                    stockNameArr = line.trim().split(",");
                    headerProcessed =false;
                }
                else{
                    String[] stockInfor = line.trim().split("\\s+");
                    int day = Integer.parseInt(stockInfor[0]);  //天數
                    String[] prices = stockInfor[1].split(",");

                    Map<String, Double> stockPrices = new LinkedHashMap<>();
                    for(int i=0;i<stockNameArr.length;i++){
                    double price = Double.parseDouble(prices[i]);
                    stockPrices.put(stockNameArr[i],price);
                    }

                    dailyStockPrices.put(day,stockPrices);
                }

            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeToCSV(int task, String title, String printResult){
        String fileName = "output.csv";
        File file = new File(fileName);
        try (FileWriter fw = new FileWriter(fileName, true);
            PrintWriter out = new PrintWriter(fw)) {

                if(task == 0){
                    //寫入output.csv
                    Map<String, Double> firstDayStocks = dailyStockPrices.firstEntry().getValue();
                    int stockSize = firstDayStocks.size();
                    int cnt = 0;
                    for(String stockName : firstDayStocks.keySet()){
                        out.print(stockName);
                        cnt++;
                        if(cnt < stockSize){
                            out.print(",");
                        }
                    }
                    out.println();

                    for(int i=1;i<31;i++){
                        Map<String, Double> dailyStock = dailyStockPrices.get(i);
                        cnt = 0;
                        for(Double price: dailyStock.values()){
                            out.print(String.format("%.2f", price));
                            cnt++;
                            if(cnt<stockSize){
                                out.print(",");
                            }
                        }
                        out.println();
                    }

                    fw.close();
                    out.close();
                }
                else{
                    out.println(title);
                    out.println(printResult);
                }
            }

            catch (IOException e) {
                System.out.println("An error occurred while writing to CSV file.");
                e.printStackTrace();
            }


    }

    public static int getDailyStockPrices(){
        int day = 0;
        try{
                Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();
                Elements stockNames = doc.select("table tbody tr th");
                Elements prices = doc.select("table tbody tr:eq(1) td");
                String title = doc.title();
                day = Integer.parseInt(title.replaceAll("[^0-9]", ""));
                Map<String, Double> stockMap = new LinkedHashMap<>();

                for(int i=0;i<stockNames.size();i++){
                    String stockname = stockNames.get(i).text();
                    Double price = Double.parseDouble(prices.get(i).text());
                    stockMap.put(stockname, price); 
                }

                dailyStockPrices.put(day, stockMap);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return day;
    }

    public static String simpleMovingAverage(String stockName, int start, int end){
        
        ArrayList<String> movingAvg = new ArrayList<>();
        double sum=0.0;
        int cnt=0;

        for(int day = start; day<=end-4; day++){
            cnt=0;
            sum=0;

            for(int j=day;j<day+5;j++){
                Map<String, Double> wantedStock = dailyStockPrices.get(j);
                Double price = wantedStock.get(stockName);
                sum+=price.doubleValue();
                cnt++;

            }
            if(cnt == 5){
                double avg=sum/5.0;
                BigDecimal avgTwoDecimalPoint = new BigDecimal(avg).setScale(2, RoundingMode.HALF_UP);
                String avgTwoDecimalPointStr = String.valueOf(avgTwoDecimalPoint);
                if(String.valueOf(avgTwoDecimalPointStr.charAt(avgTwoDecimalPointStr.length()-1)).equals("0")){
                    avgTwoDecimalPointStr = avgTwoDecimalPointStr.substring(0, avgTwoDecimalPointStr.length()-1);
                    if(String.valueOf(avgTwoDecimalPointStr .charAt(avgTwoDecimalPointStr.length()-1)).equals("0")){
                        avgTwoDecimalPointStr = avgTwoDecimalPointStr.substring(0, avgTwoDecimalPointStr.length()-2);
                    }
                }
                movingAvg.add(avgTwoDecimalPointStr);
            }
        }
        return String.join(",",  movingAvg);
    }

    public static String standardDeviation(String stockName, int start, int end){
        //avg
        double sum=0;
        for(int i=start;i<=end;i++){
            Map<String, Double> wantedStock = dailyStockPrices.get(i);
            sum+=wantedStock.get(stockName).doubleValue();
        }

        //sum of squared deviations
        double avg = sum/(end-start+1);
        double deviationsSum = 0;
        for(int i=start;i<=end;i++){      //求離均差平方和
            Map<String, Double> wantedStock = dailyStockPrices.get(i);
            deviationsSum+= (wantedStock.get(stockName).doubleValue()-avg) * (wantedStock.get(stockName).doubleValue()-avg);
        }
        deviationsSum=deviationsSum/(end-start);  //離均差平方和/(n-1)

        double squaredDeviations =0;
        for(double i=0.000001;i>0;i=i+0.000001){
            if(i*i>deviationsSum){
                squaredDeviations= i-0.000001;
                break;
            }
        }
        BigDecimal squaredDeviationsTwoDecimalPoint = new BigDecimal(squaredDeviations).setScale(2, RoundingMode.HALF_UP);
        String squaredDeviationsTwoDecimalPointStr = String.valueOf(squaredDeviationsTwoDecimalPoint);
        if(String.valueOf(squaredDeviationsTwoDecimalPointStr.charAt(squaredDeviationsTwoDecimalPointStr.length()-1)).equals("0")){
            squaredDeviationsTwoDecimalPointStr = squaredDeviationsTwoDecimalPointStr.substring(0, squaredDeviationsTwoDecimalPointStr.length()-1);
            if(String.valueOf(squaredDeviationsTwoDecimalPointStr.charAt(squaredDeviationsTwoDecimalPointStr.length()-1)).equals("0")){
                squaredDeviationsTwoDecimalPointStr = squaredDeviationsTwoDecimalPointStr.substring(0, squaredDeviationsTwoDecimalPointStr.length()-2);
            }
        }
        return squaredDeviationsTwoDecimalPointStr;

    }
    
    public static TreeMap<Double,String> standardDeviationTop3(int start, int end){
        TreeMap<Double, String> returnTreeMap = new TreeMap<>(Collections.reverseOrder());     //<標準差, stockName>
        Map<String, Double> wantedStock = dailyStockPrices.get(start);
        for(String stockName : wantedStock.keySet()){
            returnTreeMap.put(Double.valueOf(standardDeviation(stockName, start, end)),stockName);
        }

        ArrayList<Double> keysToRemoe= new ArrayList<>();
        int cnt=0;
        for(Double key :returnTreeMap.keySet()){
            cnt++;
            if(cnt>3){
                keysToRemoe.add(key);
            }
        }

        for(Double key: keysToRemoe){
            returnTreeMap.remove(key);
        }
        return returnTreeMap;
    }

    public static String linearRegression(String stockName, int start, int end){
        //時間平均
        double sumTime=0;
        for(double i=start;i<=end;i++){
            sumTime+=i;
        }
        double avgTime=sumTime/(end-start+1);

        //股票價格平均
        double sumStockPrices=0;
        for(int i=start;i<=end;i++){
            Map<String, Double> wantedStock = dailyStockPrices.get(i);
            sumStockPrices+= wantedStock.get(stockName);
        }
        double avgStockPrices= sumStockPrices/(end-start+1);

        //b1：回歸直線的斜率
        double numerator = 0;
        double denominator = 0;

        for(int i=start;i<=end;i++){  //(Yt-avg_Y)
            double stockDeviations =0;
            double timeDeviations=0;
            Map<String, Double> wantedStock = dailyStockPrices.get(i);
            stockDeviations = wantedStock.get(stockName)-avgStockPrices;
            timeDeviations = i-avgTime;

            numerator += timeDeviations * stockDeviations;
            denominator += timeDeviations * timeDeviations;
        }

        double slope = numerator/denominator;
        double intercept = (avgStockPrices)-slope*avgTime;

        BigDecimal slopeTwoDecimalPoint = new BigDecimal(slope).setScale(2, RoundingMode.HALF_UP);
        String slopeTwoDecimalPointStr = String.valueOf(slopeTwoDecimalPoint);
        if(String.valueOf(slopeTwoDecimalPointStr.charAt(slopeTwoDecimalPointStr.length()-1)).equals("0")){
            slopeTwoDecimalPointStr = slopeTwoDecimalPointStr.substring(0, slopeTwoDecimalPointStr.length()-1);
            if(String.valueOf(slopeTwoDecimalPointStr.charAt(slopeTwoDecimalPointStr.length()-1)).equals("0")){
                slopeTwoDecimalPointStr = slopeTwoDecimalPointStr.substring(0, slopeTwoDecimalPointStr.length()-2);
            }
        }

        BigDecimal interceptTwoDecimalPoint = new BigDecimal(intercept).setScale(2, RoundingMode.HALF_UP);
        String interceptTwoDecimalPointStr = String.valueOf(interceptTwoDecimalPoint);
        if(String.valueOf(interceptTwoDecimalPointStr.charAt(interceptTwoDecimalPointStr.length()-1)).equals("0")){
            interceptTwoDecimalPointStr = interceptTwoDecimalPointStr.substring(0, interceptTwoDecimalPointStr.length()-1);
            if(String.valueOf(interceptTwoDecimalPointStr.charAt(interceptTwoDecimalPointStr.length()-1)).equals("0")){
                interceptTwoDecimalPointStr = interceptTwoDecimalPointStr.substring(0, interceptTwoDecimalPointStr.length()-2);
            }
        }
        
        String returnString = slopeTwoDecimalPointStr+","+interceptTwoDecimalPointStr;
        return returnString;
    }
}

