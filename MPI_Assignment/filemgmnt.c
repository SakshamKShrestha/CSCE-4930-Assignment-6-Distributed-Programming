#include<math.h>

#define HASHTYPE int32_t
//array generating function
int generatevalues(HASHTYPE* array,int arraysize,int rangelimit){
    int i;
    //printf("%d",2<<sizeof(HASHTYPE)-1);
    for(i=0;i<arraysize;i++){
        array[i]=(HASHTYPE)(rand()%rangelimit-rangelimit/2); //rand give too many bits, so need to trim down to what we need
    }
}
//generate array and write to file
int createFile(int arraysize,char* filename){
    int *array = malloc(sizeof(HASHTYPE)* arraysize);
    int i;
    generatevalues(array,arraysize,65536);

    FILE* file = fopen( filename, "w" ); //write byte
    for(i=0;i<arraysize;i++){
    fprintf(file,"%d\n",array[i]); //
    //printf("array[%d]=%d\n",i,array[i]);
    }
    fclose(file);
    return 0;
}
//readfile, into array
int readFile(char* filename,HASHTYPE* array,int arraysize){
    int i;
    FILE* file = fopen( filename, "r" );
    for(i=0;i<arraysize;i++){
    fscanf(file,"%u",&array[i]); //
    //printf("array[%u]=%d\n",i,array[i]);
    }
    fclose(file);
}


//binary mode is more efficient than text mode, However, they are harder to verify the content with normal text editor
int createFile_binMode(int arraysize,char* filename){
    int *array = malloc(sizeof(HASHTYPE) * arraysize);
    int i=0;
    generatevalues(array,arraysize,65536);

    FILE* file = fopen( filename, "wb" ); //write byte
    fwrite( array, 1, sizeof(HASHTYPE) * arraysize, file ); //
    fclose(file);
return 0;
}
//readfile, into array
int readFile_binMode(char* filename,int32_t* array,int arraysize){
    FILE *fileptr;
    char *buffer;
    long filelen;
    fileptr = fopen(filename, "rb");  // Open the file in binary mode
    if(fileptr!=NULL){
        fseek(fileptr, 0, SEEK_END);          // Jump to the end of the file
        filelen = ftell(fileptr);             // Get the current byte offset in the file
        rewind(fileptr);                      // Jump back to the beginning of the file
        fread(array, filelen, 1, fileptr); // Read in the entire file
    }else{
        printf("unable to read input file");
    }
    fclose(fileptr); // Close the file
}
