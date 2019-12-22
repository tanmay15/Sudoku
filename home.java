package com.example.renuka.sudoku;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class home extends AppCompatActivity {
    static Button solve;
    static TextView diffText;
    String id[] = new String[81];
    static EditText editArr[] = new EditText[81];
    static int GlobalCount=0;
    static int change;
    static int BruteForceFlag=0;
    static int diff=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        solve = findViewById(R.id.btnSolve);
        diffText = findViewById(R.id.textView2);

        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solveInitialize();
                String str;
                for(int i=1;i<=81;i++)
                {
                    id[i-1]= "editText"+i;
                }
                int temp;
                int t,m,n;
                int matrix[][] = new int[10][10];
                boolean err= false;
                for(int i=0; i<id.length; i++)
                {
                    m=i/9;
                    n=i%9;
                    temp = getResources().getIdentifier(id[i], "id", getPackageName());
                    editArr[i] = (EditText) findViewById(temp);
                    str=editArr[i].getText().toString();
                    if(str.isEmpty()) {
                        t = 0;
                        editArr[i].setTextColor(Color.rgb(0,136,183));
                        editArr[i].setTextSize(26);
                    }
                    else {
                        t = Integer.parseInt(str);
                        System.out.println("size="+editArr[i].getTextSize());
                        if(t<1 || t>9)
                        {

                            err=true;
                        }
                        editArr[i].setTextColor(Color.rgb(0,0,0));
                        editArr[i].setTextSize(26);
                    }
                    matrix[m+1][n+1]=t;
                }
                if(err) {
                    Toast.makeText(getBaseContext(), "Sudoku should contain numbers from 1 to 9 only", Toast.LENGTH_LONG).show();
                    return;
                }


                for(int q=1;q<=9;q++)
                {
                    for(int w=1;w<=9;w++)
                    {
                        System.out.print(matrix[q][w]+"  ");
                    }
                    System.out.println();
                }
                solution(matrix);


            }// onClick
        });// onClickListener
    } // onCreate

    public void resetMethod(View view)
    {
        int row,column;
        for(int i=1;i<=9;i++)
        {
            for(int j=1;j<=9;j++)
            {
                row = i-1;
                column=j-1;
                editArr[(9*row)+column].setText("");
            }
        }
        for(int i=0;i<81;i++) {
            editArr[i].setTextColor(Color.rgb(0,0,0));
            editArr[i].setTextSize(30);
        }
        diffText.setText("");
        System.out.println("Enabled again");
        solve.setEnabled(true);
        for(int ii=0;ii<editArr.length;ii++)
        {

            editArr[ii].setEnabled(true);
            editArr[ii].setCursorVisible(true);
        }
    }



    public void solveInitialize()
    {
        id = new String[81];
        editArr = new EditText[81];
        GlobalCount=0;
        change=0;
        BruteForceFlag=0;
    }


    public void solution(int matrix[][])
    {
        int i,j,k;
        int nonet[][]= new int[10][10];
        int possArr[][][]=new int[10][10][10];
        int status[][]=new int[10][10];
        int t=0;
        //int matrix[][]= new int[10][10];
        //Scanner sc = new Scanner(System.in);
        //System.out.print("Enter the sudoku\n");
        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                //matrix[i][j]= sc.nextInt();  // this is optional
                if(matrix[i][j]!=0)
                {
                    status[i][j]=1;
                    GlobalCount++;
                }
                if(matrix[i][j]>9 || matrix[i][j]<0)
                {
                    System.out.print("Sudoku should contain numbers from 1 to 9 only\n");
                    System.out.print("Wrong Problem\n");
                    return;
                }
            }
        }
        int res= isValidProblem(matrix,1);
        if(res==0)
        {
            //System.out.print("Solution does not exists!\n");
            Toast.makeText(getBaseContext(), "Solution does not exist!!", Toast.LENGTH_LONG ).show();
            return;
        }

        makeNonets(matrix,nonet,1);  //558
        printMatrix(matrix,1);
        writePossibilities(matrix,possArr,nonet);
        begin(possArr,matrix,status);
        res= isValidProblem(matrix,2);
        if(res==0)
        {
            //System.out.print("Solution does not exists!\n");
            Toast.makeText(getBaseContext(), "Solution does not exist!!", Toast.LENGTH_LONG ).show();
            return;
        }
        printMatrix(matrix,2);
    }

    static int isValidProblem(int matrix[][], int flag)
    {
        int i,j,check;
        if(flag==2 && GlobalCount!=81)
        {
            return 0;
        }
        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                if(matrix[i][j]!=0) {
                    check = doViolatingCheck(matrix, i, j, matrix[i][j]);
                    if (check == 0)
                        return 0;
                }
            }
        }
        return 1;
    }

    static int begin(int possArr[][][],int matrix[][],int status[][])
    {   int a,b,c,d,temp;
        for(;GlobalCount!=81;)
        {
            change=0;
            if(noMorePossibilities(possArr)==1 && GlobalCount!=81)
            {
                return 0;
            }
            a=OnlyOneInBox(possArr,status,matrix);
            if(a==0)
                return 0;
            if(diff<2)
                diff=2;
            b=UniqueInRow(possArr,status,matrix);
            if(b==0)
                return 0;
            c=UniqueInColumn(possArr,status,matrix);
            if(c==0)
                return 0;
            d=UniqueInNonet(possArr,matrix,status);
            if(d==0)
                return 0;

            if(change==0)
            {
                if(diff<3)
                    diff=3;
                LogicalRemoveColumn(possArr);
                LogicalRemoveRow(possArr);
                LogicalNonetRow(possArr);
                LogicalNonetRow(possArr);
            }
            if(change==0)
            {
                if(diff<4)
                    diff=4;
                System.out.print("Brute force method required..  \n");
                BruteForceFlag=1;
                temp=duplicate(possArr,matrix,status);
                if(temp==0)
                    return 0;

            }
        }
        return 1;

    }

    static int noMorePossibilities(int possArr[][][])
    {  int i,j,k;
        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                for(k=1;k<=9 && possArr[i][j][k]!=0;k++)
                {
                    return 0;
                }
            }
        }
        return 1;
    }



    static int OnlyOneInBox(int possArr[][][],int status[][],int matrix[][]) // Only one possibility is present in that cell
    {   int i,j,k,x,m=1998,h;

        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {   x=0;
                if(status[i][j]!=1)
                {
                    for(k=1;k<=9;k++)
                    {
                        if(possArr[i][j][k]!=0)
                        {
                            m=possArr[i][j][k];
                            x++;
                        }
                    }
                    if(x==1)
                    {
                        h=makeChanges(matrix,status,i,j,m,possArr);
                        if(h==0)
                        {return 0;}
                    }
                }
            }
        }

        return 1;
    }

    static int UniqueInRow(int possArr[][][],int status[][],int matrix[][]) // If a number is appearing only once in possibilties of a row
    {  int i,j,k,m,h;
        int[] digitCount=new int[10];

        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                if(status[i][j]!=1)
                {
                    for(k=1;k<10 && possArr[i][j][k]!=0;k++)
                    {
                        digitCount[possArr[i][j][k]]++;
                    }
                }
            }
            for(m=1;m<=9;m++)
            {
                if(digitCount[m]==1)
                {
                    for(j=1;j<=9;j++)
                    {
                        for(k=1;k<=9;k++)
                        {   if(possArr[i][j][k]==m)
                        {   h=makeChanges(matrix,status,i,j,m,possArr);
                            if(h==0)
                            {return 0;}
                        }
                        }
                    }
                }
            }
            resetDigitCount(digitCount);
        }
        return 1;
    }

    static int UniqueInColumn(int possArr[][][],int status[][],int matrix[][]) // If a number is appearing only once in possibilties of a column
    {   int i,j,k,m,h;
        int digitCount[]=new int[10];

        for(j=1;j<=9;j++)
        {
            for(i=1;i<=9;i++)
            {
                if(status[i][j]!=1)
                {
                    for(k=1;k<10 && possArr[i][j][k]!=0;k++)
                    {
                        digitCount[possArr[i][j][k]]++;
                    }
                }
            }
            for(m=1;m<=9;m++)
            {
                if(digitCount[m]==1)
                {
                    for(i=1;i<=9;i++)
                    {
                        for(k=1;k<=9;k++)
                        {   if(possArr[i][j][k]==m)
                        {   h=makeChanges(matrix,status,i,j,m,possArr);
                            if(h==0)
                            {return 0;}
                        }
                        }
                    }
                }
            }
            resetDigitCount(digitCount);
        }
        return 1;
    }

    static void displayPossibilityMatrix(int possArr[][][])
    {   int i,j,k;
        System.out.print("\n\n\nPossibility matrix:\n");
        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                for(k=1;k<=9;k++)
                {   if(possArr[i][j][k]==0)
                {System.out.print("*");
                    break;}
                    System.out.print(possArr[i][j][k]+" ");
                }
                System.out.print(" || ") ;
            }
            System.out.print("\n\n");
        }
    }

    static void writePossibilities(int matrix[][], int possArr[][][], int nonet[][])
    {    int i,j,k,digit;
        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                if(matrix[i][j]==0)
                {   k=0;
                    for(digit=1;digit<=9;digit++)
                    {
                        if(NotInRow(i,matrix,digit) && NotInColumn(j,matrix,digit) && NotInNonet(i,j,matrix,digit, nonet))
                        {
                            k++;
                            InsertInPossibilityOfBlock(possArr,k,i,j,digit);
                        }
                    }
                }
            }
        }
    }

    static boolean NotInRow(int i,int matrix[][],int digit)  // returns 1 if digit is not present in row else returns 0
    {   int j;
        for(j=1;j<=9;j++)
        {
            if(matrix[i][j]==digit)
            {
                return false;
            }
        }
        return true;
    }

    static boolean NotInColumn(int j,int matrix[][],int digit)  // returns 1 if digit is not present in column else returns 0
    {   int i;
        for(i=1;i<=9;i++)
        {
            if(matrix[i][j]==digit)
            {
                return false;
            }
        }
        return true;
    }

    static boolean NotInNonet(int i, int j, int matrix[][],int digit, int nonet[][])  // returns 1 if digit is not present in nonet else returns 0
    {   int k,nn=1998;
        if(i>=1 && i<=3)
        {
            if(j>=1 && j<=3)
                nn=1;
            else if(j>=4 && j<=6)
                nn=2;
            else if(j>=7 && j<=9)
                nn=3;
        }
        else if(i>=4 && i<=6)
        {
            if(j>=1 && j<=3)
                nn=4;
            else if(j>=4 && j<=6)
                nn=5;
            else if(j>=7 && j<=9)
                nn=6;
        }
        else if(i>=7 && i<=9)
        {
            if(j>=1 && j<=3)
                nn=7;
            else if(j>=4 && j<=6)
                nn=8;
            else if(j>=7 && j<=9)
                nn=9;
        }

        for(k=1;k<=9;k++)
        {
            if(nonet[nn][k]==digit)
                return false;
        }
        return true;
    }

    static void InsertInPossibilityOfBlock(int possArr[][][],int k,int i, int j, int digit) // InsertInPossibilityOfCell is better name
    {
        possArr[i][j][k]=digit;
    }



    static void  printMatrix(int matrix[][],int flag)  // printing matrix
    {   int i,j;

        if(flag==1)
            System.out.print("\n\nEntered problem matrix:\n");
        else
            System.out.print("Solution matrix:\n");
        /*for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                System.out.print(matrix[i][j]+"  ");
                if(j%3==0)
                    System.out.print("   ");
            }
            if(i%3==0)
                System.out.print("\n");
            System.out.print("\n");
        }*/
        if(flag==2) {
            int row, column;
            for (i = 1; i <= 9; i++) {
                for (j = 1; j <= 9; j++) {
                    row = i - 1;
                    column = j - 1;


                    editArr[(9 * row) + column].setText(matrix[i][j] + "");
                }
            }
            if (diff == 1)
                diffText.setText("Difficulty level: Easy");
            else if (diff == 2)
                diffText.setText("Difficulty level: Medium");
            else if (diff == 3)
                diffText.setText("Difficulty level: Hard");
            else
                diffText.setText("Difficulty level: Expert");
            System.out.println("Button disabled");
            solve.setEnabled(false);
            System.out.println("Edittext disabled");
            for (int ii = 0; ii < editArr.length; ii++) {

                editArr[ii].setEnabled(false);
                editArr[ii].setCursorVisible(false);

            }
        }

    }


    static void resetDigitCount(int digitCount[])  // making digitcount[] 0
    {   int i;

        for(i=1;i<=9;i++)
        {
            digitCount[i]=0;
        }
    }

    static void modifyPossArr(int r, int c, int dig, int possArr[][][])  // once the number is fixed remove it from possibilities in that row, column and nonet
    {   int i,j,k;

        for(k=1;k<=9;k++)
        {
            possArr[r][c][k]=0;
        }

        //In rows
        for(j=1;j<=9;j++)
        {
            for(k=1;k<=9;k++)
            {
                if(possArr[r][j][k]==dig)
                {
                    removeItFromPossibilities(possArr,r,j,k);
                }
            }
        }

        //In columns
        for(i=1;i<=9;i++)
        {
            for(k=1;k<=9;k++)
            {
                if(possArr[i][c][k]==dig)
                {
                    removeItFromPossibilities(possArr,i,c,k);
                }
            }
        }

        //In nonets
        for(i=(((r-1)/3)*3)+1;i<=(((r-1)/3)*3)+3;i++)
        {
            for(j=(((c-1)/3)*3)+1;j<=(((c-1)/3)*3)+3;j++)
            {
                for(k=1;k<=9;k++)
                {
                    if(possArr[i][j][k]==dig)
                    {
                        removeItFromPossibilities(possArr,i,j,k);
                    }
                }
            }
        }
    }

    //clear
    static void removeItFromPossibilities(int possArr[][][], int r, int c, int k)
    {   int x;

        for(x=k;x<=8;x++)
        {
            if(possArr[r][c][x+1]!=0)
            {
                possArr[r][c][x]=possArr[r][c][x+1];
            }
            else
                break;
        }
        possArr[r][c][x]=0;
    }

    static int UniqueInNonet(int possArr[][][],int matrix[][], int status[][])  // If a number is appearing only once in possibilties of a nonet
    {   int i=0,j=0,k,m,p,temp;
        int a[]={1,3,4,6,7,9};
        int b[]={1,3,4,6,7,9};

        for(p=1;p<=9;p++)
        {
            temp=uni(a[i],a[i+1],b[j],b[j+1],possArr,matrix,status);
            if(temp==0)
            {return 0;}
            if(p%3==0)
            {
                i=i+2;
                j=0;
            }
            else
                j=j+2;
        }
        return 1;
    }


    static int uni(int a,int b,int c,int d,int possArr[][][],int matrix[][], int status[][])
    {   int i,j,k,m;
        int digitCount[]=new int[10];
        for(i=a;i<=b;i++)
        {
            for(j=c;j<=d;j++)
            {
                if(status[i][j]!=1)
                {
                    for(k=1;k<10 && possArr[i][j][k]!=0;k++)
                    {
                        digitCount[possArr[i][j][k]]++;
                    }
                }
            }
        }
        for(m=1;m<=9;m++)
        {
            if(digitCount[m]==1)
            {
                for(i=a;i<=b;i++)
                {
                    for(j=c;j<=d;j++)
                    {
                        if(status[i][j]!=1)
                        {
                            for(k=1;k<=9;k++)
                            {
                                if(possArr[i][j][k]==m)
                                {
                                    if(makeChanges(matrix,status,i,j,m,possArr)==0)
                                    {return 0;}                            }
                            }
                        }
                    }
                }
            }
        }
        resetDigitCount(digitCount);
        return 1;
    }


    //clear
    static int makeChanges(int matrix[][],int status[][],int i,int j,int m,int possArr[][][])
    {
        int temp=8;
        change=1;
        if(BruteForceFlag==1)
            temp=doViolatingCheck(matrix,i,j,m);
        if(temp==0)
        {
            return 0;
        }
        matrix[i][j]=m;
        status[i][j]=1;
        GlobalCount++;
        modifyPossArr(i,j,m,possArr);
        return 1;
    }


    //clear
    static int doViolatingCheck(int matrix[][],int r,int c,int m ) // if violating returns 0 else returns 1
    {   int i,j;
        for(j=1;j<=9;j++)
        {
            if((matrix[r][j]==m && j!=c)|| (matrix[j][c]==m && j!=r))
            {
                return 0;
            }

        }

        for(i=(((r-1)/3)*3)+1;i<=(((r-1)/3)*3)+3;i++)
        {
            for(j=(((c-1)/3)*3)+1;j<=(((c-1)/3)*3)+3;j++)
            {
                if(matrix[i][j]==m && (i!=r && j!=c))
                {
                    return 0;
                }
            }
        }

        return 1;
    }


    //clear
    static void makeNonets(int matrix[][], int nonet[][],int flag)
    {
        int m=1,i=0,j=0,p;

        int a[]={1,3,4,6,7,9};
        int b[]={1,3,4,6,7,9};

        for(p=1;p<=9;p++)
        {
            makeN(a[i],a[i+1],b[j],b[j+1],p,matrix,nonet);
            if(p%3==0)
            {
                i=i+2;
                j=0;
            }
            else
                j=j+2;
        }
    }


    //clear
    static void makeN(int a, int b,int c,int d,int p,int matrix[][], int nonet[][])
    {   int i,j,m=1;
        for(i=a; i<=b;i++)
        {
            for(j=c;j<=d;j++)
            {
                nonet[p][m]=matrix[i][j];
                m++;
            }
        }
    }

    static void LogicalRemoveColumn(int possArr[][][]) // exxplanation is written at the end
    {
        int j,n,i,k,flag=0,x=1998,temp=0,r,c;

        for(j=1;j<=9;j++)
        {
            for(n=1;n<=9;n++)
            {
                flag=0;
                for(i=1;i<=9;i++)
                {
                    temp=0;
                    for(k=1;k<=9 && possArr[i][j][k]!=0;k++)
                    {
                        if(possArr[i][j][k]==n)
                        {
                            flag++;
                            temp=1;
                            x=i;
                            break;
                        }
                    }
                    if(flag>1)
                        break;

                    if(temp==1)
                    {
                        if(flag==1)
                        {
                            if(x>=1 && x<=3)
                                i=3;
                            if(x>=4 && x<=6)
                                i=6;
                            if(x>=7 && x<=9)
                                i=9;
                        }
                    }
                }
                if(flag==1)
                {
                    for(r=(((x-1)/3)*3)+1;r<=(((x-1)/3)*3)+3;r++)
                    {
                        for(c=(((j-1)/3)*3)+1;c<=(((j-1)/3)*3)+3;c++)
                        {
                            for(k=1;k<=9;k++)
                            {
                                if(possArr[r][c][k]==n && c!=j)
                                {
                                    change=1;
                                    removeItFromPossibilities(possArr,r,c,k);
                                }
                            }
                        }
                    }
                }
            }


        }
    }

    static void LogicalRemoveRow(int possArr[][][])
    {
        int j,n,i,k,flag=0,x=1998,temp=0,r,c;

        for(i=1;i<=9;i++)
        {
            for(n=1;n<=9;n++)
            {
                flag=0;
                for(j=1;j<=9;j++)
                {
                    temp=0;
                    for(k=1;k<=9 && possArr[i][j][k]!=0;k++)
                    {
                        if(possArr[i][j][k]==n)
                        {
                            flag++;
                            temp=1;
                            x=j;
                            break;
                        }
                    }
                    if(flag>1)
                        break;

                    if(temp==1)
                    {
                        if(flag==1)
                        {
                            if(x>=1 && x<=3)
                                j=3;
                            if(x>=4 && x<=6)
                                j=6;
                            if(x>=7 && x<=9)
                                j=9;
                        }
                    }
                }
                if(flag==1)
                {
                    for(r=(((i-1)/3)*3)+1;r<=(((i-1)/3)*3)+3;r++)
                    {
                        for(c=(((x-1)/3)*3)+1;c<=(((x-1)/3)*3)+3;c++)
                        {
                            for(k=1;k<=9;k++)
                            {
                                if(possArr[r][c][k]==n && r!=i)
                                {
                                    change=1;
                                    removeItFromPossibilities(possArr,r,c,k);
                                }
                            }
                        }
                    }
                }
            }

        }
    }


    static void LogicalNonetRow(int possArr[][][])
    {
        int i,j,k,q,p,n,flag=0,temp=0,x=1998;
        for(q=1;q<=3;q++)
        {
            for(p=1;p<=3;p++)
            {
                for(n=1;n<=9;n++)
                {
                    flag=0;
                    for(i=((q-1)*3)+1;i<=((q-1)*3)+3;i++)
                    {
                        for(j=((p-1)*3)+1;j<=((p-1)*3)+3;j++)
                        {
                            temp=0;
                            for(k=1;k<=9 && possArr[i][j][k]!=0;k++)
                            {
                                if(possArr[i][j][k]==n)
                                {
                                    flag++;
                                    x=i;
                                    temp=1;
                                    break;
                                }
                            }
                            if(flag>1)
                                break;
                            if(temp==1 && flag==1)
                            {
                                j=((p-1)*3)+3;
                            }
                        }
                    }
                    if(flag==1)
                    {
                        for(j=1;j<=9;j++)
                        {
                            for(k=1;k<=9;k++)
                            {
                                if(possArr[x][j][k]==n && (j<((p-1)*3)+1 && j>((p-1)*3)+3))
                                {
                                    change=1;
                                    //System.out.print("--LogicalNonetRow... removing %d from i=%d j=%d k=%d",possArr[x][j][k],x,j,k);
                                    removeItFromPossibilities(possArr,x,j,k);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static void LogicalNonetColumn(int possArr[][][])
    {
        int i,j,k,q,p,n,flag=0,temp=0,x=1998;

        for(q=1;q<=3;q++)
        {
            for(p=1;p<=3;p++)
            {
                for(n=1;n<=9;n++)
                {
                    flag=0;
                    for(j=((q-1)*3)+1;j<=((q-1)*3)+3;j++)
                    {
                        for(i=((p-1)*3)+1;i<=((p-1)*3)+3;i++)
                        {
                            temp=0;
                            for(k=1;k<=9 && possArr[i][j][k]!=0;k++)
                            {
                                if(possArr[i][j][k]==n)
                                {
                                    flag++;
                                    x=j;
                                    temp=1;
                                    break;
                                }
                            }
                            if(flag>1)
                                break;
                            if(temp==1 && flag==1)
                            {
                                i=((p-1)*3)+3;
                            }
                        }
                    }
                    if(flag==1)
                    {
                        for(i=1;i<=9;i++)
                        {
                            for(k=1;k<=9;k++)
                            {
                                if(possArr[i][x][k]==n && (i<((p-1)*3)+1 && i>((p-1)*3)+3))
                                {
                                    change=1;
                                    removeItFromPossibilities(possArr,i,x,k);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


//Brute Force start

    static int duplicate(int possArr[][][],int matrix[][],int status[][])
    {
        int i,j,x,t,check,k,c,a=1998,b=1998,h,min=1998;
        int dp[][][]= new int[10][10][10];
        int dm[][]=new int[10][10];
        int ds[][]=new int[10][10];

        //Copying of matrix and possArr and status arrays in duplicates
        copy(possArr,dp,dm,matrix,status,ds);

        //Finding cell with minimum possibilities
        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {   c=0;
                for(k=1;k<=9 && possArr[i][j][k]!=0;k++)
                {
                    c++;
                }
                if(c>0 && c<min)
                {
                    min=c;
                    a=i;
                    b=j;
                }

            }
        }
        t=1;

        while(true)
        {
            x=GlobalCount;
            if(dp[a][b][t]==0)
                return 0;
            dm[a][b]=dp[a][b][t];
            h=makeChanges(dm,ds,a,b,dm[a][b],dp);
            check= begin(dp,dm,ds);

            if(check==0)
            {
                t++;
                copy(possArr,dp,dm,matrix,status,ds);
                GlobalCount=x;
            }
            else if(check==1)
            {
                for(i=1;i<=9;i++)
                {
                    for(j=1;j<=9;j++)
                    {
                        matrix[i][j]=dm[i][j];
                    }
                }
                return 1;
            }

        }
    }

    static void copy(int possArr[][][] ,int dp[][][], int dm[][], int matrix[][], int status[][], int ds[][])
    {   int i,j,k;
        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                for(k=1;k<=9;k++)
                {
                    dp[i][j][k]=possArr[i][j][k];
                }
            }
        }
        for(i=1;i<=9;i++)
        {
            for(j=1;j<=9;j++)
            {
                dm[i][j]=matrix[i][j];
                ds[i][j]=status[i][j];
            }
        }
    }

/*
In one column if possibility of a number is lying only in a
single nonet, remove that number from other possibilities of that
nonet

In one row if possibility of a number is lying only in a
single nonet, remove that number from other possibilities of that
nonet

In one nonet if possibility of a number is lying only in a
single row, remove that number from other possibilities of that
row

In one nonet if possibility of a number is lying only in a
single column, remove that number from other possibilities of that
column*/


}// class home

class SudokuSolver {











}

