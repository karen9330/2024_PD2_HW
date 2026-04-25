import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RegExp {
    
    public static void main(String[] args) {
        String str1 = args[1];
        String str2 = args[2];
        int s2Count = Integer.parseInt(args[3]);

        //For your testing of input correctness
        System.out.println("The input file:"+args[0]);
        System.out.println("str1="+str1);
        System.out.println("str2="+str2);
        System.out.println("num of repeated requests of str2 = "+s2Count);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line;
            boolean isemty=true;
            while ((line = reader.readLine()) != null) {
                //You main code should be invoked here
                isemty=false;
                char ans1='N';
                char[] data=line.toCharArray();

                for(int i=0;i<data.length;i++){
                    if(data[i]>='A' && data[i]<='Z'){
                        data[i]=(char)((int)data[i]+32);
                    }
                }

                //first request
                boolean request1=true;
                
                for(int i=0;i<data.length/2;i++){
                    if(data[i]!=data[data.length-1-i]){
                        request1=false;
                        break;
                    }
                }
                if(request1)
                    ans1='Y';
                else    
                    ans1='N';

            //second request
            char[] str1_arr=str1.toCharArray();
            char ans2='N';
            
            int find=0;
            for(int i=0;i<data.length-str1_arr.length+1;i++){
               for(int j=0;j<str1_arr.length;j++){
                    if(data[i+j]==str1_arr[j]){
                        find++;
                    }
                    else{
                        find=0;
                        break;
                    }
                    if(find==str1_arr.length){
                        ans2='Y';
                    }
                }
                  
            }

            //third request
            int cnt=0;
            char ans3='N';
            int match=0;
            char[] str2_arr=str2.toCharArray();

            for(int i=0;i<data.length-str2_arr.length+1;i++){
                for(int k=0;k<str2_arr.length;k++){
                    if(data[i+k]==str2_arr[k]){
                        match++;
                    }
                    else{
                        match=0;
                        break;
                    }
                    if(match==str2_arr.length){
                        match=0;
                        cnt++;
                    }
                }
            }
            if(cnt>=s2Count){
                ans3='Y';
            }

            // forth request
            int conti_a=0;
            int conti_b=0;
            char ans4='N';
            for(int i=0;i<data.length;i++){
                if(data[i]=='a'){
                    conti_a++;
                    for(int k=0;(i+k)<data.length;k++){
                        if(data[i+k]=='b'){
                            conti_b++;
                            if(conti_b==2*conti_a){
                                ans4='Y';
                                break;
                            }
                        }
                        else
                            conti_b=0;
                    }
                }
                else
                    conti_a=0;
                
            }
            
            //print
            System.out.println(ans1+","+ans2+","+ans3+","+ans4);        
            }
            if(isemty)
                System.out.println("N,N,N,N The file is empty!");
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}