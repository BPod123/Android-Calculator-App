package com.example.ben.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layouts textView and set it to the text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
        try {
            String equation = message.replace(" ", "");
                textView.setText(solve(equation));

        } catch (Exception ex) {
            /*for (StackTraceElement e : ex.getStackTrace())
                textView.setText(textView.getText() + e.toString());*/
            textView.setText(message);
            ex.printStackTrace();
        }
    }
    public static String solve(String equation)
    {
        //0. double res = 0;
        //1. Find all indecies of operators
        //2. Find all indecies of higher order operators
        //3. if their is more than 1 operator
        //3A. replace the higher order operators and their operators with METHOD solve(substring of equation form firs operand
        // to the end of the second one + 1
        //3B. -
        //turn the operands into doubles and the solve with the operator.


        //****************************
        //If equation has no operators
      /*
      if(equation.length()<=1)
      {
         return equation;
      }
      if(equation.indexOf("-")<0||equation.indexOf("+")<0||equation.indexOf("*")<0||equation.indexOf("/")<0)
      {
         return equation;
      }*/
        //****************************

        int[] outerOps = new int[2];// Will be set to the operators besides the first higher order operator when aplicable
        int numOps = 0;
        ArrayList<Integer> opIndecies = new ArrayList<Integer>();
        ArrayList<Integer> hOpIndecies = new ArrayList<Integer>();
        if(equation.length()<=1)
            return equation;
        for(int i = 1; i<equation.length(); i++)
        {
            if(isOperator(equation.charAt(i)))
            {
                numOps++;
                opIndecies.add(i);
                if(isHigherOrder(equation.charAt(i)))
                    hOpIndecies.add(i);
            }
        }

        if((opIndecies.get(0)==0&&equation.charAt(opIndecies.get(0))=='-')&&equation.charAt(0)=='-')
            //If the first char is '-' remove (0) from opIndecies
            opIndecies.remove(0);

        if(equation.indexOf("%")>=0)
            return solveWithPercent(equation,opIndecies);
        //PEMDOS section. Solves the equation in order.

        if(numOps>1) {//If there is more than one operator
            if(hOpIndecies.size()>0)
            {//If there is a higher order operator
                if(opIndecies.get(0).equals(hOpIndecies.get(0))){//if the first operator is higher order
                    //The equation = solve(equation substring from 0 to the next operator

                    equation = solve(equation.substring(0,opIndecies.get(1)))+equation.substring(opIndecies.get(1));
                    //Return the result of solve(equation)
                    return solve(equation);
                }
                else//The first higher order operator is not the first operator
                {
                    if(hOpIndecies.get(0)==opIndecies.get(opIndecies.size()-1))
                    {//if the first higher order operator is the last operator

                        //eqaution = equation substring from 0 to the second to last operand (one after last low order operator)
                        // + solve(equation .substring (second to last operand)
                        equation = equation.substring(0,opIndecies.get(opIndecies.size()-2)+1)
                                +solve(equation.substring(opIndecies.get(opIndecies.size()-2)+1));
                        return solve(equation);
                    }
                    else
                    //The first higher order operator is in between other operators
                    {
                        outerOps = findOuterOperators(opIndecies,hOpIndecies.get(0));//Write a method to find the indecies besides the first
                        //higher operator
                        equation = equation.substring(0,outerOps[0]+1)+solve(equation.substring(outerOps[0]+1,outerOps[1]))+
                                equation.substring(outerOps[1]);
                        //equation = substring from beginning to operator before first higher order operator, plus solve(index of first
                        //outer operator to the last outer operater) plus substring from last outer operator to the end of string
                        return solve(equation);
                    }
                }
            }
            else {//There are multiple low order operators and no higher order operators

                equation = solve(equation.substring(0,opIndecies.get(1)))+equation.substring(opIndecies.get(1));
                return solve(equation);


            }
        }
        else
        if(numOps==1)
        {//There is only one operator
            return operate(equation,opIndecies.get(0));//Solves for one operator
        }
        else
            //There are no operators
            return equation;


    }
    public static String solveWithPercent(String equation, ArrayList<Integer> opIndecies)
    {
        if(equation.charAt(opIndecies.get(opIndecies.size()-1))=='%'&&opIndecies.get(opIndecies.size()-1)==equation.length()-1&&opIndecies.size()>1)
        {//if % is the last character and there is more than one operater
            String percent = equation.substring(opIndecies.get(opIndecies.size()-2)+1);//The last operand and percent symbol
            String solvedHalf =solve(equation.substring(0,opIndecies.get(opIndecies.size()-2)));
            String justOperator=equation.substring(opIndecies.get(opIndecies.size()-2),opIndecies.get(opIndecies.size()-2)+1);
            percent = ""+((Double.parseDouble(percent.substring(0,percent.length()-1)))*(0.01)*(Double.parseDouble(solvedHalf)));
            //Percent = (the %'s operand *0.01)*solved half
            equation = solvedHalf+justOperator+percent;//Rebuid equation string with the correct percent operand and % symbol replaced
            //with the correct number for the last operation
            return solve(equation);
            //Solve everything before the second to last operator (counting % as an operator)
            //Then multiply the solved half by 0.01 * percent then combine the strings
        }
        else
        if(equation.indexOf("-")<0&&equation.indexOf("+")<0&&equation.indexOf("s")<0&&equation.indexOf("*")<0&&equation.indexOf("/")<0)
            return ""+0;//If there are no other operatos, return zero
        else
            return "Error: solveWithPercent method in Equation Solver";
    }
    public static String operate(String equation, int opIndex)
    {
        if(equation.charAt(opIndex)=='+')// +
            return ""+(Double.parseDouble(equation.substring(0,opIndex))+Double.parseDouble(equation.substring(opIndex+1)));
        else
        if(equation.charAt(opIndex)=='-')// -
            return ""+(Double.parseDouble(equation.substring(0,opIndex))-Double.parseDouble(equation.substring(opIndex+1)));
        else
        if(equation.charAt(opIndex)=='*')// *
            return ""+(Double.parseDouble(equation.substring(0,opIndex))*Double.parseDouble(equation.substring(opIndex+1)));
        else
        if(equation.charAt(opIndex)=='/')// /
            return ""+(Double.parseDouble(equation.substring(0,opIndex))/Double.parseDouble(equation.substring(opIndex+1)));
        else
        if(equation.charAt(opIndex)=='s')
            return""+(Math.sqrt(Double.parseDouble(equation.substring(0,opIndex))));
        return "Somthing went wrong in operate method.";
    }

    public static int[] findOuterOperators(ArrayList<Integer> opIndecies, int hOpIndex)
    {
        int[] res = new int[2];
        for(int i = opIndecies.get(0); i<opIndecies.get(opIndecies.size()-2); i++)
        {
            if(opIndecies.get(i)<hOpIndex&&opIndecies.get(i+1)==hOpIndex&&opIndecies.get(i+2)>hOpIndex)
            {
                res[0] = opIndecies.get(i);
                res[1] = opIndecies.get(i+2);
                return res;
            }
        }
        return res;//Will return { 0, 0 } if something goes wrong
    }

    public static boolean isHigherOrder(char op)
    {//Determines if a char in the string is a multiplication or division sign{
        return op=='/'||op=='*';
    }
    public static boolean isOperator(char op)
    {
        return op=='/'||op=='*'||op=='+'||op=='-'||op=='s'||op=='%';
    }
    public static void main(String[]args)
    {
        String equation = "5.2";
        System.out.print(solve(equation));
    }

}
