#include <bits/stdc++.h>
using namespace std;
struct candidates{
    int key;
    float number_vote;
};
bool sorter(candidates a,candidates b)
{
    if(a.number_vote!=b.number_vote) return a.number_vote<b.number_vote;
    return a.key<b.key;

}
int main()
{
    map<string,float> voter;
    map<string,float>::iterator it;
    map<int,string> candidate;
    vector<candidates> cndt;
    string name,adress,cnp,series;
    int frauda=0;
    float tvoturi=0;
    int nrc=0;
    ifstream in("evidenta.csv");
    while(in.good())
    {
        getline(in,name,',');
        getline(in,adress,',');
        getline(in,cnp,',');
        getline(in,series,'\n');

        voter.insert (pair<string,int>(cnp,0));
    }
    char comanda;
    cin>>comanda;
    while(comanda!='*')
    {
    switch (comanda) {
        case '+' :
    
        {
            string cnp_voter;
            cin>>cnp_voter;
            string numecand,prencand;
            cin>>numecand>>prencand;
            candidates can;
            string numec=numecand+" "+prencand;
             it = voter.find(cnp_voter);
                if((((cnp_voter.compare("50005")>0) && cnp_voter.compare("6")<0) || (cnp_voter.compare("60005")>0)) && (it!=voter.end()))
                    {
                        cout<<"Persoana minora"<<endl;
                    }
               else
                {
                    it = voter.find(cnp_voter);
                if(it!= voter.end())
                        {
                        if(voter[cnp_voter]==0)
                            {
                             voter[cnp_voter]++;
                             int ok=-1;
                             for(int i=0;i<nrc;i++)
                             {
                                 if(candidate[cndt[i].key]==numec) ok=i;
                             }
                               if(ok!=-1)
                                {
                                cndt[ok].number_vote++;
                                }
                             else
                                {
                                candidate.insert (pair<int,string>(nrc,numec));
                                can.number_vote=1;
                                can.key=nrc;
                                cndt.push_back(can);
                                nrc++;
                                 }

                            tvoturi++;
                            }
                        else
                            {
                            cout<<"Vot deja inregistrat"<<endl;
                            frauda++;
                            }

                     }
                else    {
                        cout<<"CNP invalid"<<endl;
                        frauda++;
                         }

                }


        }
        break;

        case '?':
            {
        cout<<"Statistici"<<'\n';
        cout<<"=========="<<'\n';
        sort(cndt.begin(),cndt.end(),sorter);
        for(int i=0;i<nrc;i++)
        {
            cout<<candidate[cndt[i].key]<<": "<<cndt[i].number_vote<<" voturi (";
            cout<<fixed<<setprecision(2)<<(cndt[i].number_vote/tvoturi)*100<<"%)"<<endl;
        }
        cout<<"Incercari de frauda: "<<frauda<<endl;


            }
            break;
    }
         cin>>comanda;

    cout<<"Statistici"<<'\n';
        cout<<"=========="<<'\n';
          sort(cndt.begin(),cndt.end(),sorter);
           for(int i=0;i<nrc;i++)
        {
            cout<<candidate[cndt[i].key]<<": "<<cndt[i].number_vote<<" voturi (";
            cout<<fixed<<setprecision(2)<<(cndt[i].number_vote/tvoturi)*100<<"%)"<<endl;
        }
        cout<<"Incercari de frauda: "<<frauda<<endl;;
    return 0;
}
}
