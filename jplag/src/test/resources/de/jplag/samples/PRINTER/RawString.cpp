#include <iostream>
#include <string> 
using namespace std;

int main()
{
    string normal_str="First line.\nSecond line.\nEnd of message.\n";
    string raw_str=R"(First line.\nSecond line.\nEnd of message.\n)";
    cout<<normal_str<<endl;
    cout<<raw_str<<endl;

    string normal_str="First line.\nSecond line.\nEnd of message.\n";
    string raw_str=R"(First line.\nSecond line.\nEnd of message.\n)";
    cout<<normal_str<<endl;
    cout<<raw_str<<endl;
    return 0;
}