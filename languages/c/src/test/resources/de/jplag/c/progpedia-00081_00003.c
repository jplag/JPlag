#include <stdio.h>
int main (){
  int i=0, j=0, m=0, caminho[10000];

  scanf ("%d", &caminho[i]); //ignore

  while (caminho[i]!=0){
    i++;
    scanf ("%d", &caminho[i]); //ignore
  }

  for (j=0; j<i; j++){
    for (m=0; m<i; m++){
      if (caminho[j]==caminho[m])
        j=m;
    }
    printf ("%d\n", caminho[j]); //ignore
  }
  return 0;
}
