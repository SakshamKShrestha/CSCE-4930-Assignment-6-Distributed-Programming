#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include "filemgmnt.c"

#define HASHTYPE int32_t

char *FILENAME = "data3000000.txt";
size_t arraysize = 3000000; //if read from file only, must match the input file's size
int32_t hashveluetolookfor=21598;


int main(int argc, char **argv)
{

    int *array;
    int rank=0,size;
    MPI_Status status;
    MPI_Request request;
    int found,myfound,inrange,nvalues;
    int i,j,dummy_send,dummy_recv=0;

    /* Let the system do what it needs to start up MPI */
    MPI_Init(&argc,&argv); //initialize
    MPI_Comm_rank(MPI_COMM_WORLD,&rank); //get my rank (my ID)
    MPI_Comm_size(MPI_COMM_WORLD,&size); //get size (of the cluster)
    myfound=0;
    if (rank == 0) //if i'm head node
    {
        //command line parameters
        if(argc==1){ //no parameters
            printf("using all default values\n");
		}else if(argc==2){ //one parameter
			hashveluetolookfor=atoi(argv[1]);
            printf("set number to search to %d\n",hashveluetolookfor);
        }else if(argc==4){    // full custom run
            FILENAME=argv[1]; // 
            arraysize=atoi(argv[2]); //
            hashveluetolookfor=atoi(argv[3]);
        }else{
            printf("usage: either provide no arguments,\n or provide 1 (as new hashcode to look for)\n or provide 3 in format of \n filename size valuetolookfor.\n running with default values\n");
            
        }
    }

    //start the work
    MPI_Bcast(&arraysize, 1, MPI_INT, 0, MPI_COMM_WORLD); //broadcase array size (int) from rank 0 to everyone
    array=malloc(sizeof(int)*arraysize); //create array to that size
    if (rank == 0) //if i'm head node
    {
            //createFile(arraysize,FILENAME); // (disabled) create a new input file, and generate its value, see filemgmnt.c
            readFile(FILENAME,array,arraysize); //read an input file into array
            printf("P%d: finish reading data, about to forward to everyone\n", rank);
    }
    MPI_Bcast(array, arraysize, MPI_INT, 0, MPI_COMM_WORLD); //broadcase actual array from rank 0 to everyone
    MPI_Bcast(&hashveluetolookfor, 1, MPI_INT, 0, MPI_COMM_WORLD); //broadcase hashcodetolookfor from rank 0 to everyone   
    
    //MPI_Recv block the execution until it get the data, MPI_IRecv do not block.
    MPI_Irecv(&dummy_recv, 1, MPI_INT, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &request);


    nvalues = arraysize / size;                                        //EACH PROCESS RUNS THAT MUCH NUMBER IN ARRAY
    i = rank * nvalues;                                                 //OFFSET FOR EACH PROCESS INSIDE THE ARRAY
    inrange = (i <= ((rank + 1) * nvalues - 1) && i >= rank * nvalues); //check limit of the offset
    printf("P%d: I search in range %d to %d\n", rank, rank*nvalues,(rank + 1) * nvalues - 1);
    while (inrange)
    {
        if (array[i] == hashveluetolookfor)
        {
            dummy_send = i;
            for (j = 0; j < size; j++) //send to everyone that we found the search pattern!
            {
                if(j!=rank){ // don't send to myself
                MPI_Send(&dummy_send, 1, MPI_INT, j, 1, MPI_COMM_WORLD);
                }
            }
            printf("P%d: I found the secret code at global index %d\n", rank, i);
            myfound = 1;
        }
        //printf("P:%d - %d -  %d\n", rank, i, array[i]); //print out each hash value
        MPI_Test(&request, &found, &status); //check if &request is completed.
        if(found){
            printf("P%d: someone found secret code at global index %d\n", rank, dummy_recv);        
            MPI_Irecv(&dummy_recv, 1, MPI_INT, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &request);
        }
        ++i;
        inrange = (i <= ((rank + 1) * nvalues - 1) ); //check limit of the offset
    }
    printf("P%d: stop my search at global index %d\n", rank, i - 1);
    MPI_Finalize();
}
