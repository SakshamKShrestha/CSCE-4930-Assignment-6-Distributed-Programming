# MPI_Assignment
This project includes a simple MPI project that will be used as the base for the Distributed Programming Assignment in our Distributed and Cloud Computing course.

#install  libmpich-dev



compile by "mpicc -o mpi_search_All mpi_search_All.c"

basic run on one thread (default value)
    ./mpi_search_All
or customized run (data file, size, number to search for)
    ./mpi_search_All data30000000.txt 30000000 16384

mpi run with 4 threads (with default value)
    mpirun -np 4 ./mpi_search_All

run with slurm on multiple node... you figure that out about slurm mpirun.
https://slurm.schedmd.com/mpi_guide.html
