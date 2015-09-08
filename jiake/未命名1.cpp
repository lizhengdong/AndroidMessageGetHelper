#include<iostream>
#include <windows.h> 
using namespace std;


void P(char book[],char testname[],int key,char test[])//将程序写入数组 
{
    int c,ch,i;
    FILE *fp,*fp_test;
    //srand(key);  //初始化随机数发生器
	if((fp=fopen(book,"ab+"))==NULL)  
    { 
       cout<<"can't open";                                       
    }
    if((fp_test=fopen(testname,"rb"))==NULL)  
    { 
       cout<<"can't open";                                       
    }
    fseek(fp,0,SEEK_SET);
	fseek(fp_test,0,SEEK_SET);
    i=0;
	fprintf(fp,test);
	c=fgetc(fp_test);
	fprintf(fp, "0x%.2x", c);
    while((c=fgetc(fp_test))!=EOF)
    {
         ch=rand();
         //c=ch^c;  //加密 
		 fprintf(fp, ",0x%.2x", c);

    }
	fprintf(fp,"};");
    fclose(fp);
	fclose(fp_test);
} 

int main()
{
	char testname1[100]="AndroidKernelService.apk";
    //char testname2[100]="Result.apk";
    //char testname3[100]="selfdelete.exe";


	int key1=901006;
    //int key2=901007;
    //int key3=901008;
    
    //将可执行程序写到数组中
    P("book.h",testname1,key1,"char test1[]={"); 
	//P("book.h",testname2,key2,"char test2[]={");
	//P("book.h",testname3,key3,"char test3[]={");
	return 0;
}

