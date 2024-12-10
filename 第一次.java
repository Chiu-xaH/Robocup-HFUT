package org.example;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;



public class Main {
    //提取括号用
    private static final Pattern PATTERN = Pattern.compile("\\(([^)]+)\\)");
    //see用的
    private static final Pattern PATTERN2 = Pattern.compile("\\((see)\\s(\\d+)\\s(.*)\\)\\)");
    //()()()...提取括号内容并组成数组
    public static String[] parseBrackets(String input) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int level = 0; // 层次
        for (char c : input.toCharArray()) {
            if (c == '(') {
                if (level > 0) {
                    current.append(c);
                }
                level++;
            } else if (c == ')') {
                level--;
                if (level > 0) {
                    current.append(c);
                } else {
                    result.add(current.toString());
                    current.setLength(0); // 清空
                }
            } else {
                if (level > 0) {
                    current.append(c);
                }
            }
        }
        return result.toArray(new String[0]);
    }
    //类似Kotlin的SubStringAfter函数,返回delimiter往后的字符
    public static String subStringAfter(String input, String delimiter) {
        int index = input.indexOf(delimiter);
        if (index == -1) {
            return ""; //找不到分隔符
        }
        int sub = index + delimiter.length();
        return input.substring(sub);
    }
    //写文件
    public static Boolean writeFile(String data) {
         //追加数据append=true
         try (FileWriter writer = new FileWriter("OutPut.txt",true)) {
            writer.write(data+"\n");
            return true;
         } catch (IOException e) {
            e.printStackTrace();
            return false;
         }
    }
    public static void parse(String str) {
        //当传递滚来的是hear
        if(str.contains("hear")) {
            //去括号
            Matcher matchers = PATTERN.matcher(str);
            String res = null;
            while (matchers.find()) {
                res = matchers.group(1);
            }
            //匹配后补加)
            res += ")";
            //也可以用subString直接提取1,len-1
            //res = str.substring(1,str.length()-1);
            String[] list = res.split(" ");

            String parseStr = "在 "+ list[1] + " 周期 hear 从 "+ list[2]+" 方向 听到了 "+list[3];
            //输出、保存
            System.out.println(parseStr);
            writeFile(parseStr);
        } else if(str.contains("see")) { /*当传递过来的是see*/
            // 正则去括号 (ball)->ball
            Matcher matcher = PATTERN2.matcher(str);
            String cycle = null,people = null;
            while (matcher.find()) {
                cycle = matcher.group(2);
                people = matcher.group(3);
            }
            //正则匹配后，补回末尾括号
            people += ")";

            String[] peopleList = parseBrackets(people);
            StringBuilder parseStr = new StringBuilder("在 " + cycle + " 周期 see 从 " + " ");

            for (String item : peopleList) {
                //正则表达式 提取括号内的名字
                Matcher matchers = PATTERN.matcher(item);
                String name = null;
                while (matchers.find()) {
                    name = matchers.group(1);
                }

                parseStr.append(name).append(" 距离我的");

                //拿到(名字)后面的内容，也就是若干个参数，使用空格分割
                String[] itemList = subStringAfter(item, ") ").split(" ");
                //一个标签数组，追加字符串，到达itemList结尾为止
                String[] labels = {"Direction", "Distance", "DirChng", "DistChng", "BodyDir", "HeadDir"};
                for (int j = 0; j < itemList.length && j < labels.length; j++) {
                    if(j == 0) {
                        //第一次追加的时候不加逗号
                        parseStr.append(" ");
                    } else {
                        parseStr.append(", ");
                    }
                    parseStr.append(labels[j]).append(" 是 ").append(itemList[j]);
                }
                //到下一个人
                parseStr.append(";");
            }
            System.out.println(parseStr);
            writeFile(parseStr.toString());
        } else {
            System.out.println("格式有误");
        }
    }

    public static void main(String[] args) {
      //  parse("(hear 1022 -30 passto(23,24))");
        //parse("(see 1022 ((ball) -20 20 1 -2)((player hfut1 2) 45 23 0.5 1 22 40)((goal r) 12 20))");
        //parse("(see 1022 ((soal r) 12 20 22))");
        org.example.Car car = new org.example.Car();
        car.setPrice(100.0);
        car.setColor("WHITE");
        car.printPrice();
        car.printColor();
    }
}